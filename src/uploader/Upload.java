package uploader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import Utils.FFMpegUtil;
import Utils.HttpsOpenUtil;
import Utils.JR;

public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Upload.class); 
	/**
	 * Constructor of the object.
	 */
	public Upload() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("【上传】开始uploder");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		
		//int issuccess=0;
		int statusCode=0;
		int partFinish=0;
		PrintWriter pw = null;
		try {
			String path = request.getParameter("path");
			path = path != null ? java.net.URLDecoder.decode(path, "utf-8"):"";
			boolean isMultipart=true;
		
			if (isMultipart) {
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);

				//得到所有的表单域，它们目前都被当作FileItem
				List<FileItem> fileItems = upload.parseRequest(request);
				
				
				/**
				 InputStream inputStream=request.getInputStream();
		         String hash =request.getParameter("hash");
				 String fileName = request.getParameter("name");
				 String fid=request.getParameter("fid");
				 String ext=request.getParameter("ext");
				 // 如果大于1说明是分片处理
				 int chunks = Integer.parseInt(request.getParameter("chunks"));
				 int chunk = Integer.parseInt(request.getParameter("chunk"));
				 String access_token = request.getParameter("access_token");
				 */
		         

		        String hash = "";
				String fileName = "";
				//String fileRealName = "";
				String fid="";
				String ext="";
				// 如果大于1说明是分片处理
				int chunks = 1;
				int chunk = 0;
				String access_token = "";
				FileItem tempFileItem = null;
				
				for (FileItem fileItem : fileItems) {
					if (fileItem.getFieldName().equals("hash")) {
						hash = fileItem.getString();
					} else if (fileItem.getFieldName().equals("name")) {
						fileName = new String(fileItem.getString().getBytes(
								"ISO-8859-1"), "UTF-8");
						fileName = fileName.replaceAll(" ", "");
					} else if (fileItem.getFieldName().equals("chunks")) {
						chunks = NumberUtils.toInt(fileItem.getString());
					} else if (fileItem.getFieldName().equals("chunk")) {
						chunk = NumberUtils.toInt(fileItem.getString());
					} else if (fileItem.getFieldName().equals("access_token")) {
						access_token = fileItem.getString();
					} else if (fileItem.getFieldName().equals("file")) {
						tempFileItem = fileItem;
					}else if (fileItem.getFieldName().equals("fid")) {
						fid = fileItem.getString();
					}else if (fileItem.getFieldName().equals("ext")) {
						ext = fileItem.getString();
					}
				}


				String filePath=null;
				String imagePath=null;
				String tempPath=null;
				String tempFilePath=null;
				Jedis jd = null;
				try{
					jd=JR.getJd();
					filePath=jd.get("v:upload:fileRealPath:"+access_token+":"+fid);
					imagePath=jd.get("v:upload:imageRealPath:"+access_token+":"+fid);
					tempFilePath=jd.get("v:upload:fileQueuePath:"+access_token+":"+fid);
				}catch(Exception e){
					log.info(fileName+"【上传】redis-->获取失败:",e);
					if(statusCode==0)statusCode=600;
				}finally{
					JR.close(jd);
				}
				
				if(filePath==null||"".equals(filePath)){return;}
				
				File realFile = new File(tempFilePath);
				tempPath=this.getServletContext().getRealPath("/")+"media/temp/"+access_token+"/"+hash+"/";
				tempPath=tempPath.replaceAll("//","/");
				//String realPartName=MD5Util.string2MD5(access_token+"@split@"+hash);//+i;
				//String realName=realPartName+ext;
				log.info("【上传】tempFilePath="+tempFilePath);
				log.info("【上传】tempPath="+tempPath);
				
				
				// 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台(默认每片为5M)
				File tempFileDir = new File(tempPath);
				if (!tempFileDir.exists()) {
					tempFileDir.mkdirs();
				}

				File tempPartFile = new File(tempFileDir.getPath(), hash
						+ "_" + chunk + ".part");
				FileUtils.copyInputStreamToFile(tempFileItem.getInputStream(),
						tempPartFile);
				//FileUtils.copyInputStreamToFile(inputStream,
				//		tempPartFile);
				
				//单个分片成功;
				boolean uploadDone = true;
				for (int i = 0; i < chunks; i++) {
					File partFile = new File(tempFileDir.getPath(), hash
							+ "_" + i + ".part");
					if (!partFile.exists()) {
						uploadDone = false;
					}
				}
				
				Thread.sleep(20);
				
				File ft=new File(tempPartFile.getPath());
				if(!ft.exists()){
					statusCode=600;
				}else{
					if(!uploadDone){
						if(statusCode==0)statusCode=200;
					}
				}
				
				// 所有分片文件都上传完成
				// 将所有分片文件合并到一个文件中
				if (uploadDone) {
					log.info("【上传】所有分片上传成功，将所有分片合并到一个文件中");
					Thread.sleep(200);
					partFinish=1;
					File lockFile =new File(tempFileDir.getPath()+realFile.getName()+".lock");
					
					if(!lockFile.exists()){
						lockFile.createNewFile();
					}
					
					RandomAccessFile fi = new RandomAccessFile(lockFile, "rw");  
			        FileChannel fc = fi.getChannel();  
			        FileLock fl=null;
			        try{
			        	fl=fc.tryLock();
			        	if(fl==null){
			        	    return;
			        	}
			        }catch(Exception e){
			        	log.error("tryLock出错：",e);
			        	if(fl!=null) fl.release();
			        	return;
			        }	
			        
					
					for (int i = 0; i < chunks; i++) {
						File partFile = new File(tempFileDir.getPath(), hash
								+ "_" + i + ".part");
					
						FileOutputStream destTempfos = new FileOutputStream(
								realFile, true);
						
						FileUtils.copyFile(partFile, destTempfos);
						destTempfos.flush();
						destTempfos.close();
						//优化为只删最后分片
						if(i==chunks-1){
							try{
								partFile.delete();
							}catch(Exception e){
								log.error("【上传】"+fileName+"删除最后分片失败:",e);
								FileUtils.deleteDirectory(tempFileDir);
							}
							//FileUtils.deleteDirectory(partFile);
						}
					}
					
					// 删除临时目录中的分片文件
					//FileUtils.deleteDirectory(tempFileDir);
					try{
						if(fl!=null){
						fl.release();
						fl=null;
						}
						if(fc!=null){
							fc.close();
							fc=null;
						}
					
					lockFile.delete();
					lockFile=null;
					}catch(Exception e){
						log.error("【上传】lock文件删除失败:"+fileName,e);
					}
				
			     log.info("【上传】文件合并完成！");
				//文件合并完成后;
				String config = null;
				String fileUrl=null;
				String imageUrl=null;
				try{
					jd=JR.getJd();
					config = jd.get("v:upload:config:" + access_token);
					fileUrl=jd.get("v:upload:fileUrl:"+access_token+":"+fid);
					imageUrl=jd.get("v:upload:imageUrl:"+access_token+":"+fid);
				}catch(Exception e){
					log.error("【上传】redis-->获取失败:"+fileName,e);
					if(statusCode==0)statusCode=600;
				}finally{
					JR.close(jd);
				}
				
				if(null==config)config="{}";
				Map configMap=new HashMap();
				JSONObject jo = JSONObject.fromObject(config);
			
				try {
					configMap.put("bit_rate_v", jo.get("bit_rate_v"));
					configMap.put("codec_name_v", jo.get("codec_name_v"));
					configMap.put("bit_rate_s", jo.get("bit_rate_s"));
					configMap.put("codec_name_s", jo.get("codec_name_s"));
					configMap.put("resolving", jo.get("resolving"));
				} catch (Exception e) {
					if(statusCode==0)statusCode=600;
					log.error("【上传】configMap失败:",e);
				}
			    
				//生成图片
				String data=tempFilePath +"@postdataWithCdnPassWordTruth@"+imagePath
						+"@postdataWithCdnPassWordTruth@"+"320@postdataWithCdnPassWordTruth@240";
				if(ext.toLowerCase().indexOf("mp4")!=-1){
				    //new GearClient().(data);
					log.info("【上传】MP4截图："+data);
					boolean flag=FFMpegUtil.getFFMpgeUtil().createPhoto(tempFilePath, imagePath, 320,240);
				}
				
				log.info("【上传】获取信息1--->"+filePath+":"+fileName);
			    //获取信息	
				String msgFinish="0";
				Map map=FFMpegUtil.getFFMpgeUtil().getMessage(tempFilePath);
				if(map==null||map.size()<2){
					msgFinish="2";
				}else{
					msgFinish="1";
				}
				
				//相对路径
				log.info("【上传】获得信息2--->"+map+":"+fileName);
				map.put("imgurl", imageUrl.replaceAll("//","/"));
				map.put("realname", realFile.getName());
				map.put("url", fileUrl.replaceAll("//","/"));
				String myname="";
				if(fileName.indexOf("."+ext)!=-1){
					myname=fileName.split("."+ext)[0];
				}
				
				map.put("basename", myname.length()>60?myname.substring(0,60):myname);
				JSONObject result = JSONObject.fromObject("{}");
				result.putAll(map);
				String keyquq="v:upload:queue:" + access_token + ":" + fid;
				
				//存入redis队列 result 额外信息
				JSONObject tempMap=JSONObject.fromObject("{}");
				try {
					jd = JR.getJd();
					//String jt=jd.get(keyquq);
					tempMap.putAll(JSONObject.fromObject(jd.get(keyquq)));
					//信息比较
					Map map2=FFMpegUtil.compareConfig(tempMap, map);
					
					result.putAll(map2);
					tempMap.putAll(result);
					tempMap.put("status_file",2);
					jd.setex(keyquq, 60 * 60 * 24,tempMap.toString());
				} catch (Exception e) {
					log.error("【上传】redis-->获取失败",e);
					if(statusCode==0)statusCode=600;
				} finally {
					JR.close(jd);
				}
				
				
				if(tempMap.get("error")!=null&&!"".equals(tempMap.get("error"))){
					try{
						File f2=new File(imagePath);
						if(realFile.exists())realFile.delete();
						if(f2.exists()&&imagePath.indexOf(".jpg")>10)f2.delete();
						if(tempFileDir.exists())tempFileDir.delete();
					}catch(Exception e2){
						log.error("【上传】删除失败:",e2);
					}
				}
				
				//发送接口入库
				/*try{
					if(tempMap.get("error")==null||"".equals(tempMap.get("error"))){
					
						String backUrl=tempMap.getString("back_url");
						if(backUrl.indexOf("\\?")>1){
							backUrl=backUrl+"?key="+keyquq;
						}else{
							backUrl=backUrl+"&key="+keyquq;
						}
						long t1=System.currentTimeMillis();
						String value=sendUrl(backUrl, "key="+keyquq);
						long t2=System.currentTimeMillis();
						if(value!=null&&value.toLowerCase().indexOf("success")!=-1){
							tempMap.put("issuccess",1);
							if(statusCode==0)statusCode=200;
							log.info("成功回调,耗时-"+(t2-t1)+"ms:"+fileName+":"+backUrl+"-->"+keyquq);
						}else{
							log.info("回调失败,耗时-"+(t2-t1)+"ms:"+fileName+":"+backUrl+"-->"+keyquq+"--返回值为:"+value);
							if(statusCode==0)statusCode=600;
						}
					
					}else{
						try{
							File f2=new File(imagePath);
							if(realFile.exists())realFile.delete();
							if(f2.exists()&&imagePath.indexOf(".jpg")>10)f2.delete();
							if(tempFileDir.exists())tempFileDir.delete();
						}catch(Exception e2){
							e2.printStackTrace();
						}
					}
				}catch(Exception e){
					log.info("回调失败:"+fileName+":"+tempMap.get("back_url")+"-->"+keyquq);
					log.error(e.getMessage());
					if(statusCode==0)statusCode=600;
				}*/
				
				tempMap.put("issuccess",1);
				tempMap.put("msgFinish", 1);
				log.info("【上传】token:"+fid+"---->"+fileName+":"+tempMap.toString());
				log.info("【上传】上传完成返回页面数据："+tempMap.toString());
				pw = response.getWriter();
				pw.write(tempMap.toString());
				
			}
			/*else{// 临时文件创建失败
				if (chunk == chunks - 1) {
					FileUtils.deleteDirectory(tempFileDir);
					// ResponseUtil.responseFail(response, "500", "内部错误");
				}
			}*/
			}
			/*if(issuccess!=1&&partFinish==1){
				 response.setStatus(500);
			}*/
			if(statusCode==0){
				statusCode=200;
			}
			response.setStatus(statusCode);
//		} catch (IOFileUploadException e) {
//			 log.error("【上传】IOFileUploadException:",e);
//			 response.setStatus(600);
//			// ResponseUtil.responseFail(response, "500", "内部错误");
//		
			}catch(Exception e){
			 log.error("【上传】Exception",e);
			 response.setStatus(600);
		}finally{
			if(pw!=null){
				pw.flush();
				pw.close();
			}
		}
		
		log.info("【上传】结束uploader");
	}
	

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * 发送URL
	 * @param doUrl
	 * @param paramStr
	 * @return
	 * @throws Exception
	 */
	private String sendUrl(String doUrl, String paramStr)throws Exception{
		if(doUrl.indexOf("http://")==0){
			//参数
	        StringBuffer recieveData = new StringBuffer();
	        String recieveLine = "";
	        String recieveString = "";
	        
	        //初始化 地址放到配置文档中
	        java.net.URL url = new java.net.URL(doUrl);
	        java.net.URLConnection con = url.openConnection();
	        con.setUseCaches(false);
	        con.setDoOutput(true);
	        con.setDoInput(true);
	        
	        //发送
	        BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
	        outWriter.write(paramStr);
	        outWriter.flush();
	        outWriter.close();
	        
	        //获取服务器端返回信息
	        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
	        while ((recieveLine = in.readLine()) != null){
	            recieveData.append(recieveLine);
	        }
	        in.close();
	      
	        recieveString = recieveData.toString();
	        //返回数据
	        return recieveString;
		}else if(doUrl.indexOf("https://")==0){
			byte[]b=null;
			
			try{
				b=HttpsOpenUtil.post(doUrl, paramStr, "UTF-8");
			}catch(Exception e){
				log.error("sendUrl:"+doUrl+";失败:"+doUrl,e);
			}
			
			if(b!=null){
				return new String(b, "UTF-8");
			}else return "";
		}else{
			return "";
		}
    }
	
	public void init() throws ServletException {
	}

	// 合并文件
	public static void main(String args[]) throws Exception {
		FileChannel outChannel = null;
		/*
		 * File f=new File("E:/up"); File[] fs=f.listFiles();
		 * 
		 * String name=fs[0].getName(); String fName="E:/be/"+name; outChannel =
		 * new FileOutputStream(fName).getChannel(); for(int
		 * i=0;i<fs.length;i++){
		 * 
		 * File ff=new File("E:/up/酒.mp4_"+i+".mp4");
		 * System.out.println(ff.getName()); FileChannel fc = new
		 * FileInputStream(ff).getChannel(); ByteBuffer bb =
		 * ByteBuffer.allocate(1000*1000); while(fc.read(bb) != -1){ bb.flip();
		 * outChannel.write(bb); bb.clear(); } fc.close(); } if (outChannel !=
		 * null) {outChannel.close();}
		 */
		String str = "酒副本 - 副本2.mp4";
		System.out.println(str.replaceAll(" ", ""));
		
	}
}

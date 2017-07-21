package uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
		super.destroy(); 
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
				 * 二进制
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
		         
				String hash="";
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
				
				// 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台(默认每片为5M)
				File tempFileDir = new File(tempPath);
				if (!tempFileDir.exists()) {
					tempFileDir.mkdirs();
				}

				File tempPartFile = new File(tempFileDir.getPath(), hash+ "_" + chunk + ".part");
				FileUtils.copyInputStreamToFile(tempFileItem.getInputStream(),tempPartFile);
				
				//tempPartFile.delete();//测试上传切片为空情况使用
				if (tempPartFile!=null&&tempPartFile.length()!=0) {
				}else{
					tempPartFile.delete();
					throw new Exception("上传切片为空,报异常重新请求上传。问题切片:"+tempFileDir.getPath()+"/"+hash+"_"+chunk+".part");
				}
				
				//单个分片成功;
				boolean uploadDone = true;
				for (int i = 0; i < chunks; i++) {
					File partFile = new File(tempFileDir.getPath(), hash+ "_" + i + ".part");
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
					log.info(tempFileDir.getPath()+realFile.getName()+".lock");
					
					if(!lockFile.exists()){
						lockFile.createNewFile();
					}
					
					RandomAccessFile fi = new RandomAccessFile(lockFile,"rw");  
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
						File partFile = new File(tempFileDir.getPath(),hash+ "_" + i + ".part");
						FileOutputStream destTempfos = new FileOutputStream(realFile,true);
						
						log.info("【上传】分片合并partFile="+partFile+",分片大小:"+partFile.length());
						Long long1=FileUtils.copyFile(partFile, destTempfos);
						log.info("【上传】合并拷贝完成的分片大小:"+long1);
						
						destTempfos.flush();
						destTempfos.close();
						
						//合并完成后,删除最后分片
						if(i==chunks-1){
							try{
								partFile.delete();
							}catch(Exception e){
								log.error("【上传】"+fileName+"删除最后分片失败:",e);
								FileUtils.deleteDirectory(tempFileDir);
							}
						}
					}
					
					log.info("【上传】文件合并完成！");
					
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
				
				if(null==config){config="{}";}
				
				Map<String,Object> configMap=new HashMap<String,Object>();
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
//				String data=tempFilePath +"@postdataWithCdnPassWordTruth@"+imagePath
//						+"@postdataWithCdnPassWordTruth@"+"320@postdataWithCdnPassWordTruth@240";
				
				if(ext.toLowerCase().indexOf("mp4")!=-1){
					FFMpegUtil.getFFMpgeUtil().createPhoto(tempFilePath, imagePath, 320,240);
				}
				
			    //获取信息	
				Map<String,Object> map=FFMpegUtil.getFFMpgeUtil().getMessage(tempFilePath);
				
				//相对路径
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
					tempMap.putAll(JSONObject.fromObject(jd.get(keyquq)));
					
					//信息比较
					Map<String,Object> map2=FFMpegUtil.compareConfig(tempMap, map);
					
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
				
				//因为需要测试上传失败合并后视频是否正确，因此注释掉,请勿删除
//				if(tempMap.get("error")!=null&&!"".equals(tempMap.get("error"))){
//					try{
//						log.info("【上传】文件比较error，删除文件,imagePath="+imagePath+";realFile:"+realFile.getName()+",路径:"+realFile.getPath());
//						log.info("【上传】tempFileDir="+tempFileDir.getPath()+",name="+tempFileDir.getName());
//						
//						File f2=new File(imagePath);
//						log.info("【上传】截图是否存在:"+f2.exists());
//						log.info("【上传】视频是否存在:"+tempFileDir.exists());
//						
//						if(realFile.exists())realFile.delete();
//						if(f2.exists()&&imagePath.indexOf(".jpg")>10)f2.delete();
//						if(tempFileDir.exists())tempFileDir.delete();
//					}catch(Exception e2){
//						log.error("【上传】删除失败:",e2);
//					}
//				}
				
				tempMap.put("issuccess",1);
				tempMap.put("msgFinish",1);
				pw = response.getWriter();
				pw.write(tempMap.toString());
			}
		
			}
	
			if(statusCode==0){
				statusCode=200;
			}
			response.setStatus(statusCode);
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
	
	public void init() throws ServletException {
	}
}

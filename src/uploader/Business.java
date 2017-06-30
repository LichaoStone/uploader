package uploader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;


import redis.clients.jedis.Jedis;
import Utils.FFMpegUtil;
import Utils.FileUtil;
import Utils.GearClient;
import Utils.HttpsOpenUtil;
import Utils.JR;
import Utils.MD5Util;
import Utils.PropertiesUtil;
import Utils.SqlSession;
import instock.Instock;

/**
 * 上传业务逻辑类
 * @作者 lichao
 * @时间 2016年10月12日 下午5:55:44
 * @说明
 */ 
public class Business extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Business.class); 
	/**
	 * Constructor of the object.
	 */
	public Business() {
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
		log.info("【开始】Business.doGet");
        
        response.setContentType("text/html");
		String token = request.getParameter("access_token");
		String fid = request.getParameter("fid");
		String process=request.getParameter("process");
		String chname="";
		
		log.info("token="+token);
		log.info("fid="+fid);
		log.info("process="+process);
		
		if(process==null){
			log.info("【开始】process为空");
			
			Jedis jd = null;
			String chuncksval=",";
			String realPartName=MD5Util.string2MD5(token+"@split@"+fid);
			String imageName=realPartName+".jpg";
			String mediaName=realPartName+"."+request.getParameter("ext");
			String rootPath = this.getServletContext().getRealPath("/");
			String filename=request.getParameter("name");
			String baseFolderPath ="media/temp/"+token + "/";
			String imageFolderPath="image/"+token+"/";
		
			int size=0;
			Map comannds=new HashMap();
			
			if(filename!=null){
			    filename=filename.replaceAll(" ", "");
			}else 
				filename=fid;
		
		try {
			String jobj=null;
			try{
				jd = JR.getJd();
				jobj = jd.get("v:upload:config:" + token);
			}catch(Exception e){
				log.error(filename+"--->加载第一次失败");
				Thread.sleep(2000);
				if(jd==null){
					jd=JR.getJd();
					jobj = jd.get("v:upload:config:" + token);
				}else if(jobj==null||"".equals(jobj)){
					jobj = jd.get("v:upload:config:" + token);
				}else{
					JR.close(jd);
					jd=null;
					jd = JR.getJd();	
					jobj = jd.get("v:upload:config:" + token);
				}
			}
			
			if(jobj==null||"".equals(jobj)){
				Thread.sleep(2000);
				if(jd==null){
					jd=JR.getJd();
				}	
				jobj = jd.get("v:upload:config:" + token);
			}
			JR.close(jd);
			
			log.error("---【jobj】---"+jobj);
			JSONObject jo=JSONObject.fromObject(jobj);
			try {
				if (jo.getString("path") != null
						&& !jo.getString("path").equals("")) {
					baseFolderPath = jo.getString("path");
				}
				
				if (jo.getString("imgepath") != null
						&& !jo.getString("imgepath").equals("")) {
					imageFolderPath = jo.getString("imgepath");
				}
				
				//查看断点分片
				String tempP=rootPath+"media/temp/"+token+"/"+request.getParameter("hash");
			
				File f=new File(tempP);
				
				if(f.exists()){
					for(File ff:f.listFiles()){
						String name=ff.getName();
						name=name.split("_")[1];
						name=name.split("\\.")[0];
						chuncksval+=name+",";
						size+=ff.length();
					}
				}
				
				String quePath=rootPath+"media/temp/"+token+"/"+request.getParameter("hash")+"-queue";
				File folder=new File(quePath);
				if(!folder.exists())folder.mkdirs();
				comannds.put("v:upload:fileQueuePath:"+token+":"+fid,quePath+"/"+mediaName);
				if(mediaName.indexOf(".mp3")!=-1&&((mediaName.lastIndexOf(".mp3")+4))==mediaName.length())
				mediaName=mediaName.substring(0, mediaName.lastIndexOf(".mp3"))+".m4a";
				comannds.put("v:upload:fileRealPath:"+token+":"+fid,rootPath+baseFolderPath+mediaName);
				comannds.put("v:upload:imageRealPath:"+token+":"+fid,rootPath+imageFolderPath+imageName);
				comannds.put("v:upload:fileTempPath:"+token+":"+fid,rootPath+"media/temp/"+token+"/"+request.getParameter("hash"));
				comannds.put("v:upload:fileUrl:"+token+":"+fid,baseFolderPath+mediaName);
				comannds.put("v:upload:imageUrl:"+token+":"+fid,imageFolderPath+imageName);
				
				//jd.setex("v:upload:fileRealPath:"+token+":"+fid, 60*60*24,rootPath+baseFolderPath+mediaName);
				//jd.setex("v:upload:imageRealPath:"+token+":"+fid, 60*60*24,rootPath+imageFolderPath+imageName);	
				//jd.setex("v:upload:fileTempPath:"+token+":"+fid, 60*60*24,rootPath+"media/temp/"+token+"/"+request.getParameter("hash"));
				//jd.setex("v:upload:fileUrl:"+token+":"+fid, 60*60*24,baseFolderPath+mediaName);
				//jd.setex("v:upload:imageUrl:"+token+":"+fid, 60*60*24,imageFolderPath+imageName);
				
			} catch (Exception e) {
				log.error("【redis取值】失败:",e);
			}
			
			File file =new File(rootPath+baseFolderPath);
			if(!file.exists())file.mkdirs();
			file=null;
			file=new File(rootPath+imageFolderPath);
			if(!file.exists())file.mkdirs();
			file=null;
			
			log.error("jo.get(chname)"+jo.get("chname"));
			chname=jo.get("chname")==null?"":jo.get("chname").toString();
			
			jo.put("status_file", 1);
			
			/*jd.setex("v:upload:queue:" + token + ":" + fid, 60 * 60 * 24,
					jo.toString());
			*/
			comannds.put("v:upload:queue:" + token + ":" + fid,
					jo.toString());		
			
		} catch (Exception e) {
			log.error("加载失败:filename="+filename,e);
		} finally {
			JR.close(jd);
		}
		
		Jedis jdPut=null;
		try{
			jdPut=JR.getJd();
			for(Object key:comannds.keySet()){
				String me=key.toString();
				String val=comannds.get(key).toString();
				jdPut.setex(me, 60 * 60 * 24, val);
			}
		}catch(Exception et){
			log.error(et);
			
			try {
				Thread.sleep(2000);
				JR.close(jdPut);
				jdPut=JR.getJd();
				for(Object key:comannds.keySet()){
					String me=key.toString();
					String val=comannds.get(key).toString();
					jdPut.setex(me, 60 * 60 * 24, val);
					}
				} catch (InterruptedException e) {
					JR.close(jdPut);
					log.error(filename+"初始化失败:",e);
				}finally{
					JR.close(jdPut);
				}
			
		}finally{
			JR.close(jdPut);
		}
		
		String[] fids = fid.split("_");
		String backid = fids[0] + "_" + fids[1] + "_" + fids[2];
		JSONObject obj = JSONObject.fromObject("{}");
		obj.put("id", backid);
		obj.put("fid", fid);
		
		obj.put("chname",URLEncoder.encode(chname==null?"":chname,"UTF-8"));
		obj.put("chuncksval",chuncksval);
		obj.put("size",size);
		PrintWriter pw = response.getWriter();
		pw.print(obj.toString());
		pw.flush();
		pw.close();
		}else if("del".equals(process)){
			log.info("【删除】process==del");
			//删除 //key:del_url
			String queuekey="v:upload:queue:"+token+":"+fid;
			Jedis jd = null;
			String delurl=null;
			String path=null;
			String path2=null;
			String path3=null;
			String path4=null;
			try{
				jd=JR.getJd();
				path=jd.get("v:upload:fileQueuePath:"+token+":"+fid);
				path2=jd.get("v:upload:imageRealPath:"+token+":"+fid);
				path3=jd.get("v:upload:fileTempPath:"+token+":"+fid);
				path4=jd.get("v:upload:fileRealPath:"+token+":"+fid);
				
				try{
				    delurl=JSONObject.fromObject(jd.get(queuekey)).getString("del_url");
				}catch(Exception e){
					delurl=null;	
					log.error("删除回调地址不通:",e);
				}
			}catch(Exception e){
				log.error("redis获取数据失败：",e);
			}finally{
				JR.close(jd);
			}
			
			if(delurl!=null){
				try {
					sendUrl(delurl, "key="+queuekey);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
			
			if(path!=null){
				//String pathLock=path+".lock";
				String pathimg=path2;
				File f1=new File(path3);
				File f3=new File(path);
				File f4=new File(pathimg);
				File f2=new File(path3+"/"+f3.getName()+".lock");
				
				if(f1.exists()){	
					for(File f:f1.listFiles())f.delete();
					f1.delete();
				}
				
				if(f2.exists()){
					f2.delete();
				}
				
				if(f3.exists()){
					f3.delete();
				}
				
				if(f4.exists()&&path2.indexOf(".jpg")>10){
					f4.delete();
				}
				
				if(path4!=null){
					File f5=new File(path4);
					if(f5.exists()) f5.delete();
				}
			}
			
			log.info("【结束】process==del");
		}else if("repost".equals(process)){
			log.info("【repost】");
			
			String msgFinish_=request.getParameter("msgFinish");
			String access_token=token;
			String fileName=request.getParameter("fname");
			String ext=request.getParameter("ext");
			ext=ext==null?"":ext.toLowerCase();
			
			if("1".equals(msgFinish_)){
				
			}else{
				String config = null;
				String fileUrl=null;
				String imageUrl=null;
				Jedis jd=null;
				String path="";
				String path2="";
			try{
				jd=JR.getJd();

				path=jd.get("v:upload:fileRealPath:"+token+":"+fid);
				path2=jd.get("v:upload:imageRealPath:"+token+":"+fid);
				
				config = jd.get("v:upload:config:" + access_token);
				fileUrl=jd.get("v:upload:fileUrl:"+access_token+":"+fid);
				imageUrl=jd.get("v:upload:imageUrl:"+access_token+":"+fid);
			}catch(Exception e){
				log.error(fileName+"回调-redis-->获取失败:",e);
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
				log.info(e.getMessage());
			}
		    
			//生成图片
			String data=path +"@postdataWithCdnPassWordTruth@"+path2
					+"@postdataWithCdnPassWordTruth@"+"320@postdataWithCdnPassWordTruth@240";
			log.info("回调-截图--->"+data);
			File realFile=new File(path);
			
			if(ext.toLowerCase().indexOf("mp4")!=-1){
			try {
				new GearClient().createPhoto(data);
			} catch (Exception e) {
				log.error("调用gearman进行createPhoto失败：",e);
			}
			
		}
			
		    log.info("回调-获取信息--->"+path+":"+fileName);
		    //获取信息	
			String msgFinish="0";
			JSONObject map=null;
			try {
				map = new GearClient().getMessage(path);
			} catch (InterruptedException e1) {
				log.error(e1.getMessage());
				e1.printStackTrace();
			}
			
			if(map==null||map.size()<2){
				msgFinish="2";
			}else{
				msgFinish="1";
			}
			
			//相对路径
			log.info("回调-获得信息--->"+map+":"+fileName);
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
				
				Map map2=FFMpegUtil.compareConfig(tempMap, map);
				result.putAll(map2);
				tempMap.putAll(result);
				tempMap.put("status_file",2);
				jd.setex(keyquq, 60 * 60 * 24,
						tempMap.toString());
				
			} catch (Exception e) {
				log.error(fileName+"回调-redis-->获取失败",e);
			} finally {
				JR.close(jd);
			}
			
			//发送接口入库
			try{
			
				if(tempMap.get("error")==null||"".equals(tempMap.get("error"))){
				    String backUrl=tempMap.getString("back_url");
					
				    if(backUrl.indexOf("\\?")>1){
						backUrl=backUrl+"?key="+keyquq;
					}else{
						backUrl=backUrl+"&key="+keyquq;
					}
					
					String value=sendUrl(backUrl, "key="+keyquq);
					if(value!=null&&value.toLowerCase().indexOf("success")!=-1){
						tempMap.put("issuccess",1);
					}
				    log.info("回调-成功回调:"+fileName+":"+backUrl+"-->"+keyquq);
				}else{
					try{
						File f2=new File(path2);
						if(realFile.exists())realFile.delete();
						if(f2.exists()&&path2.indexOf(".jpg")>10)f2.delete();
					}catch(Exception e2){
						log.error(e2.getMessage());
						e2.printStackTrace();
					}
				}
			}catch(Exception e){
				log.error("回调-回调失败:"+fileName+":"+tempMap.get("back_url")+"-->"+keyquq);
			}
			
			tempMap.put("msgFinish", msgFinish);
			PrintWriter pw = response.getWriter();
			pw.print(tempMap.toString());
			pw.flush();
			pw.close();
			log.info("回调-token:"+fid+"---->"+fileName+":"+tempMap.toString());
			}
		}else if("isTransCoding".equals(process)){//校验是否需要转码
			
			log.info("【isTransCoding】开始");
			int resultPK=Instock.insertTransCoding(token,"v:upload:queue:"+token+":"+fid);
			//String video_key="";
			String path="";
			String isTransCoding="";
			Jedis jd=null;

			try {
                jd=JR.getJd();
                path=jd.get("v:upload:fileQueuePath:"+token+":"+fid);
				Map resultMap=FFMpegUtil.getFFMpgeUtil().getMessage(path);
				
				log.info("path="+path);
				log.info("resultMap="+resultMap);
				
				if (FFMpegUtil.getFFMpgeUtil().isTransCoding(resultMap)) {//需要转码
					log.info("【需要转码】");
					isTransCoding="1";
				}else {//不需要转码
					log.info("【不需要转码】");
					isTransCoding="0";
					//video_key=Instock.toInStock(token,"v:upload:queue:" + token + ":" + fid,resultPK,request);  //access_token   key  
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("校验是否需要转码失败："+e.getMessage());
			}finally {
				JR.close(jd);
			}
			
			isTransCoding=isTransCoding+","+resultPK;
			PrintWriter pw = response.getWriter();
			pw.write(isTransCoding);
			pw.flush();
			pw.close();
			log.info("【isTransCoding】结束");
		}else if("copyFile".equals(process)){//无需转码入库完成后，开始复制文件
			log.info("【copyFile】开始");
			String pk=request.getParameter("pk");
			String resultFlag="";//0:成功;其他：文件复制失败
			Jedis jd=null;
			
			try {
                jd=JR.getJd();
               
                String originalName=jd.get("v:upload:fileQueuePath:"+token+":"+fid);
        		originalName=originalName.replaceAll("\\\\","/");
        		originalName=originalName.replaceAll("//","/");
        		
        		
        		String path=request.getSession().getServletContext().getRealPath("/");
        		path=path.replaceAll("\\\\", "/");
        		path=path.replaceAll("//","/");
        		
        		//复制文件到指定目录
        		String object = jd.get("v:upload:queue:"+token+":"+fid);
        		JSONObject jo = JSONObject.fromObject(object);
    			String targetFile=jo.getString("url");//目标文件
        		
    			//开始复制文件
    			FileUtil.copyFile(originalName,path+targetFile);//originalName
    			
    			try {
    				//文件复制结束再入库
    				String video_key=Instock.toInStock(token,"v:upload:queue:" + token + ":" + fid,Integer.parseInt(pk),request);  //access_token   key  
				} catch (Exception e) {
					resultFlag="1";//入库失败
					log.error("入库失败："+e.getMessage());
					
					
					PrintWriter pw = response.getWriter();
					pw.write(resultFlag);
					pw.flush();
					pw.close();
					
					//修改t_transcoding.msg='入库失败'
					StringBuffer updateSql=new StringBuffer();
					updateSql.append("update  t_transcoding  set msg='入库失败' where transcoding_id="+Integer.parseInt(pk));
				    Integer resFlag=SqlSession.getSqlSession().update(updateSql.toString());
				}
    			
    			
			} catch (Exception e) {
				log.error("文件复制失败："+e.getMessage());
				resultFlag="2";//文件复制失败
				e.printStackTrace();
				
				PrintWriter pw = response.getWriter();
				pw.write(resultFlag);
				pw.flush();
				pw.close();
				
				//修改t_transcoding.msg='文件复制失败'
				StringBuffer updateSql=new StringBuffer();
				updateSql.append("update  t_transcoding  set msg='文件复制失败' where transcoding_id="+Integer.parseInt(pk));
				Integer resFlag=SqlSession.getSqlSession().update(updateSql.toString());
			}finally {
				JR.close(jd);
			}
			
			resultFlag="0";//成功
			PrintWriter pw = response.getWriter();
			pw.write(resultFlag);
			pw.flush();
			pw.close();
			log.info("【copyFile】resultFlag="+resultFlag);
			log.info("【copyFile】结束");
		}else if("coding".equals(process)){
			log.info("【coding】开始");
			Jedis jd=null;
			String appid=null;
			String sequenceid=generate();
			String file_type="";
			String component=null;
			String classify=null;
			String originalName=null;
			String itemImgName=null;
			String file_long=null;
			String file_name=null;
			JSONObject result=JSONObject.fromObject("{}");
			JSONObject objFile=null;
			String backUrl="";
			try{
				
				jd=JR.getJd();
				objFile=JSONObject.fromObject(jd.get("v:upload:queue:"+token+":"+fid));
				appid=fornull(objFile.get("app_information_key"));
				file_type=objFile.getString("exts").toLowerCase();
				file_long=objFile.get("duration")==null?"0":objFile.get("duration").toString();
				component=fornull(objFile.get("app_component_key"));
				classify=fornull(objFile.get("chname"));
				itemImgName=objFile.getString("url");
				
				//itemImgName="";
				if(itemImgName!=null&&itemImgName.length()>0&&itemImgName.charAt(0)!='/'){
					itemImgName="/"+itemImgName;
				}
				
				originalName=jd.get("v:upload:fileQueuePath:"+token+":"+fid);
				originalName=originalName.replaceAll("\\\\","/");
				originalName=originalName.replaceAll("//","/");
				String path=this.getServletContext().getRealPath("/");
				path=path.replaceAll("\\\\", "/");
				path=path.replaceAll("//","/");
				
				if(originalName.indexOf(path)!=-1){
				    originalName=originalName.split(path)[1];
				}
				if(originalName!=null&&originalName.length()>0&&originalName.charAt(0)!='/'){
					originalName="/"+originalName;
				}
				
				backUrl=objFile.getString("back_url");
				//file_name=objFile.getString("basename");
				file_name=request.getParameter("name");
				String pk=request.getParameter("pk");
				if(file_name!=null) file_name=URLDecoder.decode(URLDecoder.decode(URLDecoder.decode(file_name,"UTF-8"),"UTF-8"),"UTF-8");
				//String[] fileName=file_name.split("\\.");
				String fileName=file_name.substring(0,file_name.lastIndexOf("."));
				
				result.put("appid", appid);
				result.put("file_type", file_type);
				result.put("file_long", file_long);
				result.put("component", component);
				result.put("classify", classify);
				result.put("itemImgPath", itemImgName);
				result.put("originalPath", originalName);
				result.put("sequenceid", sequenceid);
				result.put("file_size", objFile.get("size")==null?"0":objFile.get("size").toString());
				result.put("basename", fileName);
				result.put("creator", objFile.get("user_key"));
				result.put("media_type", objFile.get("content_type"));
				
				
                //2.5期新增传递字段
 				result.put("sourceimage", objFile.get("imgurl")==null?"":objFile.get("imgurl"));//视频截图地址
				result.put("sourcetype", objFile.get("content_type"));//类型：video,audio,other
				//result.put("sourcekey", "080f7316eade42118ec0a151c1f69846");//插入表的主键,t_video或者t_audio
				result.put("transcodingid", pk);//t_transcodiing主键
				result.put("channel_key", objFile.getString("channel"));
				result.put("column_key", objFile.getString("column"));
			}catch(Exception e){
				log.error(e.getMessage());
				e.printStackTrace();
			}finally{
				JR.close(jd);
			}
			
			String issuc="";
			String finish="0";
			
			
			//0 成功 1入库失败 2转码失败
			//调用gearman转码
			try {
				String GearClientFileName=PropertiesUtil.getPropertiesStringValue("uploader.properties","GearClientFileName");
				log.info("GearClientFileName="+GearClientFileName);
				new GearClient().toWork(GearClientFileName,URLEncoder.encode(result.toString(),"UTF-8"));
			} catch (Exception e) {
				log.error("调用gearman转码错误："+e.getMessage());
				e.printStackTrace();
			}
			
			/*
			 * 2.5版本修改：调用gearman不再接收返回状态。
			 * 如果不需要转码，uploder组件直接进行存库操作
			 * 如果需要转码,调用gearman,但是不用等待返回状态，并且不再sendUrl()入库操作
			 * 
			   if("success".equals(issuc)){
			 	System.out.println("开始入库....");
			 	//入库
			 	String keyquq="v:upload:queue:" + token + ":" + fid;
			 	try{		
					System.out.println("backUrl="+backUrl);
					if(backUrl.indexOf("\\?")>1){
						backUrl=backUrl+"?key="+keyquq;
					}else{
						backUrl=backUrl+"&key="+keyquq;
					}
					long t1=System.currentTimeMillis();
					System.out.println("send URL start");
					String value=sendUrl(backUrl, "key="+keyquq);
					System.out.println("send URL end");
					long t2=System.currentTimeMillis();
					if(value!=null&&value.toLowerCase().indexOf("success")!=-1){
						objFile.put("issuccess",1);
						log.info("成功回调,耗时-"+(t2-t1)+"ms:"+file_name+":"+backUrl+"-->"+keyquq);
					}else{
						finish="1";
						log.info("回调失败,耗时-"+(t2-t1)+"ms:"+file_name+":"+backUrl+"-->"+keyquq+"--返回值为:"+value);
					}
					
					}catch(Exception e){
						finish="1";
						log.info("回调失败:"+file_name+":"+backUrl+"-->"+keyquq);
						log.info(e.getMessage());
					
					}
				}else finish="2";
			 * 
			*/
			
			PrintWriter pw = response.getWriter();
			pw.write(finish);
			pw.flush();
			pw.close();
            log.info("【coding】结束");
		}else{
			   log.info("【else】开始");
			   String count= request.getParameter("count");
			   Jedis jd=null;
			   try {
				jd=JR.getJd();
				jd.setex("v:process:"+token,  60 * 60 * 24, count);
			} catch (Exception e) {
				 log.error("发送失败process:"+request.getParameter("count"));
			}finally{
				JR.close(jd);
			}
			log.info("【else】结束");
		}
		
		log.info("【结束】Business.doGet");
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
		String token = request.getParameter("access_token");
		String fid = request.getParameter("fid");
		String process=request.getParameter("process");
		log.info("【doPost】token="+token);
		log.info("【doPost】fid="+fid);
		log.info("【doPost】process="+process);
		doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	
	
	private String sendUrl(String doUrl, String paramStr)throws Exception{
		
		if(doUrl.indexOf("http://")==0){
	        //logger.info("发送url请求的方法   执行");
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
	        log.info("获取服务器端返回信息="+in);
	        
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
					log.info("发送---"+doUrl);
					b=HttpsOpenUtil.post(doUrl, paramStr, "UTF-8");
					log.info("成功---"+doUrl);
				}catch(Exception e){
					log.error("失败---"+doUrl);
				}
				if(b!=null){
					return new String(b, "UTF-8");
				}else return "";
				
			}else return "";
		
	    }
	
	    /**
	     * 
	     * @return
	     */
		public String generate(){
			String x="";
			for(int i=0;i<32;i++){
				x+=(int)(Math.random()*10)+"";
			}
			return x;
		}
		
		/**
		 * 
		 * @param o
		 * @return
		 */
		public String fornull(Object o){
			return o==null?null:o.toString();
		}
		
		
}

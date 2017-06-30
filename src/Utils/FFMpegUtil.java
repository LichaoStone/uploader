package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FFMpegUtil {
	private static final Logger logger = Logger.getLogger(FFMpegUtil.class);
	
	//String cmdf="cmd /c start ";//windows命令头 linux需更改
	String cmdf="";
	private  FFMpegUtil(){}
	private static final  FFMpegUtil ffmpgeUtil =new FFMpegUtil();
	public static FFMpegUtil getFFMpgeUtil(){
		return ffmpgeUtil;
	}
	
	 public synchronized void  prit() throws InterruptedException{
		System.out.println("I am run");
		Thread.sleep(3000);
	 }
	 
	 /**
	  * 获取视频或音频信息
	  * @param path
	  * @return
	  * @throws InterruptedException
	  */
	 public synchronized  Map  getMessage(String path) throws InterruptedException{
		logger.info("【获取音频/视频信息】开始");
		
		Map map=new HashMap();	
		String command=cmdf +"ffprobe -v quiet -print_format json -show_format -show_streams "+path;
		command=command.replaceAll("//","/"); 
		logger.info("【获取音频/视频信息】执行命令："+command);
		
		try{
			
			Process p = Runtime.getRuntime().exec(command);  
			BufferedReader br = null;  
			br = new BufferedReader(new InputStreamReader(p.getInputStream(),"utf-8")); 
			String line = null;  
	        StringBuilder sb = new StringBuilder(); 
	        while ((line =br.readLine()) != null) {  
	        	 sb.append(line);  
	        }  
	        
	        br.close();  
	        p.waitFor();
	        
	        if(null!=p){
	        	p.destroy();
	        	p=null;
	        }
	        
	        logger.info("【获取音频/视频信息】返回json串："+sb.toString());
	        JSONObject jo=JSONObject.fromObject(sb.toString());
	        JSONArray streams=jo.getJSONArray("streams");
	      
	        for(int i=0;i<streams.size();i++){
	        	JSONObject stream=streams.getJSONObject(i);
	        	Object type=stream.get("codec_type");
	        	if(type!=null&&type.toString().indexOf("video")!=-1){//视频
	        			map.put("coded_width",stream.get("coded_width"));
	        			map.put("coded_height",stream.get("coded_height"));
	        			map.put("codec_name_v",stream.get("codec_name") );
	        			map.put("bit_rate_v", stream.get("bit_rate"));
	        			//break;
	        	}else if(type!=null&&type.toString().indexOf("audio")!=-1){//音频
	    			map.put("codec_name_s",stream.get("codec_name") );
	    			map.put("bit_rate_s", stream.get("sample_rate"));
	    			
	    			//2.5版本新增 
	    			map.put("bit_rate_a", stream.get("bit_rate"));
	        		}
	        	}
	        
	        	JSONObject styt=jo.getJSONObject("format");
	        	try{
		        	map.put("duration", Integer.parseInt(styt.getString("duration").toString().split("\\.")[0]));
		        	Object size=styt.get("size");
		        	if(size==null)size="0";
		        	long sizeKb=Long.parseLong(size.toString())/1024;
		        	map.put("size", sizeKb);
				}catch(Exception e){
					e.printStackTrace(); 
					map.put("size", styt.get("size"));
					map.put("duration",0);
				}
		}catch(Exception e){
			logger.error("【获取音频/视频信息】出错:",e);
		}
		
		logger.info("【获取音频/视频信息】返回map="+map);
		logger.info("【获取音频/视频信息】结束");
		return map;
	 }
	 
	 
	/**
	 * 截图
	 * @param filePath
	 * @param imgPath
	 * @param x
	 * @param y
	 * @return
	 */
	public synchronized boolean createPhoto(String filePath,String imgPath,int x,int y){
		logger.info("【截图】createPhoto开始");
		String command=cmdf +""+ "ffmpeg -y -i "+filePath+" -vframes 1 -r 1 -ac 1 -ab 2 "+"-s "+x+"x"+y+" -ss 1 -f image2 "+imgPath;
		logger.info("执行命令："+command);
		
	    try{
			Process   process   = Runtime.getRuntime().exec(command);  
			process.waitFor();
		    File f=new File(imgPath);
		    
		    logger.info("【截图】createPhoto结束");
		    return f.exists();
		}catch(Exception e){
			logger.info("【截图】出错:",e);
			return  false;
		}
	}
	
	/**
	 * 校验：格式、大小
	 * @param config
	 * @param media
	 * @return
	 */
	public static  Map compareConfig(Map config,Map media){
		 /*
		  * config:bit_rate_v 1000-2000;codec_name_v a,b;bit_rate_s;codec_name_s;resolving 133-20
		  * media:coded_width;coded_height;codec_name_v;bit_rate_v;
		  * coded_height;codec_name_v;bit_rate_v;codec_name_s;bit_rate_s
		  * 
		  * 
		  */
		//List result=new ArrayList();
		logger.info("【信息比较】开始");
		logger.info("【信息比较】duration空：文件格式错误。duration="+media.get("duration"));
		logger.info("【信息比较】realname不为空且不包含mp3或mp4：文件格式错误。realname="+media.get("realname"));
		logger.info("【信息比较】codec_name_v="+config.get("codec_name_v"));
		logger.info("【信息比较】codec_name_s="+config.get("codec_name_s"));
		logger.info("【信息比较】mp4不能为空，mp3应该为空。coded_width="+media.get("coded_width"));
		logger.info("【信息比较】mp4不能为空。codec_name_v="+media.get("codec_name_v"));
		
		
		Map map=new HashMap();
		Object str;
		if(media.get("duration")==null){
			map.put("error","文件格式错误");
			return map;	
		}
		
		if(media.get("realname")!=null&&media.get("realname").toString().indexOf(".mp4")!=-1){
			if(media.get("coded_width")==null||media.get("codec_name_v")==null){
				map.put("error","文件格式错误");
				return map;	
			}
		}
		if(media.get("realname")!=null&&media.get("realname").toString().indexOf(".mp3")!=-1){
			if(media.get("coded_width")!=null){
				map.put("error","文件格式错误");
				return map;	
			}
		}
		// codec_name_v:
		if(!isnull(str=config.get("codec_name_v"))){
			String codec_name=str.toString()+",";
			String codec_name_=forString(media.get("codec_name_v"));
			
			if("".equals(codec_name_)||codec_name.indexOf(codec_name_+",")==-1){
				map.put("error","视频编码无效");
				return map;
			}
		}else if(!isnull(str=config.get("codec_name_s"))){// codec_name_s:
			String codec_name=str.toString()+",";
			String codec_name_=forString(media.get("codec_name_s"));
			if("".equals(codec_name_)||codec_name.indexOf(codec_name_+",")==-1){
				map.put("error","音频编码无效");
				return map;
			}
		}
		/*	
		//bit_rate_v 视频码率
		else if(!isnull(str=config.get("bit_rate_v"))){
			String [] arrs=str.toString().split("-");
			int start=Integer.parseInt(arrs[0]);
			int end=Integer.parseInt(arrs.length>1?arrs[1]:"100000000");
			int bit_rate=forInt(media.get("bit_rate_v"));
			if(bit_rate==0||bit_rate<start||bit_rate>end){
				//map.put("error","视频码率应为:"+str);
				map.put("error","视频码率应为:250Kbps");
				return map;
			}
			
		}
		//bit_rate_s 音频码率	
		else if(!isnull(str=config.get("bit_rate_s"))){
			String [] arrs=str.toString().split("-");
			
			int start=Integer.parseInt(arrs[0]);
			
			int end=Integer.parseInt(arrs.length>1?arrs[1]:"100000000");
			
			int bit_rate=forInt(media.get("bit_rate_s"));
			
			if(bit_rate==0||bit_rate<start||bit_rate>end){
				//map.put("error","音频码率应为:"+str);
				map.put("error","音频码率应为:32Kbps");
				return map;
			}
			
		}
		config:resolving 133-20
		   media:coded_width;coded_height
		
		else if(!isnull(str=config.get("resolving"))){
			try{
				int width=Integer.parseInt(media.get("coded_width").toString());
				int height=Integer.parseInt(media.get("coded_height").toString());
				int pianyi=Integer.parseInt(str.toString().split("-")[1]);
				int w=Integer.parseInt(str.toString().split("-")[0].split("*")[0]);
				int h=Integer.parseInt(str.toString().split("-")[0].split("*")[1]);
				int p=(int)(w*100/h);
				int percent=(int)(width*100/height);
				if(percent>p+pianyi||percent<p-pianyi){
					map.put("error","分辨率无效");	
				}
			}catch(Exception e){
				map.put("error","分辨率无效");	
			}
		}
		*/
		logger.info("【信息比较】返回结果：map="+map);
		logger.info("【信息比较】结束");
		return  map;
	}
	
	/**
	 * 判断是否需要转码
	 * true:需要转码；false:不需要转码。
	 * 音频都需要转码
	 * @param config
	 * @return
	 */
	public static  boolean isTransCoding(Map config){
		Integer bit_rate_v=Integer.parseInt(PropertiesUtil.getPropertiesStringValue("uploader.properties", "bit_rate_v"));
		Integer bit_rate_s=Integer.parseInt(PropertiesUtil.getPropertiesStringValue("uploader.properties", "bit_rate_s"));
		
		logger.info("【判断是否需要转码】配置文件中设置的视频码率="+bit_rate_v);
		logger.info("【判断是否需要转码】配置文件中设置的音频码率="+bit_rate_s);
		logger.info("【判断是否需要转码】上传视频类型(h264)="+config.get("codec_name_v"));
		logger.info("【判断是否需要转码】上传视频音频类型(acc)="+config.get("codec_name_s"));
		logger.info("【判断是否需要转码】上传视频视频码率="+config.get("bit_rate_v"));
		logger.info("【判断是否需要转码】上传视频音频码率="+config.get("bit_rate_s"));
		
		boolean result=true;
		
		if ("h264".equals(config.get("codec_name_v"))&&"aac".equals(config.get("codec_name_s"))&&Double.parseDouble(config.get("bit_rate_v").toString())<bit_rate_v&&Double.parseDouble(config.get("bit_rate_s").toString())<bit_rate_s) {
			result=false;
		}else {//需要转码
			result=true;
		}
		
		logger.info("【判断是否需要转码】比较结果:"+(result==true?"需要转码":"不需要转码"));
		return result;
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isnull(Object o){
		return o==null||"".equals(o.toString())||"null".equals(o.toString())?true:false;
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 */
	public  static String forString(Object o){
		return o==null?"":o.toString();
	}
	
	/**
	 * 强制转化转为Int类型
	 * @param o
	 * @return
	 */
	public static int forInt(Object o){
		try{
			return Integer.parseInt(o.toString());
		}catch(Exception e){
			return 0;
		}
	}

}
package instock;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mysql.jdbc.StringUtils;

import Utils.BCConvert;
import Utils.DateUtil;
import Utils.FFMpegUtil;
import Utils.JR;
import Utils.PropertiesUtil;
import Utils.SqlSession;
import Utils.UUIDGenerator;
import instock.bean.ClassifyBean;
import instock.bean.TransCodingBean;
import instock.bean.VideoBean;
import instock.bean.VideoOtherBean;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

public class Instock {
	private static final Logger logger = Logger.getLogger(Instock.class);
	
	/**
	 * 判断是否转码之前，先插入t_transcoding表一条数据
	 */
	public static int insertTransCoding(String access_token,String key){
		logger.info("【insertTransCoding】开始access_token="+access_token+";key="+key);
		int returnPK=0;
		Jedis jd=null;
		
		try {
			jd=JR.getJd();
			String value = jd.get("v:upload:isactive:" + access_token);
			if (value.equals("2")) {
				jd.set("v:upload:isactive:" + access_token, "3");
			}
			
			String object = jd.get(key);
			JSONObject jo = JSONObject.fromObject(object);
			logger.info("value="+value);
			logger.info("insertTransCoding jo="+jo);
			
			//组织插入数据
			TransCodingBean bean=new TransCodingBean();
			bean.setApp_information_key(jo.getString("app_information_key") == null ? "" : jo
					.get("app_information_key").toString());
			bean.setCreate_time(DateUtil.getTimeToSec());
			bean.setBegin_time(DateUtil.getTimeToSec());
			bean.setDuration(jo.getString("duration") == null ? "" : jo
					.get("duration").toString());
			bean.setCreator(jo.getString("user_key") == null ? "" : jo
					.get("user_key").toString());
			bean.setMedia_type(jo.getString("content_type") == null ? "" : jo
					.get("content_type").toString());
			if (!"other".equals(jo.getString("content_type"))) {//非其他视屏，才获取app_component_key
				bean.setComponent(jo.getString("app_component_key") == null ? "" : jo
						.get("app_component_key").toString());
			}
			bean.setFile_long(jo.getString("duration") == null ? "" : jo
					.get("duration").toString());
			bean.setFile_type(jo.getString("exts") == null ? "" : jo
					.get("exts").toString());
			bean.setClassify(jo.getString("chname") == null ? "" : jo
					.get("chname").toString());
			//bean.setFile_name(jo.getString("basename")+"."+jo.getString("realname").split("\\.")[1]);
			bean.setFile_name(jo.getString("realname")==null?"":jo.getString("realname"));
			bean.setSequenceid(generate());
			bean.setStatus("transcoding");//***状态为正在转码
			bean.setFile_original_name(jo.getString("basename") == null ? "" : jo
					.get("basename").toString());
			bean.setFile_size(jo.get("size")==null?"0":jo.get("size").toString());
			//bean.setMsg();
			//bean.setS_list_imgname();
			bean.setEnd_time(DateUtil.getTimeToSec());
			bean.setWait_time(DateUtil.getTimeToSec());
			
			
			
			//组织SQL
			StringBuffer insertSql=new StringBuffer();
			insertSql.append("insert into t_transcoding (")
			.append("app_information_key,").append("create_time,")
			.append("wait_time,").append("begin_time,").append("end_time,")
			.append("duration,").append("file_name,").append("file_size,")
			.append("file_long,").append("file_type,").append("component,")
			.append("classify,").append("status,").append("file_original_name,")
			.append("msg,").append("s_list_imgname,").append("sequenceid,")
			.append("creator,").append("media_type")
			.append(")")
			.append(" values ")
			.append("(")
			.append("'").append(bean.getApp_information_key()).append("'")
			.append(",'").append(bean.getCreate_time()).append("'")
			.append(",'").append(bean.getWait_time()).append("'")
			.append(",'").append(bean.getBegin_time()).append("'")
			.append(",'").append(bean.getEnd_time()).append("'")
			.append(",'").append(bean.getDuration()).append("'")
			.append(",'").append(BCConvert.ToSBC(bean.getFile_name())).append("'")
			.append(",'").append(bean.getFile_size()).append("'")
			.append(",'").append(bean.getFile_long()).append("'")
			.append(",'").append(bean.getFile_type()).append("'");
			
			if (bean.getComponent()==null) {
				insertSql.append(",").append(bean.getComponent()).append("");
			}else {
				insertSql.append(",'").append(bean.getComponent()).append("'");
			}
			
			insertSql.append(",'").append(bean.getClassify()).append("'")
			.append(",'").append(bean.getStatus()).append("'")
			.append(",'").append(BCConvert.ToSBC(bean.getFile_original_name())).append("'");
			
			if (bean.getMsg()==null) {
				insertSql.append(",").append(bean.getMsg()).append("");
			}else {
				insertSql.append(",'").append(bean.getMsg()).append("'");
			}
			
			if (bean.getS_list_imgname()==null) {
				insertSql.append(",").append(bean.getS_list_imgname()).append("");
			}else {
				insertSql.append(",'").append(bean.getS_list_imgname()).append("'");
			}
			
			insertSql.append(",'").append(bean.getSequenceid()).append("'")
			.append(",'").append(bean.getCreator()).append("'")
			.append(",'").append(bean.getMedia_type()).append("'")
			.append(")");
			
			returnPK=SqlSession.getSqlSession().insertAndReturnPK(insertSql.toString());
			logger.info("【insertTransCoding】 结束");
		} catch (Exception e) {
		    logger.error("【insertTransCoding】插入t_transcoding表数据出错:",e);
		}finally{
			JR.close(jd);
		}
		return returnPK;
	}
	
	/**
	 * 不需要转码文件，直接入库
	 * @param access_token
	 * @param key
	 * @param respk
	 * @return
	 */
	public static String  toInStock(String access_token,String key,int respk,HttpServletRequest request){
	    logger.info("【toInStock】开始入库");
		
	    Jedis jd=JR.getJd();
		String value = jd.get("v:upload:isactive:" + access_token);
		logger.info("value="+value);
		
		if (value.equals("2")) {
			jd.set("v:upload:isactive:" + access_token, "3");
		}
		
	    //修改t_transcoding.status状态为：needless（不需要转码）
		StringBuffer updateSql=new StringBuffer();
		updateSql.append("update t_transcoding set status='needless' where transcoding_id='"+respk+"'");
		
		SqlSession.getSqlSession().update(updateSql.toString());
		
		
		String itemPath=jd.get("v:upload:fileQueuePath:"+access_token+":"+key.split(":")[4]);
		Map resultMap=new HashMap();
		try {
			resultMap = FFMpegUtil.getFFMpgeUtil().getMessage(itemPath);
		} catch (InterruptedException e1) {
			logger.error("获取getMessage出错：",e1);
			JR.close(jd);
		}
		
		String object = jd.get(key);
		JSONObject jo = JSONObject.fromObject(object);
		String type = jo.getString("type");
		String pk = UUIDGenerator.getTablePk();
		String content_type = jo.get("content_type") == null
				|| "".equals(jo.get("content_type"))
				|| "null".equals(jo.get("content_type")) ? "1" : jo.get(
				"content_type").toString();
		
		logger.info("插入t_video或t_audio或t_video_others表主键key="+pk);
		
		if (!"other".equals(content_type)) {
			if (type.equals("video")) {
			try {	
				logger.info("【存库-视频】");
				//组织插入条件
				VideoBean bean = new VideoBean();
				bean.setVideo_key(pk);
				String url = jo.getString("url") == null ? "" : jo.get(
						"url").toString();
				bean.setPlay_url(url);
				if (!StringUtils.isNullOrEmpty(url)) {
					String play_url = PropertiesUtil.getPropertiesStringValue("uploader.properties", "playurl");
					String video_url = play_url + "/" + url.substring(6)
							+ PropertiesUtil.getPropertiesStringValue("uploader.properties", "playSuffix");
					bean.setVideo_url(video_url);
				}
				bean.setVideo_img(jo.getString("imgurl") == null ? "" : jo
						.get("imgurl").toString());
				String name = jo.getString("basename") == null ? "" : jo
						.get("basename").toString();
				bean.setVideo_name(name);
				int miao = jo.get("duration") == null ? 0 : Integer
						.parseInt(jo.get("duration").toString());
				bean.setVideo_long(String.valueOf(miao));//DateUtil.formatTime
				bean.setCreator(jo.getString("user_key") == null ? "" : jo
						.get("user_key").toString());
				String channel = jo.getString("channel") == null ? "" : jo
						.get("channel").toString();
				String column = jo.get("column") == null ? null : jo.get(
						"column").toString();
				bean.setChannel_key(channel);
				bean.setColumn_key(column);
				bean.setApp_component_key(jo.getString("app_component_key") == null ? ""
						: jo.get("app_component_key").toString());
				bean.setApp_information_key(jo
						.getString("app_information_key") == null ? "" : jo
						.get("app_information_key").toString());
				bean.setPlay_time(DateUtil.getTimeToSec());
				bean.setType("1");
				bean.setCreate_time(DateUtil.getTimeToSec());

				// 判断是否需要审核
				String sql="select content_auditing as status from t_menu_app where app_component_key=";
				sql=sql+"'"+bean.getApp_component_key()+"' limit 1";
				String content_auditing=SqlSession.getSqlSession().selectOne(sql);
				logger.info("判断是否需要审核：content_auditing="+content_auditing);
				
				if ("0".equals(content_auditing)) {// 不需要审核
					bean.setStatus("examineYes");
				} else {// 需要审核
					bean.setStatus("waitExamine");
				}
				
				// 初始化标签
				bean.setFile_size(jo.get("size") == null ? "0" : jo.get(
						"size").toString());
				bean.setContent_type(content_type);
				
				
				long bit_rate_v=Long.parseLong((String) resultMap.get("bit_rate_v"));
				long bit_rate_a=Long.parseLong((String) resultMap.get("bit_rate_a"));
				long sum=(bit_rate_a+bit_rate_v)/1000;
				bean.setBitrate(String.valueOf(sum));//码率
				bean.setTranscode_status("needless");//转码状态
				bean.setResolution(resultMap.get("coded_width")+"x"+resultMap.get("coded_height"));//分辨率
				
				ClassifyBean chbean = new ClassifyBean();
				ClassifyBean cobean = new ClassifyBean();

				if ("categoryVideo".equals(content_type)) {
					chbean.setClassify_key(channel);
					String selectSql="select classify_name from t_classify where status!='-1' and classify_key='"+chbean.getClassify_key()+"'";
					String classify_name=SqlSession.getSqlSession().selectOne(selectSql);
					bean.setTag("分类视频，" + name + "，"
							+ classify_name);
				} else {
					chbean.setClassify_key(channel);
					cobean.setClassify_key(column);
					String selectSqlCh="select classify_name from t_classify where status!='-1' and classify_key='"+chbean.getClassify_key()+"'";
					String selectSqlCo="select classify_name from t_classify where status!='-1' and classify_key='"+cobean.getClassify_key()+"'";

					String classify_nameCh=SqlSession.getSqlSession().selectOne(selectSqlCh);
					String classify_nameCo=SqlSession.getSqlSession().selectOne(selectSqlCo);
					
					bean.setTag("点播，" + name + "，"+ classify_nameCh+ "、"+ classify_nameCo);
				}
			
				
				
				//组织SQL
				StringBuffer insertSql=new StringBuffer();
				insertSql.append("insert into t_video (")
				.append("video_key,").append("video_name,").append("channel_key,")
				.append("column_key,").append("video_long,").append("status,")
				.append("description,").append("create_time,").append("creator,")
				.append("video_img,").append("video_url,").append("app_information_key,")
				.append("channel_name,").append("column_name,").append("play_time,")
				.append("tag,").append("app_component_key,").append("play_url,")
				.append("type,").append("content_type,").append("file_size,")
				.append("bitrate,").append("resolution,").append("transcode_status")
				.append(") values ( ")
				.append("'").append(bean.getVideo_key()).append("'")
				.append(",'").append(BCConvert.ToSBC(bean.getVideo_name())).append("'")
				.append(",'").append(bean.getChannel_key()).append("'");
				if (bean.getColumn_key()==null) {
					insertSql.append(",").append(bean.getColumn_key()).append("");
				}else{
					insertSql.append(",'").append(bean.getColumn_key()).append("'");
				}
				insertSql.append(",'").append(bean.getVideo_long()).append("'")
				.append(",'").append(bean.getStatus()).append("'");
				if (bean.getDescription()==null) {
					insertSql.append(",").append(bean.getDescription()).append("");
				}else{
					insertSql.append(",'").append(bean.getDescription()).append("'");
				}
				insertSql.append(",'").append(bean.getCreate_time()).append("'")
				.append(",'").append(bean.getCreator()).append("'")
				.append(",'").append(bean.getVideo_img()).append("'")
				.append(",'").append(bean.getVideo_url()).append("'")
				.append(",'").append(bean.getApp_information_key()).append("'");
			    if (bean.getChannel_name()==null) {
				    insertSql.append(",").append(bean.getChannel_name()).append("");
				}else{
					insertSql.append(",'").append(bean.getChannel_name()).append("'");
				}
			    if (bean.getColumn_name()==null) {
				    insertSql.append(",").append(bean.getColumn_name()).append("");
				}else{
					insertSql.append(",'").append(bean.getColumn_name()).append("'");
				}
				insertSql.append(",'").append(bean.getPlay_time()).append("'")
				.append(",'").append(bean.getTag()).append("'")
				.append(",'").append(bean.getApp_component_key()).append("'")
				.append(",'").append(bean.getPlay_url()).append("'")
				.append(",'").append(bean.getType()).append("'")
				.append(",'").append(bean.getContent_type()).append("'")
				.append(",'").append(bean.getFile_size()).append("'")
				.append(",'").append(bean.getBitrate()).append("'")
				.append(",'").append(bean.getResolution()).append("'")
				.append(",'").append(bean.getTranscode_status()).append("'")
				.append(")");
					
					SqlSession.getSqlSession().insert(insertSql.toString());		
				} catch (Exception e) {
					logger.error("【toInStock】插入t_video异常：",e);
				}finally{
					JR.close(jd);
				}
				
			}
		} else {
			logger.info("【存库-其他视频】");
			try {
				//组织插入条件
				VideoOtherBean bean = new VideoOtherBean();
				bean.setVideo_key(pk);
				String url = jo.getString("url") == null ? "" : jo.get("url")
						.toString();
				bean.setPlay_url(url);
				if (!StringUtils.isNullOrEmpty(url)) {
					String play_url = PropertiesUtil.getPropertiesStringValue("uploader.properties", "playurl");
					String video_url = play_url + "/" + url.substring(6)
							+ PropertiesUtil.getPropertiesStringValue("uploader.properties", "playSuffix");
					bean.setVideo_url(video_url);
				}
				bean.setVideo_img(jo.getString("imgurl") == null ? "" : jo.get(
						"imgurl").toString());
				String name = jo.getString("basename") == null ? "" : jo.get(
						"basename").toString();
				bean.setVideo_name(name);
				int miao = jo.get("duration") == null ? 0 : Integer.parseInt(jo
						.get("duration").toString());
				bean.setVideo_long(String.valueOf(miao));//DateUtil.formatTime
				bean.setCreator(jo.getString("user_key") == null ? "" : jo.get(
						"user_key").toString());
				bean.setFile_size(jo.get("size") == null ? "0" : jo.get("size")
						.toString());
	
				bean.setApp_information_key(jo.getString("app_information_key") == null ? ""
						: jo.get("app_information_key").toString());
				bean.setPlay_time(DateUtil.getTimeToSec());
				// 初始化标签
				String file_type = jo.get("exts") == null ? null : jo
						.get("exts").toString().toLowerCase();
				bean.setFile_type(file_type);
				bean.setTag("其他视频，" + name);
				bean.setCreate_time(DateUtil.getTimeToSec());
				
				
				long bit_rate_v=Long.parseLong((String) resultMap.get("bit_rate_v"));
				long bit_rate_a=Long.parseLong((String) resultMap.get("bit_rate_a"));
				long sum=(bit_rate_a+bit_rate_v)/1000;
				bean.setBitrate(String.valueOf(sum));//码率
				bean.setTranscode_status("needless");//转码状态
				bean.setResolution(resultMap.get("coded_width")+"x"+resultMap.get("coded_height"));//分辨率
			
				
				 StringBuffer insertSql=new StringBuffer();
				 insertSql.append("insert into t_video_others ( ")
				 .append("video_key")
				 .append(",video_name")
				 .append(",video_long")
				 .append(",description")
				 .append(",create_time")
				 .append(",creator")
				 .append(",video_img")
				 .append(",video_url")
				 .append(",app_information_key")
				 .append(",play_time")
				 .append(",tag")
				 .append(",play_url")
				 .append(",file_size")
				 .append(",file_type")
				 .append(",resolution")
				 .append(",bitrate")
				 .append(",transcode_status")
				 .append(")")
				 .append(" values ")
				 .append("(")
				 .append("'").append(bean.getVideo_key()).append("'")
				 .append(",'").append(BCConvert.ToSBC(bean.getVideo_name())).append("'")
				 .append(",'").append(bean.getVideo_long()).append("'");
				 if (bean.getDescription()==null) {
					 insertSql.append(",").append(bean.getDescription()).append("");
				 }else {
					 insertSql.append(",'").append(bean.getDescription()).append("'");
				 }
				 insertSql.append(",'").append(bean.getCreate_time()).append("'")
				 .append(",'").append(bean.getCreator()).append("'")
				 .append(",'").append(bean.getVideo_img()).append("'")
				 .append(",'").append(bean.getVideo_url()).append("'")
				 .append(",'").append(bean.getApp_information_key()).append("'")
				 .append(",'").append(bean.getPlay_time()).append("'")
				 .append(",'").append(bean.getTag()).append("'")
				 .append(",'").append(bean.getPlay_url()).append("'")
				 .append(",'").append(bean.getFile_size()).append("'")
				 .append(",'").append(bean.getFile_type()).append("'")
				 .append(",'").append(bean.getResolution()).append("'")
				 .append(",'").append(bean.getBitrate()).append("'")
				 .append(",'").append(bean.getTranscode_status()).append("'")
				 .append(")");
					
					SqlSession.getSqlSession().insert(insertSql.toString());
				} catch (Exception e) {
					logger.error("【toInStock】插入t_video_others表异常：",e);
				}finally{
					JR.close(jd);
				}
		
		}
		//String[] keys = key.split(":");
		//RedisUtil.setex("v:result:" + keys[3] + ":" + keys[4], pk + ":"
		//		+ type, 24 * 60 * 60);
		
		/*
		 * 复制文件到指定文件
		 * 说明：不在入库操作时进行文件复制，否则页面的等待时间太长
		 * 
		String originalName=jd.get(key.replace("queue","fileQueuePath"));
		originalName=originalName.replaceAll("\\\\","/");
		originalName=originalName.replaceAll("//","/");
		
		
		String path=request.getSession().getServletContext().getRealPath("/");
		path=path.replaceAll("\\\\", "/");
		path=path.replaceAll("//","/");
		
		
		//复制文件到指定目录
		try {
			String targetFile=jo.getString("url");//目标文件
			copyFile(originalName,path+targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		logger.info("【toInStock】结束入库pk="+pk);
		return pk;
	}
	
	
	public static String generate(){
		String x="";
		for(int i=0;i<32;i++){
			x+=(int)(Math.random()*10)+"";
		}
		return x;
	}
}

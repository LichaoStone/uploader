package Utils;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public class RetryGerman {
	public static void main(String[] args) {
		String sql="select * from t_transcoding where 1=1 and status='transcoding' and create_time > '2017-02-23 15:00:00' order by create_time desc";
		List<Map<String,Object>> dataList= SqlSession.getSqlSession().selectGerman(sql);
		JSONObject result=JSONObject.fromObject("{}");
		for(Map<String, Object> map:dataList){
	    	
	    	result.put("appid", map.get("app_information_key"));
			result.put("file_type", map.get("file_type") );
			result.put("file_long",map.get("file_long") );
			result.put("component", map.get("component") );
			result.put("classify", map.get("classify"));
			result.put("itemImgPath",map.get("classify"));//itemImgName  url
			result.put("originalPath", map.get("file_original_name"));//file_original_name
			result.put("sequenceid", map.get("sequenceid"));//sequenceid
			result.put("file_size", map.get("file_size"));
			result.put("basename",  map.get("file_original_name")); //file_original_name  request
			result.put("creator", map.get("creator")); //creator
			result.put("media_type", map.get("media_type")); //media_type
			
			 //2.5期新增传递字段
			//result.put("sourceimage", objFile.get("imgurl")==null?"":objFile.get("imgurl"));//视频截图地址
			result.put("sourcetype",  map.get("media_type"));//类型：video,audio,other
			//result.put("sourcekey", "080f7316eade42118ec0a151c1f69846");//插入表的主键,t_video或者t_audio
			result.put("transcodingid", map.get("transcoding_id"));//t_transcodiing主键
			//result.put("channel_key", objFile.getString("channel"));
			//result.put("column_key", objFile.getString("column"));
	    	
	    }
		
		
		//调用gearman转码
		try {
			String GearClientFileName=PropertiesUtil.getPropertiesStringValue("uploader.properties","GearClientFileName");
			new GearClient().toWork(GearClientFileName,URLEncoder.encode(result.toString(),"UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

package Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 加载指定配置文件公共类
 * @作者 lichao
 * @时间 2016年10月9日 上午8:04:59
 * @说明
 */
public class PropertiesUtil {
	private static Log log = LogFactory.getLog(PropertiesUtil.class);

	@SuppressWarnings("finally")
	public static Properties init(String path) {
		Properties properties = new Properties();
		try {
			InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
			if (in == null) {
				throw new RuntimeException("PropertiesUtil读取配置过程中" + path+ "文件找不到！");
			}
			properties.load(in);
		} catch (IOException e) {
			if(log.isErrorEnabled()){
			log.error("PropertiesUtil 加载" + path + "文件出错", e);
			}
		} finally {
			return properties;
		}
	}



	/**
	 * 获取自定义配置文件中的值
	 * @param path
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getPropertiesStringValue(String path, String key)  {
		String returnStr="";
		try {
			returnStr=new String(PropertiesUtil.init(path).getProperty(key).getBytes("iso-8859-1"),"utf-8");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return  returnStr;
	}
	
	public static void main(String[] args) {
		String str=init("uploader.properties").getProperty("special_char_In_Title");
		String str2=getPropertiesStringValue("uploader.properties","special_char_In_Title");
		System.out.println("str="+str);
		System.out.println("str2="+str2);
	}
}

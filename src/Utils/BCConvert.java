package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 特殊符号处理
 * @作者 lichao
 * @时间 2016年10月16日 下午2:38:36
 * @说明
 */
public class BCConvert {
	private static final Logger logger = LoggerFactory.getLogger(BCConvert.class);
	
	/**
	 * 半角转全角
	 * 
	 * @param input
	 *            String.
	 * @return 全角字符串.
	 */
	public static String ToSBC(String input) {
		logger.info("输入字符串:"+input);
		char c[] = input.toCharArray();
		String special = PropertiesUtil.getPropertiesStringValue("uploader.properties","special_char_In_Title");
		logger.info("需要处理的特殊符号(配置文件中)："+special);
		
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				if(special.indexOf(c[i])>0)
				  c[i] = (char) (c[i] + 65248);
			}
		}
		
		String ret = new String(c);
		logger.info("转化后的字符串:"+ret);
		return ret;
	}
	
	
	public static void main(String[] args){
		System.out.print(BCConvert.ToSBC("我@的%ac"));
	}
}

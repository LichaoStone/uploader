package Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 * @作者 lichao
 * @时间 2016年10月9日 上午9:34:43
 * @说明
 */
public class DateUtil {
	/**
	 * "yyyy-MM-dd HH:mm:ss"的日期格式
	 */
	private static String dateFormatToSec = "yyyy-MM-dd HH:mm:ss";
	
	 public static String secToTime(int time) {  
	        String timeStr = null;  
	        int hour = 0;  
	        int minute = 0;  
	        int second = 0;  
	        if (time <= 0)  
	            return "00:00";  
	        else {  
	            minute = time / 60;  
	            if (minute < 60) {  
	                second = time % 60;  
	                timeStr = unitFormat(minute) + ":" + unitFormat(second);  
	            } else {  
	                hour = minute / 60;  
	                if (hour > 99)  
	                    return "99:59:59";  
	                minute = minute % 60;  
	                second = time - hour * 3600 - minute * 60;  
	                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);  
	            }  
	        }  
	        return timeStr;  
	    }
	   public static String unitFormat(int i) {  
	        String retStr = null;  
	        if (i >= 0 && i < 10)  
	            retStr = "0" + Integer.toString(i);  
	        else  
	            retStr = "" + i;  
	        return retStr;  
	    }
	   
	   /**
		 * 该方法获取系统的当前时间并将其转换为"yyyy-MM-dd HH:mm:ss"格式字符串
		 * 
		 * @return String 当前时间的"yyyy-MM-dd HH:mm:ss"格式化字符串
		 */
		public static String getTimeToSec() {
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatToSec);
			return dateFormat.format(new Date());
		}
		
		/**
		 * 格式化时间
		 * @param miao
		 * @return
		 */
		public static String formatTime(int miao) {
			int hour = miao / 3600;
			int yuMin = miao % 3600;
			int min = yuMin / 60;
			int ss = yuMin % 60;
			String h = String.valueOf(hour);
			String m = String.valueOf(min);
			String s = String.valueOf(ss);
			if (hour < 10) {
				h = "0" + h;
			}
			if (min < 10) {
				m = "0" + m;
			}
			if (ss < 10) {
				s = "0" + s;
			}
			return h + ":" + m + ":" + s;
		}
}

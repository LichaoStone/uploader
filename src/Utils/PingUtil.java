package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class PingUtil extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PingUtil.class); 
	
	public PingUtil(){
	    super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 String addrs= request.getParameter("adds");
		 String backString="";
		 
		 //截取ping地址
		 if (addrs.startsWith("http://")) {
			if (addrs.endsWith("/")) {
				addrs=addrs.substring(7, addrs.length()-1);
				addrs=addrs.split(":")[0];
			}else{
				addrs=addrs.split("http://")[1];
				addrs=addrs.split(":")[0];
			}
		 }else if (addrs.startsWith("https://")) {
			 if (addrs.endsWith("/")) {
					addrs=addrs.substring(8, addrs.length()-1);
					addrs=addrs.split(":")[0];
				}else{
					addrs=addrs.split("https://")[1];
					addrs=addrs.split(":")[0];
				}
		 }
		
		 logger.info("【网速测试】addrs="+addrs);
	     if (addrs==null||"".equals(addrs)||"undefined".equals(addrs)){
	    	 logger.error("【网速测试】addrs不能为空");
	     }else{
		         String line = null;
		         try{
		        	 
		             //windows下
		        	 //Process pro = Runtime.getRuntime().exec("ping " + addrs+" -l 1000 -n 4");
		        	 
		        	 //linux下
		        	 Process pro = Runtime.getRuntime().exec("ping " + addrs+" -l 1000 -c 4");
		             BufferedReader buf = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		             while((line = buf.readLine()) != null){
		            	 //if (line.indexOf("TTL")==-1) {
		            		 logger.info("【网速测试】明细:"+line);
				              int position=0;
				              if((position=line.indexOf("平均"))!=-1){  
				            	  String speed=line.substring(position+4,line.lastIndexOf("ms"));
				            	  backString= "({success:" + true + ",msg:'" + speed + "'})";
				            	  logger.info("【网速测试】结果:"+backString);      
				              }else {
				            	  backString= "({success:" + false + ",msg:''})";
							  }
						 //}else{
						 //	 speed=line;
						 //}
		             }     
		         }catch(Exception ex){
		        	 backString= "({success:" + false + ",msg:''})";
		        	 logger.info("【网速测试】出错:",ex);
	             }
	     }
		
	    
        PrintWriter pw = response.getWriter();
		pw.write(backString);
		pw.flush();
		pw.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
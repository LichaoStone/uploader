package Utils;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobReturn;
import org.gearman.GearmanServer;

public class GearClient { 
	
	private static final Logger logger = Logger.getLogger(GearClient.class);
	private static final String sbase=PropertiesUtil.getPropertiesStringValue("uploader.properties","GearClientIp");
	private static final String prort=PropertiesUtil.getPropertiesStringValue("uploader.properties","GearClientPrort");

	/**
	 * 截图
	 * @param data
	 * @return
	 * @throws InterruptedException
	 */
	public  String createPhoto(String data) throws InterruptedException{
          Gearman gearman = Gearman.createGearman();  
          //创建一个Gearman client               
          GearmanClient client = gearman.createGearmanClient();  
          
          /*  
           * 创建一个jobserver  
           *   
           * Parameter 1: job server的IP地址  
           * Parameter 2: job server监听的端口  
           *   
           *job server收到client的job，并将其分发给注册worker  
           *  
           */  
          GearmanServer server = gearman.createGearmanServer(  
         		  sbase, Integer.parseInt(prort));  
          // 告诉客户端，提交工作时它可以连接到该服务器  
          client.addServer(server);  
          
          /*  
           * 向job server提交工作  
           *   
           * Parameter 1: gearman function名字  
           * Parameter 2: 传送给job server和worker的数据  
           *   
           * GearmanJobReturn返回job发热结果  
           */  
          GearmanJobReturn jobReturn = client.submitJob(  
                         "image",data.getBytes());  
            
          //遍历作业事件，直到我们打到最后文件               
          String result="";
          while (!jobReturn.isEOF()) {  
                  //下一个作业事件  
                  GearmanJobEvent event = jobReturn.poll();  
                  switch (event.getEventType()) {  
	                  case GEARMAN_JOB_SUCCESS:     //job执行成功  
	                	      result=new String(event.getData());  
	                          break;  
	                  case GEARMAN_SUBMIT_FAIL:     //job提交失败  
	                  case GEARMAN_JOB_FAIL:        //job执行失败  
	                  default:  
                  }  
          }  

          //关闭  
          gearman.shutdown();  
          return result;
	}
	
	
	/**
	 * 获取视频/音频信息
	 * @param data
	 * @return
	 * @throws InterruptedException
	 */
	public JSONObject getMessage(String data) throws InterruptedException{
		 Gearman gearman = Gearman.createGearman();  
        
		 //创建一个Gearman client               
         GearmanClient client = gearman.createGearmanClient();  
         /*  
          * 创建一个jobserver  
          *   
          * Parameter 1: job server的IP地址  
          * Parameter 2: job server监听的端口  
          *   
          *job server收到client的job，并将其分发给注册worker  
          *  
          */  
         GearmanServer server = gearman.createGearmanServer(  
       		  sbase, Integer.parseInt(prort));  

         // 告诉客户端，提交工作时它可以连接到该服务器  
         client.addServer(server);  

         /*  
          * 向job server提交工作  
          *   
          * Parameter 1: gearman function名字  
          * Parameter 2: 传送给job server和worker的数据  
          *   
          * GearmanJobReturn返回job发热结果  
          */  
         GearmanJobReturn jobReturn = client.submitJob(  
                        "data",data.getBytes());  

         //遍历作业事件，直到我们打到最后文件               
         String result="{}";
         while (!jobReturn.isEOF()) {  
                 //下一个作业事件  
                 GearmanJobEvent event = jobReturn.poll();  
                 switch (event.getEventType()) {  
	                 case GEARMAN_JOB_SUCCESS:     //job执行成功  
	               	  result=new String(event.getData());  
	                         break;  
	                 case GEARMAN_SUBMIT_FAIL:     //job提交失败  
	                 case GEARMAN_JOB_FAIL:        //job执行失败  
	                 default:  
                 }  
         }  
         
         if(result==null||"{}".equals(result)){
        	 result="{}";
         }
         //关闭  
         gearman.shutdown(); 
         return JSONObject.fromObject(result);
	}
	
	/**
	 * 调用gearman进行转码
	 * @param name
	 * @param data
	 * @return
	 * @throws InterruptedException
	 */
	 public String toWork(String name,Object data) throws InterruptedException{
		 logger.info("【GEARMAN】开始");
		 logger.info("【GEARMAN】IP="+sbase);
		 logger.info("【GEARMAN】PORT="+prort);
		 Gearman gearman=null;
		 GearmanClient client=null;
         try {
            
        	gearman= Gearman.createGearman();  
             //创建一个Gearman client               
        	client= gearman.createGearmanClient();  
             
             
             String[] ips=sbase.split(";");
             String[] ports=prort.split(";");
             
             if (ips==null||ports==null) {
            	 logger.error("【GEARMAN】配置文件中IP地址或端口号不能为空！");
    		 }else{
    			 if (ips.length==ports.length) {
    				 for (int i = 0; i < ips.length; i++) {
    			        
    					 GearmanServer server = gearman.createGearmanServer(  
    			        		 ips[i], Integer.parseInt(ports[i]));  
    			          // 告诉客户端，提交工作时它可以连接到该服务器  
    			          client.addServer(server);  
    				 }
    			}else{
    				logger.error("【GEARMAN】配置文件中IP地址和端口号数量不匹配！");
    				throw new RuntimeException("【GEARMAN】配置文件中IP地址和端口号数量不匹配！");
    			}
    		 }
             
        	 /*  
             * 向job server提交工作  
             *   
             * Parameter 1: gearman function名字  
             * Parameter 2: 传送给job server和worker的数据  
             *   
             * GearmanJobReturn返回job发热结果  
             */  
        	//submitBackgroundJob
    	    GearmanJobReturn jobReturn = client.submitBackgroundJob(
    			name,data==null?"".getBytes():data.toString().getBytes());
			while (!jobReturn.isEOF()) {
				GearmanJobEvent event = jobReturn.poll();
				switch (event.getEventType()) {
				case GEARMAN_JOB_SUCCESS:
					System.out.println(">>>> "
							+ new String(event.getData(), "utf-8"));
					break;
				case GEARMAN_SUBMIT_FAIL:
					throw new Exception("submit fail");
				case GEARMAN_JOB_FAIL:
					throw new Exception("job fail");
				default:
				}
			}
			
			Thread.sleep(20);
		} catch (Exception e) {
			 logger.error("【GEARMAN】调用gearman转码异常：",e);
			 e.printStackTrace();
		}finally {
			gearman.shutdown(); 
		}
        
         //遍历作业事件，直到我们打到最后文件    
         /*
         String result="";
         while (!jobReturn.isEOF()) { 
        	     System.out.println("while");
                
        	     GearmanJobEvent event = jobReturn.poll();  
                 switch (event.getEventType()) {  
	                 case GEARMAN_JOB_SUCCESS:       
	               	  result=new String(event.getData());  
	                         break;  
	                 case GEARMAN_SUBMIT_FAIL:     
	                       
	                 case GEARMAN_JOB_FAIL:        
	                 default:  
                 }  

         }  
		 */
         
         //关闭  
         //gearman.shutdown();  
         logger.info("【GEARMAN】结束");
         return null;
	}
}

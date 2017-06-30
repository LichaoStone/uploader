package Utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class RedisClusterFactory {
	private static final Logger logger = Logger.getLogger(RedisClusterFactory.class); 
	static Set<HostAndPort> jedisClusterNodes =null;
	static Properties pro = new Properties();
	
	static{
		String fileName = "/redis.properties";
		InputStream in = RedisClusterFactory.class.getClassLoader().getResourceAsStream(fileName);
		try {
			pro.load(in);
			in.close();
		} catch (IOException e) {
			logger.error("静态化加载redis.properties失败：",e);
		}
	}	
	
	/**
	 * 初始化redis连接
	 */
	public static void  init(){
		try {
			   String fileName = "/redis.properties";
	    	   Properties p = new Properties();
	    	   InputStream in = RedisClusterFactory.class.getClassLoader().getResourceAsStream(fileName);
    		
    		   p.load(in);
    		   in.close();
    			
    		   jedisClusterNodes=new HashSet<HostAndPort>();
        	   String addr = "";
        	   int port = 0;
        	   if (p.containsKey("addr")) {
        		  addr = p.getProperty("addr");
        	   }
        	   if (p.containsKey("port")) {
        		  port = Integer.parseInt(p.getProperty("port"));
        	   }
        	   jedisClusterNodes.add(new HostAndPort(addr, port));
    		
		    } catch (IOException e) {
    			logger.error("初始化redis链接失败：",e);
    		}
		}
		
	    /**
	     * 获取redis连接 
	     * @return
	     */
	    public static Jedis  getJedisClu(){
	    	String addr=pro.getProperty("addr");
	    	Integer port=Integer.parseInt(pro.getProperty("port"));
	    	logger.info("【redis】addr="+addr);
	    	logger.info("【redis】port="+port);
			return new Jedis(addr,port,0);
		} 
		
	    /**
	     * 关闭redis连接
	     * @param jc
	     */
		public static void close(Jedis jc){
			jc.close();
		}
}

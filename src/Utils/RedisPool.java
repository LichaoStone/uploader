/*package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

*//**
 * redis连接池
 * @author thatway
 * 2015-10-26
 *//*
public class RedisPool {
	
    private static JedisPool jedisPool = null;

	private static Logger log = Logger.getLogger(RedisPool.class);
    *//**
     * 初始化Redis连接池
     *//*
    public static JedisPool getPool(){
    	
    	if(null == jedisPool){
    		
    		String fileName = "redis.properties";
    		Properties p = new Properties();
    		
    		InputStream in = RedisPool.class.getClassLoader().getResourceAsStream(fileName);
    		try {
    			p.load(in);
    			in.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		
    		String addr = "";
    		int port = 0;
    		//String auth = "";
    		
    		int max_active = 0;
    		int max_idle = 0;
    		int max_wait = 0;
    		int time_out = 0;
    		boolean test_on_borrow = false;
    		
    		if (p.containsKey("addr")) {
    			addr = p.getProperty("addr");
    		}
    		
    		if (p.containsKey("port")) {
    			port = Integer.parseInt(p.getProperty("port"));
    		}
    		
//    		if (p.containsKey("auth")) {
//    			auth = p.getProperty("auth");
//    		}
    		
    		if (p.containsKey("max_active")) {
    			max_active = Integer.parseInt(p.getProperty("max_active"));
    		}
    		
    		if (p.containsKey("max_idle")) {
    			max_idle = Integer.parseInt(p.getProperty("max_idle"));
    		}
    		
    		if (p.containsKey("max_wait")) {
    			max_wait = Integer.parseInt(p.getProperty("max_wait"));
    		}
    		
    		if (p.containsKey("time_out")) {
    			time_out = Integer.parseInt(p.getProperty("time_out"));
    		}
    		
    		if (p.containsKey("test_on_borrow")) {
    			test_on_borrow = Boolean.parseBoolean(p.getProperty("test_on_borrow"));
    		}
        	
            try {
                JedisPoolConfig config = new JedisPoolConfig();
                
                config.setMaxActive(max_active);
                config.setMaxIdle(max_idle);
                config.setMaxWait(max_wait);
                config.setTestOnBorrow(test_on_borrow);
                
                //jedisPool = new JedisPool(config, addr, port, time_out, auth);
                jedisPool = new JedisPool(config, addr, port, time_out);
                
            } catch (Exception e) {
            	log.info("redis-->初始化失败");
                e.printStackTrace();
            }
    	}
    	
        return jedisPool;
    }
    
    *//**
     * 获取Jedis实例
     * @return
     *//*
    public synchronized static Jedis getJedis() {
    	
    	JedisPool pool = RedisPool.getPool();
    	Jedis resource = null;
    	
    	 if (pool != null) {
    		 try {
    			 resource = pool.getResource();
    	     } catch (Exception e) {
    	    	
    	    	//释放redis对象  
    	    	 pool.returnBrokenResource(resource);
    	    	
    	        e.printStackTrace();
    	    	log.info("redis-->获取链接失败超时");
    	     }
         } 
    	
        return resource;
    }
    
    *//**
     * 释放jedis资源
     * @param jedis
     *//*
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
        	JedisPool pool = RedisPool.getPool();
        	pool.returnResource(jedis);
        }
    }
}
*/
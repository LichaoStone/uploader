package Utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/*
public class JR {
	public static Jedis getJd(){
		return RedisPool.getJedis();
	}
	public static void close(Jedis jedis){
		RedisPool.returnResource(jedis);
	}
	
}
*/
public class JR {
	public static Jedis getJd(){
		return RedisClusterFactory.getJedisClu();
	}
	public static void close(Jedis jedis){
		RedisClusterFactory.close(jedis);
	
	}
}
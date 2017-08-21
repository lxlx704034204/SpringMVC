package com.mark.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @ClassName:RedisCachePool.java
 * @Description:
 * @author gaoguangjin
 * @Date 2015-5-25 下午2:30:33
 */
public class RedisCachePool {
	
	private JedisPool jedisPool = null;
	private Integer db;
	//							  0
	public RedisCachePool(Integer db, JedisPool jedisPool) {
		this.db = db;
		this.jedisPool = jedisPool;
	}
	public RedisCachePool(){}
	public Jedis getResource() {
		System.out.print("---->-test-11.0-:"+jedisPool +"\n" ); //redis.clients.jedis.JedisPool@62a8fd44
		Jedis jedisInstance = null;
		if (jedisPool != null) {
			try{
				System.out.print("---->-test-11.1-" +"\n" );
				jedisInstance = jedisPool.getResource();
			}catch (Exception e){
				System.out.print("---->-test-11.2-"+ e.toString() +"\n" );
			}
			System.out.print("---->-test-11.3-:"+jedisInstance.toString() +"\n" );
			if (db > 0) {// 每次获得连接之前，切换到对应的数据库。默认的是0
				jedisInstance.select(db);
			}
		}
		System.out.print("---->-test-11.9-" +"\n" );
		return jedisInstance;
	}
	
	/**
	 * 释放jedis资源
	 * @param jedis
	 */
	public void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}
}

package com.mark.test;

import com.mark.redis.util.RedisCacheManager;
import com.mark.redis.util.RedisCachePool;
import com.mark.redis.util.RedisDataBaseType;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ConcurrentHashMap;


/**
 * @ClassName:MonitorSql.java
 * @Description: 监控对redis的更新和删除
 * @author gaoguangjin
 * @Date 2015-5-21 上午10:32:23
 */
@Slf4j
public class MonitorSql {

	public static void main(String[] args) {
		System.out.print("---->-test-0-" +"\n" );
		ApplicationContext application = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.print("---->-test-1-:"+application +"\n" );//org.springframework.context.support.ClassPathXmlApplicationContext@f317541: startup date [Tue Aug 01 16:58:02 CST 2017]; root of context hierarchy
		RedisCacheManager redisCacheManager = (RedisCacheManager) application.getBean("redisCacheManager");
		System.out.print("---->-test-2-:"+redisCacheManager +"\n" );//com.mark.redis.util.RedisCacheManager@6f2e17a2
		ConcurrentHashMap<String, RedisCachePool> concurrentHashMap = redisCacheManager.getRedisPoolMap();
		System.out.print("---->-test-3-:"+concurrentHashMap +"\n" );// {}
		RedisCachePool pool = concurrentHashMap.get(RedisDataBaseType.defaultType.toString());
		System.out.print("---->-test-4-:"+pool +"\n" );//null



		try{
			System.out.print("---->-test-10-:" +"\n" );
			System.out.print("---->-test-11-:"+ pool  +"\n" ); //null
			final Jedis jedis = pool.getResource();
			System.out.print("---->-test-12-:" + jedis +"\n" );

			final JedisPubSub ndb = (JedisPubSub) application.getBean("notifyDataBase");
			// final Jedis jedis = RedisPool.getJedis();
			// final JedisPubSub ndb = new NotifyDataBase();
			new Thread() {
				public void run() {// 会广播形式打印log日志
					jedis.subscribe(ndb, "publog");
				}
			}.start();

		}catch (Exception e){
			System.out.print("---->-test-19-:" + e.getMessage() +"\n");
		}


	}
}


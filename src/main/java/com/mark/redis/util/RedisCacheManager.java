package com.mark.redis.util;

import java.util.concurrent.ConcurrentHashMap;

import com.mark.java.entity.Note;
import com.mark.java.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service("redisCacheManager")
@Slf4j
public class RedisCacheManager {
	@Value("${redisdbtype}")
	private String redisdbtype;//"defaultType,"
	
	@Value("${redisdbnumber}")
	private String redisdbnumber;//"0,"
	
	@Value("${host}")
	private String host;
	@Value("${port}")
	private int port;
	@Value("${timeout}")
	private int timeout;
	@Value("${passwords}")
	private String passwords;
	
	@Value("${maxtotal}")
	private String maxtotal;
	@Value("${maxidle}")
	private String maxidle;
	@Value("${minidle}")
	private String minidle;
	@Value("${maxwaitmillis}")
	private String maxwaitmillis;
	@Value("${testonborrow}")
	private String testonborrow;
	@Value("${testwhileidle}")
	private String testwhileidle;
	
	private static JedisPoolConfig poolConfig = null;

	private static JedisPool jedisPool = null;
	
	// 保存不同的数据库连接	ConcurrentMap中不能保存value为null的值
	private ConcurrentHashMap<String, RedisCachePool> redisPoolMap = new ConcurrentHashMap<String, RedisCachePool>();
	private ConcurrentHashMap<String, String> redisPoolMap2 = new ConcurrentHashMap<String, String>();

	public ConcurrentHashMap<String, RedisCachePool> getRedisPoolMap() {
		if (redisPoolMap.size() < 1) { //redisPoolMap.size() = 0
			initConfig();
			initPoolMap();
		}
		return redisPoolMap;
	}
	
	/**
	 * @Description:共享的poolconfig
	 * @return:void
	 */
	private void initConfig() {  // redis.properties文件
		poolConfig = new JedisPoolConfig();
		poolConfig.setTestOnBorrow(testwhileidle.equals("true") ? true : false);//true
		poolConfig.setTestWhileIdle(testonborrow.equals("true") ? true : false);//true
		poolConfig.setMaxIdle(Integer.parseInt(maxidle));	//20
		poolConfig.setMaxTotal(Integer.parseInt(maxtotal));	//36
		poolConfig.setMinIdle(Integer.parseInt(minidle));	//5
		poolConfig.setMaxWaitMillis(Integer.parseInt(maxwaitmillis));//1000
	}


	private void initPoolMap() {
		try {  //		"defaultType,"			"0,"
			if (null != redisdbtype && null != redisdbnumber) {
				String[] dbs = redisdbtype.split(",");
				String[] numbers = redisdbnumber.split(",");
				System.out.print("---->-test-22-:"+ dbs.length +"\n" );// 1
				for (int i = 0; i < dbs.length; i++) {
					// 得到redis连接池对象					  127.0.0.1  6379   10000	 msds
					 jedisPool = new JedisPool(poolConfig, host, port, timeout);//, passwords 注意:redis没有设置密码的话,这里不要密码参数项

					System.out.print("---->-test-22.1-:"+ jedisPool +"\n" );// 1

					RedisCachePool redisCachePool = new RedisCachePool(Integer.parseInt(numbers[i]), jedisPool);
					System.out.print("---->-test-23-:"+redisCachePool.toString() +"\n" );//
					System.out.print("---->-test-24-:"+redisPoolMap.size() +"\n" );//
					System.out.print("---->-test-25-:"+dbs[i] +"\n" );//
					// 存放不同redis数据库
					redisPoolMap.put(dbs[i], redisCachePool);
					redisPoolMap2.put("key_haha", "value_haha");//不添加这句的话，上句就无法执行--- 最终测试：改成jdk1.8就好了

				}
				System.out.print("---->-test-25.1-:"+ redisPoolMap.size() +"\n" );//
			}
		} catch (Exception e) {
			System.out.print("---->-test-26-:"+ e.toString() +"\n" );//
			log.error("redisCacheManager初始化失败！" + e.getLocalizedMessage());
		}
	}

	public  Jedis getJedisTest() { //synchronized
		initConfig();
		Jedis jedis = null;
		System.out.println("---x0.1--getJedisTest()--: "+ poolConfig );//
		JedisPool jedisPool2 = new JedisPool(poolConfig, host, port, timeout); //, passwords 注意:redis没有设置密码的话,这里不要密码参数项
		System.out.println("---x0.2--getJedisTest()--: "+ jedisPool2 );//
		try {
			if (null != jedisPool2) {
				jedis = jedisPool2.getResource();
				System.out.println("---x0.3--getJedisTest()--: "+ jedis );//
				try {
					System.out.println("-0--before of jedis.auth(PASSWORD): "+jedis.ping());//
//                    jedis.auth(PASSWORD); // no password is set
					//ping通显示PONG
					System.out.println("-0.1--redis connect sucessfully-Server is running: "+jedis.ping());//去ping我们redis的主机所在ip和端口
				} catch (Exception e) {
					System.out.println("-0.2--jedis--e.toString(): "+e.toString());//
//					_log.error("jedis.auth(PASSWORD) Exception: " + e);
				}
			}
		} catch (Exception e) {
			System.out.println("-0.3--redis connect failure-1 !: "+ e.toString());
//			_log.error("Get jedis error : " + e);
		}
		return jedis;
	}


	/**
	 * @Description: 得到jedis连接
	 * @param dbtypeName
	 * @return:Jedis
	 */
	public Jedis getResource(RedisDataBaseType dbtypeName) {
		Jedis jedisResource = null;
		RedisCachePool pool = redisPoolMap.get(dbtypeName.toString());
		if (pool != null) {
			jedisResource = pool.getResource();
		}
		return jedisResource;
	}
	
	/**
	 * @Description: 返回连接池
	 * @param dbtypeName
	 * @param jedis
	 * @return:void
	 */
	public void returnResource(RedisDataBaseType dbtypeName, Jedis jedis) {
		RedisCachePool pool = redisPoolMap.get(dbtypeName.toString());
		if (pool != null)
			pool.returnResource(jedis);
	}
}

package com.mark.redis.synchronize;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.mark.redis.dao.RedisDao;
import com.mark.redis.util.RedisCacheManager;
import com.mark.redis.util.RedisCachePool;
import com.mark.redis.util.RedisDataBaseType;

/**
 * @ClassName:NotifyDataBase.java
 * @Description: pub/sub �첽������
 * @author gaoguangjin
 * @Date 2015-5-22 ����10:13:29
 */
@Slf4j
@Service("notifyDataBase")
public class NotifyDataBase extends JedisPubSub {
	@Autowired
	RedisUpdateToDataBase redisUpdateToDataBase;

	@Autowired
	RedisCacheManager redisCacheManager;

	@Override
	public void onMessage(String channel, String sql) {
		log.info("redis����ת�������ݿ�==��sql:" + sql);

		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		final Jedis jedis = pool.getResource();

		Long length = jedis.llen(RedisDao.LOG);
		int n = 2;// ���log��list size �ﵽn ��ʱ���һ����ִ�и��¡����Ե�ʱ���Ū��2
		List<String> list = new ArrayList<String>();
		if (length >= n) {
			for (int i = 0; i < n; i++) {
				String sqlStr = jedis.lpop(RedisDao.LOG);// ɾ��list��Ԫ��
				list.add(sqlStr);
			}

			// �Ƿ�ִ�гɹ�
			boolean flag = redisUpdateToDataBase.excuteUpdate(list);
			if (!flag) {
				for (String oldSql : list) {
					jedis.lpush(RedisDao.LOG, oldSql);// ����ʧ��������ӵ�list����
				}
			}
		}
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		log.info("onPMessage");
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		log.info("��ʼ���redis�仯��");

	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		log.info("onUnsubscribe");

	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		log.info("onPUnsubscribe");

	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		log.info("onPSubscribe");
	}

}
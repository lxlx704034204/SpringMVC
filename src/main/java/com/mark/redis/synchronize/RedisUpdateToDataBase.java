package com.mark.redis.synchronize;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName:RedisUpdateToDataBase.java
 * @Description: redis�첽���µ����ݿ�
 * @author gaoguangjin
 * @Date 2015-5-22 ����10:09:48
 */
@Service
@Slf4j
@Transactional
public class RedisUpdateToDataBase {
	@Autowired
	SessionFactory sessionFactory;

	public boolean excuteUpdate(List<String> list) {
		try {
			Session session = sessionFactory.getCurrentSession();
			for (String string : list) {
				session.createSQLQuery(string).executeUpdate();
			}
			log.info("redis���µ����ݿ�ɹ���");
			return true;
		} catch (Exception e) {
			log.info("redis���µ����ݿ�ʧ�ܣ�" + e.getLocalizedMessage());
		}
		return false;
	}
}
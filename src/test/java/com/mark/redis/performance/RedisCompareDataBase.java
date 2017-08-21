package com.mark.redis.performance;

import java.util.List;

import com.mark.java.entity.Note;
import com.mark.java.service.BaseService;
import com.mark.java.service.NoteService;
import com.mark.redis.util.RedisCacheManager;
import com.mark.redis.util.RedisCachePool;
import com.mark.redis.util.RedisDataBaseType;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import redis.clients.jedis.Jedis;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" }) // "classpath:spring-context.xml"
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
public class RedisCompareDataBase {

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	RedisCacheManager redisCacheManager;

	@Qualifier("NoteServiceImp")
	@Autowired
	BaseService baseService;

	@Autowired
	NoteService noteService;

	 @Test
	 @Transactional
	public void selectAll() {
		long time1 = System.currentTimeMillis();
		Query query = sessionFactory.getCurrentSession().createQuery("from Note");
		List<Note> objectList = query.list(); 		// ֱ����redis���ò�ѯ
		for (Note note : objectList) {
			log.info(note.toString());
		}
		long time2 = System.currentTimeMillis();

		List<Note> noteList = baseService.findAll(); // jdbc ��ѯ
		for (Note note : objectList) {
			log.info(note.toString());
		}
		long time3 = System.currentTimeMillis();

		log.info("jdbc��ѯ  ���ݴ�С" + noteList.size() + "��ʱ��" + (time2 - time1));
		log.info("redis��ѯ ���ݴ�С" + objectList.size() + " ��ʱ��" + (time3 - time2));

	}

	// @Test
	// @Transactional
	public void selectOne() {
		long time1 = System.currentTimeMillis();
		Session session = sessionFactory.getCurrentSession();
		for (int i = 1; i < 100; i++) {
			Note note = (Note) session.get(Note.class, i);
			log.info(note.toString());
		}
		long time2 = System.currentTimeMillis();

		for (int i = 1; i < 100; i++) {
			Note note2 = noteService.queryById(i + "");
			log.info(note2.toString());
		}
		long time3 = System.currentTimeMillis();

		log.info("jdbc��ѯ  �ݵ������ݺ�ʱ��" + (time2 - time1));
		log.info("redis��ѯ �������� ��ʱ��" + (time3 - time2));
	}

	// redis��ѯ�������ݵ�ĳ���ֶ�ֵ��ѭ��100�������Ŵ����� jdbc��ѯ �������ݵ�ĳ���ֶ�ֵ��ʱ��375||redis��ѯ �������ݵ�ĳ���ֶ�ֵ ��ʱ��207
	// @Test
	// @Transactional
	public void selectParam() {
		long time1 = System.currentTimeMillis();
		Session session = sessionFactory.getCurrentSession();
		for (int i = 1; i < 100; i++) {
			Note note = (Note) session.get(Note.class, i);
			log.info(note.getNoteName());
		}
		long time2 = System.currentTimeMillis();

		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		Jedis jedis = pool.getResource();

		for (int i = 1; i < 100; i++) {
			log.info(jedis.get("Note:" + i + ":noteName"));
		}
		long time3 = System.currentTimeMillis();

		log.info("jdbc��ѯ  �������ݵ�ĳ���ֶ�ֵ��ʱ��" + (time2 - time1));
		log.info("redis��ѯ �������ݵ�ĳ���ֶ�ֵ ��ʱ��" + (time3 - time2));
	}

	// 1706 //1815
	@Test
	@Transactional
	public void update() {
		long time1 = System.currentTimeMillis();
		Session session = sessionFactory.getCurrentSession();
		Note note = (Note) session.get(Note.class, 1);
		if (null != note) {
			note.setAuthorName("�ž���1");
			note.setFromUrl("www.ggjlovezjy.com:13141");
		}
		session.save(note);
		long time2 = System.currentTimeMillis();

		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		Jedis jedis = pool.getResource();
		Note note2 = noteService.queryById("1");
		if (null != note2) {
			note.setAuthorName("�ž���2");
			note.setFromUrl("www.ggjlovezjy.com:13142");
		}
		baseService.update(note2);
		long time3 = System.currentTimeMillis();

		log.info("jdbc �������ݵ�ĳ���ֶ�ֵ��ʱ��" + (time2 - time1));
		log.info("redis �������ݵ�ĳ���ֶ�ֵ ��ʱ��" + (time3 - time2));
	}
}

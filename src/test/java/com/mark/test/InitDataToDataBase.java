package com.mark.test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import com.mark.java.entity.Note;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" }) // "classpath:spring-context.xml"
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
/**
 * @ClassName:InitDataToDataBase.java
 * @Description: ��ʼ����վ���ݵ����ݿ�
 * @author gaoguangjin
 * @Date 2015-5-21 ����9:55:48
 */
public class InitDataToDataBase extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private SessionFactory sessionFactory;

	// ���url ��title
	public ConcurrentHashMap<String, String> currentHashMap = new ConcurrentHashMap<String, String>();

	@Test
	@Transactional
	public void test() throws IOException {
		final Session session = sessionFactory.getCurrentSession();
		ExecutorService pool = Executors.newFixedThreadPool(10);
		try {
			for (int i = 0; i < 20; i++) {// �ܹ�20Ҳ ����20���߳�ȥ��ȥ����
				pool.execute(new PutArticelUrlByPage(currentHashMap, i));
			}
			pool.shutdown();
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);// �̳߳������߳�����ִ����֮��ִ������

			// ����map��������ӣ����浽���ݿ⡣�����1123��url
			System.out.println("map��С��" + currentHashMap.size());
			long beginTime = System.currentTimeMillis();

			// ��2�����̲߳��롢CountDownLatch�����������ֹsession�رյ�����
			CountDownLatch latch = new CountDownLatch(currentHashMap.size());
			List<Note> listNote = new StoreToDataBaseByThread(currentHashMap, session, latch).insertToDatabase();
			latch.await();
			// ��Ϊ�õ���durid���ӳأ���ȡ���ݿ����Ӻʹ���jdbc ����Ҫ��һ���߳����档���Բ��ô��б���
			for (Note note : listNote) {
				session.save(note);
			}

			long endTime = System.currentTimeMillis();
			log.info("�������ݿ��ʱ��" + (endTime - beginTime) + "ms");

			System.out.println("end");
		} catch (InterruptedException e) {
			log.error("" + e.getLocalizedMessage());
		}
	}
}

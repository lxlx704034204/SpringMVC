package com.mark.test;

import java.util.List;
import java.util.Set;

import com.mark.redis.synchronize.NotifyDataBase;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import com.mark.java.entity.Note;
import com.mark.java.service.BaseService;
import com.mark.java.service.NoteService;
import com.mark.redis.util.RedisCacheManager;
import com.mark.redis.util.RedisCachePool;
import com.mark.redis.util.RedisDataBaseType;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" }) // "classpath:spring-context.xml" //,"spring-dispatcherservlet.xml","dubbo-provider.xml"
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
/**
 *
 * @ClassName:JuitTest.java
 * @Description:   redis����
 * @author gaoguangjin
 * @Date 2015-5-21 ����10:03:00
 */
public class JuitTest {
	@Autowired
	RedisCacheManager redisCacheManager;
	@Qualifier("NoteServiceImp")
	@Autowired
	BaseService baseService;
	@Autowired
	NoteService noteService;

	// ������־��������MonitorSql ����� ��Ϊ��Ԫ����������߳��޷�����
	// @Before
	public void before() {
		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		final Jedis jedis = pool.getResource();
		final JedisPubSub ndb = new NotifyDataBase();
		new Thread() {
			public void run() {// ��㲥��ʽ��ӡlog��־
				jedis.subscribe(ndb, "publog");
			}
		}.start();
	}

	@Test
	public void redisConnetTest_diy() {
		redisCacheManager.getJedisTest();
	}

	// ��ѯ�������ݡ�redis�ͷ�������ͬһ��������
	 @Test
	public void findAll() {
		 System.out.print("--1-->--@Test-findAll()-:" +"\n" ); //
		log.info("-test-2-" );
		long time = System.currentTimeMillis();
		List<Note> list = baseService.findAll();
		 System.out.print("--2-->--@Test-findAll()-:"+list.size() +"\n" ); //

		for (Note note : list) {
			System.out.print("---->-findAll()-0-"+note.getNoteName() +"\n" );
			log.info(note.toString());
		}
		long time2 = System.currentTimeMillis();
		log.info("��ʱ" + (time2 - time));// 9790
	}

	// ��ѯ��������
	// @Test
	public void findOne() {
		String id = "1";
		Note note = noteService.queryById(id);
		log.info(note.toString());
	}

	@Test
	public void insert() throws Exception {
		Note note = new Note();
		note.setFlag(0);
		note.setFromUrl("www.ggjlovezjy.com:1314");
		note.setNoteName("���Բ���");
		note.setAuthorName("�߹����Բ���");
		// baseService.insert(note);

		// List<Object> noteList = new ArrayList<Object>();
		// noteList.add(note);
		// Transaction tx =
		// redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString()).getResource().multi();
		//
		// RedisDao da = new RedisDao(tx);
		// da.insertListToredis(noteList);
		// tx.exec();

	}

	// ��ѯ��������
	// @Test
	public void findByParam() {
		Note note = new Note();
		note.setAuthorName("�ž���");
		note.setFromUrl("http://www.tuicool.com/");
		List<Note> noteList = noteService.queryParamAnd(note);

		for (Note list : noteList) {
			log.info(list.toString());
		}
	}

	/**
	 * @Description: ����ɾ��
	 */
	// @Test
	public void delete() {
		for (int i = 0; i < 2; i++) {
			baseService.delete(i + "");
		}
	}

	/**
	 * @Description: ���Ը��¡�������Ҫע���ϸ�ھ��ǣ��ȴ�redis�����ѯ������ֵ��Ȼ�����������޸ġ�
	 */
	// @Test
	public void update() {
		String id = "2";
		Note note = noteService.queryById(id);
		note.setAuthorName("�ž���");
		note.setFromUrl("www.ggjlovezjy.com:1314");
		baseService.update(note);
	}

	@Test
	public void after() {
		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		Jedis jedis = pool.getResource();
		log.info("======ɾ��֮���ӡ===========");
		display(jedis);
		pool.returnResource(jedis);
	}
	private void display(Jedis jedis) {
		Set<String> aa = jedis.smembers("Note:createdate:2015-05-20 01:04:13.0");
		Set<String> bb = jedis.smembers("Note:fromUrl:http://www.tuicool.com/articles/vquaei");
		Set<String> cc = jedis.smembers("Note:flag:0");
		Set<String> dd = jedis.smembers("Note:authorName:�߹��");
		for (String string1 : aa) {
			log.info("��֤a" + string1);// �������ظ���
		}
		for (String string2 : bb) {
			log.info("��֤b" + string2);
		}
		for (String string3 : cc) {
			log.info("��֤c" + string3);
		}
		for (String string4 : dd) {
			log.info("��֤d" + string4);
		}
	}
}

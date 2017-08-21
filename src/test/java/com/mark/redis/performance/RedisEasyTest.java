package com.mark.redis.performance;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

/**
 * @ClassName:RedisEasyTest.java
 * @Description: redis���ܲ��ԡ�10��������
 * @see:1����ԭ��̬��jedis ����������������
 * @see:2��������transaction 33
 * @see:3����pipelined 9��
 * @see:4����pipelined ���濪������ Ч�ʺ� transactionһ��
 * @author gaoguangjin
 * @Date 2015-5-27 ����4:33:21
 */
@Slf4j
public class RedisEasyTest {
	private static Jedis jedis = new Jedis( "127.0.0.1");//10.1.9.231
	private static Jedis jedis2 = new Jedis("127.0.0.1");//10.1.9.231
	private static Jedis jedis3 = new Jedis("127.0.0.1");//10.1.9.231
	private static Pipeline p = jedis.pipelined();

	// 10��������
	private static int KEY_COUNT_1 = 100000;
	private static int KEY_COUNT_2 = 200000;
	private static int KEY_COUNT_3 = 300000;
	private static int KEY_COUNT_4 = 400000;

//	public static void main(String[] args) {
//		jedis = new Jedis("127.0.0.1", 6379);
//		jedis.auth("msds");
//		System.out.println("---->-test-1-: "+jedis.ping());
//	}

	public static void main(String[] args) {
//		jedis.auth("msds");
		jedis.flushDB();

//		jedis2.auth("msds");
		Transaction transaction = jedis2.multi();

//		jedis3.auth("msds");
		Pipeline pipeline = jedis3.pipelined();
		pipeline.multi();

		// ��Ҫ��ע�ʹ򿪣���������Ҫ��������
		long start = System.currentTimeMillis();
		// jedis();
		// System.out.printf("jedis use %d sec \n", (System.currentTimeMillis() - start) / 1000);

		start = System.currentTimeMillis();
		transation(transaction);
		System.out.printf("transation use %d sec \n", (System.currentTimeMillis() - start) / 1000);

		start = System.currentTimeMillis();
		piple();
		System.out.printf("batch piple use %d sec \n", (System.currentTimeMillis() - start) / 1000);

		// start = System.currentTimeMillis();
		// pipleWithTransation(pipeline);
		// System.out.printf("batch piple transation use %d sec \n", (System.currentTimeMillis() - start) / 1000);

	}

	private static void pipleWithTransation(Pipeline pipeline) {
		for (int i = KEY_COUNT_3; i < KEY_COUNT_4; i++) {
			pipeline.set("pipletransation" + i, i + "");
		}
		pipeline.exec();
	}

	private static void piple() {
		for (int i = KEY_COUNT_2; i < KEY_COUNT_3; i++) {
			p.set("piple" + i, i + "");
		}
		p.sync();
	}

	private static void transation(Transaction transaction) {
		for (int i = KEY_COUNT_1; i < KEY_COUNT_2; i++) {
			transaction.set("piple" + i, i + "");
		}
		transaction.exec();

	}

	private static void jedis() {
		for (int i = 0; i < KEY_COUNT_1; i++) {
			jedis.set("normal" + i, i + "");
		}

	}

}

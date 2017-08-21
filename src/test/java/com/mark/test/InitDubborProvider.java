package com.mark.test;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;


/**
 * @ClassName:InitDubborProvider.java
 * @Description: ����dubbo����
 * @author gaoguangjin
 * @Date 2015-5-20 ����12:56:10
 * http://127.0.0.1:1111
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml", "classpath:dubbo-provider.xml" }) // "classpath:spring-context.xml"
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
public class InitDubborProvider {
	@Test// ��������
	public void provider() throws IOException {
		System.in.read(); // Ϊ��֤����һֱ���ţ�������������������ģ��
	}
}

package com.mark.java.service;

import java.util.List;


public interface BaseService<T> {
	/* ��ѯ���� */
	public List<T> findAll();

	/* ɾ�� */
	void delete(String id);

	/* ���� */
	void update(T t);

	/**/
	void insert(T t);
}

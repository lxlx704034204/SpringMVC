package com.mark.redis.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import com.alibaba.fastjson.JSON;
import com.mark.redis.annation.RedisFieldNotCache;
import com.mark.redis.annation.RedisQuery;
import com.mark.redis.util.BeanField;

/**
 * @ClassName:RedisDao.java
 * @Description: redisDao����API
 * @author gaoguangjin
 * @Date 2015-5-19 ����11:27:29
 */
public class RedisDao {
	// �ָ���
	private final static String SPLIT_MARK = ":";
	// ����key����ı��λ
	private final static String SORT = "sort";
	// ����key����ı��λ
	private final static String INDEX = "index";
	// list��ʽ���log��sql
	public final static String LOG = "log";
	// pub/subģʽ��ӡlog
	public final static String PUB_LOG = "publog";

	private Jedis jedis;
	// ����
	private Transaction transaction;

	// �ܵ�
	private Pipeline pipeline;

	/**
	 * redis����һ������֮�����е�������Ŷ�һ���������棬��������ִ�С�
	 */
	public RedisDao(Transaction transaction) {
		this.transaction = transaction;
	}

	/**
	 * ĳЩ��ѯ�������ò������
	 */
	public RedisDao(Jedis jedis) {
		this.jedis = jedis;
	}

	/**
	 * �ܵ�+����Ч�ʵ���Transaction��Ŀǰû�õ������ĳЩ��������Ҫ������ƣ������õ��ܵ�
	 */
	public RedisDao(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	/**
	 * @Description�Ƚ�key�洢��value �ʹ����value�Ƿ����
	 * @param key
	 * @return:Boolean
	 */
	public Boolean existValueByKey(String key, String value) {
		return jedis.get(key) == value ? true : false;
	}

	/**
	 * @Description:����key����ֵ
	 * @param key
	 * @param jedis
	 * @return:String
	 */
	public static String get(String key, Jedis jedis) {
		return jedis.get(key);
	}

	/**
	 * @Description:set string����
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		transaction.set(key, value);
	}

	/**
	 * @Description: ���set����
	 * @param key
	 * @param value
	 */
	public void sadd(String key, String value) {
		transaction.sadd(key, value);
	}

	/**
	 * @Description: ���sortset����
	 * @param key
	 * @param score
	 * @param member
	 */
	public void zadd(String key, double score, String member) {
		transaction.zadd(key, score, member);
	}

	/**
	 * @Description: ���list����
	 * @param key
	 * @param value
	 */
	public void lpush(String key, String value) {
		transaction.lpush(key, value);

	}

	/**
	 * @Description: �������� key �У�ָ�������ڵĳ�Ա�� ���մ�С��������
	 * @param key keyֵ Note:sort:noteId 0 -1
	 * @param start ��ʼλ��
	 * @param end ����λ��
	 * @param jedis
	 * @return:Set<String>
	 */
	public static Set<String> getRangeSortSet(String key, int start, int end, Jedis jedis) {
		return jedis.zrange(key, start, end);
	}

	/**
	 * @Description: �������� key �У�ָ�������ڵĳ�Ա�� ���մӴ�С����
	 * @see: ����������������ʱ���ȡ������id
	 * @param key keyֵ Note:sort:noteId 0 0
	 * @param start ��ʼλ��
	 * @param end ����λ��
	 * @param jedis
	 * @return:Set<String>
	 */
	public static Set<String> getRevrangeSortSet(String key, int start, int end, Jedis jedis) {
		return jedis.zrevrange(key, start, end);
	}

	/**
	 * @Description:����key��set���ϣ����ض��key��Ӧ��stringList��
	 * @param sortKey
	 * @param jedis
	 * @return:List<String>
	 */
	public static List<String> getListString(Set<String> sortKey, Jedis jedis) {
		List<String> list = new ArrayList<String>();
		for (String key : sortKey) {
			list.add(jedis.get(key));
		}
		return list;
	}

	/**
	 * @Description: ɾ��string��������
	 * @param key
	 */
	public void delString(String key) {
		transaction.del(key);
	}

	/**
	 * @Description: ɾ��set��������
	 * @param key
	 */
	public void delSet(String key, String member) {
		transaction.srem(key, member);
	}

	/**
	 * @Description: ɾ��sortset���͵�����
	 * @param key
	 * @param member
	 */
	public void delSortSet(String key, String member) {
		transaction.zrem(key, member);
	}

	/**
	 * @Description:ģ����ѯkeyֵ
	 * @param pattern
	 * @return:Set<String>
	 */
	public Set<String> keys(String pattern) {
		return jedis.keys(pattern);
	}

	/**
	 * @Description:����keyֵ����set����
	 * @param key
	 * @return:Set<String>
	 */
	public Set<String> smembers(String key) {
		return jedis.smembers(key);
	}

	/**
	 * @Description:���ݶ��keyֵ���ؽ�����id
	 * @param key
	 * @return:Set<String>
	 */
	public Set<String> sinter(String... key) {
		return jedis.sinter(key);
	}

	/**
	 * @Description:���ݶ��keyֵ ���ز�����id
	 * @param key
	 * @return:Set<String>
	 */
	public Set<String> sunion(String... key) {
		return jedis.sunion(key);
	}

	/**
	 * @Description:��ʼ�����������ݷŵ�redis,��Ÿ�ʽΪtableName:id:column.
	 * @param tableName ��ӳ�������
	 * @param id ����IDֵ
	 * @param column ��ӳ�������Ϣ
	 * @param value �ж�Ӧ��ֵ
	 */
	public void setTable(String tableName, String id, String column, String value) {
		transaction.set(tableName + SPLIT_MARK + id + SPLIT_MARK + column, value);
	}

	/**
	 * @Description:��ʼ����ű��������ֶ����ݣ���Ÿ�ʽΪtableName:column:columnValue
	 * @  tableName
	 * @  column
	 * @  value
	 * @  id
	 */
	public void saddColumn(String tableName, String column, String columnValue, String id) {
		transaction.sadd(tableName + SPLIT_MARK + column + SPLIT_MARK + columnValue, tableName + SPLIT_MARK + id);
	}

	/**
	 * @Description:��ʼ�����������ݷŵ�redis,��beanת����json��ʽ����Ÿ�ʽΪtableName:id��
	 * @param tableName ��ӳ�������
	 * @param id ����idֵ
	 * @param json ��idֵ��Ӧ��json�ַ���
	 */
	public void setJSON(String tableName, String id, String json) {
		transaction.set(tableName + SPLIT_MARK + id, json);
	}

	/**
	 * @Description: ����jsonKey����ȡ��Ӧ��json�ַ�����ת����ʵ����List
	 * @  key
	 * @return:List<T>
	 */
	public static List<?> getListBean(Set<String> sortKey, Class classs, Jedis jedis) {
		List<Object> list = new ArrayList<Object>();
		for (String key : sortKey) {
			list.add(getBean(key, classs, jedis));
		}
		return list;
	}

	/**
	 * @Description:����jsonKey����ȡ��Ӧ��json�ַ�����ת����ʵ����
	 * @param key
	 * @  classsʵ����
	 * @return:T dao�㷺�͵�ʵ����
	 */
	public static Object getBean(String key, Class classs, Jedis jedis) {
		return JSON.parseObject(jedis.get(key), classs);
	}

	/**
	 * @Description:��ָ��ֵ������
	 * @param tableName
	 * @param column
	 * @param value
	 * @param id
	 * @return:void
	 */
	public void zaddSort(String tableName, String column, String value, String id) {
		transaction.zadd(tableName + SPLIT_MARK + SORT + SPLIT_MARK + column, Double.parseDouble(value), id);
	}

	/**
	 * @Description:�Ա�����������
	 * @param tableName
	 * @param column
	 * @param value
	 * @return:Long
	 */
	public void zaddIndex(String tableName, String column, String value) {
		transaction.sadd(tableName + SPLIT_MARK + INDEX + SPLIT_MARK + column, tableName + SPLIT_MARK + value);
	}

	/**
	 * @Description:����redis��־ת���ɶ�Ӧ��sql
	 * @  value
	 * @ :Long
	 */
	public void log(String sql) {
		transaction.lpush(LOG, sql);
	}

	/**
	 * @Description: ���������ڿ�������֮ǰ��ִ��watch����
	 * @param keys
	 * @return:void
	 */
	public void watch(String... keys) {
		jedis.watch(keys);
	}

	/**
	 * @Description: ��ȡʵ�������list ���뵽redis����
	 * @param list ʵ���༯��
	 */
	public void insertListToredis(List<Object> list) throws Exception {
		if (null != list && list.size() > 0) {
			BeanField bf = getBeanField(list.get(0));
			for (Object tt : list) {
				insertSingleDataToredis(tt, bf);
			}
		}
	}

	/**
	 * @Description:���뵥��ʵ���ൽredis
	 * @param t ʵ����
	 * @ fieldList ʵ�����ֶ�����
	 * @ primaryKey ʵ��������������
	 * @ className ʵ���������
	 */
	public void insertSingleDataToredis(Object t, BeanField bf) throws Exception {
		Field[] fieldList = bf.getFieldList();
		String primaryKey = bf.getPrimaryKey();
		String className = bf.getClassName();
		// ��ȡ����ֵ
		Field pvField = t.getClass().getDeclaredField(primaryKey);
		pvField.setAccessible(true);
		String primaryValue = pvField.get(t).toString();

		for (Field field : fieldList) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object fieldValue = field.get(t);
			// ȥ���������
			if (null != fieldValue && !field.isAnnotationPresent(RedisFieldNotCache.class)) {
				// 1������һk/v
				setTable(className, primaryValue, fieldName, fieldValue.toString());
				// ����ע��Ľ���sadd kv�洢
				if (field.isAnnotationPresent(RedisQuery.class)) {
					// 2�����Ͷ� k/v
					saddColumn(className, fieldName, fieldValue.toString(), primaryValue);
				}
			}
		}
		// 3�� ���ӳ��bean key-jsonValue
		setJSON(className, primaryValue, JSON.toJSON(t).toString());
		// 4������������������
		zaddIndex(className, primaryKey, primaryValue);

		// 5������id��С����
		zaddSort(className, primaryKey, primaryValue, primaryValue);

	}

	/**
	 * @Description: ��redisɾ��������
	 * @param list
	 * @throws Exception
	 * @return:void
	 */
	public void delDataListFromRedis(List<Object> list) throws Exception {
		if (null != list && list.size() > 0) {
			// ���� primaryKey=noteId className=Note
			BeanField bf = getBeanField(list.get(0));
			for (Object tt : list) {
				delSingleDataFromRedis(tt, bf);
			}
		}
	}

	public BeanField getBeanField(Object t) throws Exception {
		// ��ȡ��������
		Field pkField = t.getClass().getDeclaredField("primaryKey");
		pkField.setAccessible(true);
		String primaryKey = pkField.get(t).toString();

		// ��ȡ������
		Field cnField = t.getClass().getDeclaredField("className");
		cnField.setAccessible(true);
		String className = cnField.get(t).toString();

		Field[] fieldList = t.getClass().getDeclaredFields();
		return new BeanField(primaryKey, className, fieldList);
	}

	/**
	 * @Description: ��redis����ɾ��ĳһ������
	 * @param t
	 * @  fieldList
	 * @  primaryKey
	 * @  className
	 * @  Exception
	 * @return:void
	 */
	public void delSingleDataFromRedis(Object t, BeanField bf) throws Exception {
		Field[] fieldList = bf.getFieldList();
		String primaryKey = bf.getPrimaryKey();
		String className = bf.getClassName();

		// ��ȡ����ֵ
		Field pvField = t.getClass().getDeclaredField(primaryKey);
		pvField.setAccessible(true);
		String primaryValue = pvField.get(t).toString();
		for (Field field : fieldList) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object fieldValue = field.get(t);
			// ȥ���������
			if (null != fieldValue && !field.isAnnotationPresent(RedisFieldNotCache.class)) {
				// 1��ɾ������һk/v
				delString(className + SPLIT_MARK + primaryValue + SPLIT_MARK + fieldName);

				// ����ע��Ľ���sadd kv�洢
				if (field.isAnnotationPresent(RedisQuery.class)) {
					// 2��ɾ������2
					delSet(className + SPLIT_MARK + fieldName + SPLIT_MARK + fieldValue.toString(), className + SPLIT_MARK + primaryValue);
				}
			}
		}
		// 3��ɾ�������� json��ʽ
		delString(className + SPLIT_MARK + primaryValue);
		// 4��ɾ������
		delSet(className + SPLIT_MARK + INDEX + SPLIT_MARK + primaryKey, className + SPLIT_MARK + primaryValue);
		// 5��ɾ��ĳ������
		delSortSet(className + SPLIT_MARK + SORT + SPLIT_MARK + primaryKey, primaryValue);
	}

	/**
	 * @Description:pub/subģʽ�㲥sqllog
	 * @param log
	 * @return:void
	 */
	public void pubishLog(String log) {
		transaction.publish(PUB_LOG, log);
	}

	/**
	 * @Description:��ȡredis����list���͵�log��
	 * @see:lpop���� ���ز�ɾ������Ϊkey��list�е���Ԫ�ء�����������ݿ�ʧ����Ҫ�ٰ�log�������������log(sql)����
	 * @return:String
	 */
	public static String lpopLog(Jedis jedis) {
		return jedis.lpop(LOG);
	}

	/**
	 * @Description:���redis�����л�������
	 * @return:void
	 */
	public void flushDB() {
		transaction.flushDB();
	}

	/**
	 * @Description: ����redis�����ĳ������
	 * @param oldObject ԭ���Ķ���
	 * @param t
	 * @param bf
	 */
	public void updateSingleFromToredis(Object oldObject, Object t, BeanField bf)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field[] fieldList = bf.getFieldList();
		String primaryKey = bf.getPrimaryKey();
		String className = bf.getClassName();
		// ��ȡ����ֵ
		Field pvField = t.getClass().getDeclaredField(primaryKey);
		pvField.setAccessible(true);
		String primaryValue = pvField.get(t).toString();

		for (Field field : fieldList) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object fieldValue = field.get(t);
			// ȥ���������
			if (null != fieldValue && !field.isAnnotationPresent(RedisFieldNotCache.class)) {
				// 1����������һk/v
				setTable(className, primaryValue, fieldName, fieldValue.toString());
				// ����ע��Ľ���sadd kv�洢
				if (field.isAnnotationPresent(RedisQuery.class)) {
					// ɾ���͵�����2
					delSet(className + SPLIT_MARK + fieldName + SPLIT_MARK + field.get(oldObject), className + SPLIT_MARK + primaryValue);
					// 2���������Ͷ� k/v
					saddColumn(className, fieldName, fieldValue.toString(), primaryValue);
				}
			}
		}
		// 3������ ���ӳ��bean key-jsonValue
		setJSON(className, primaryValue, JSON.toJSON(t).toString());
	}

}

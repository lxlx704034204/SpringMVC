package com.mark.test;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import com.mark.java.entity.NoteBook;
import com.mark.java.entity.NoteBookGroup;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.hibernate.Session;

import com.mark.java.entity.Note;

/**
 * @ClassName:StoreToDataBase.java
 * @Description: ����url ��ȡ����д�뵽������
 * @author gaoguangjin
 * @Date 2015-5-15 ����6:08:19
 */
@Slf4j
public class StoreToDataBaseByThread {
	private ConcurrentHashMap<String, String> currentHashMap;
	private Session session;
	private CountDownLatch latch;
	// Ĭ�ϱʼǱ���id��1
	private NoteBookGroup noteBookGroup = new NoteBookGroup();

	// ���߳�httpclient�����ʱ����
	private static RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(500000).setConnectTimeout(500000).build();
	// http�������ӳ�
	private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	// һ��������ֻ��һ���ͻ���
	private CloseableHttpClient httpClients = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(defaultRequestConfig).build();

	public StoreToDataBaseByThread(ConcurrentHashMap<String, String> currentHashMap, Session session, CountDownLatch latch) {
		this.currentHashMap = currentHashMap;
		this.session = session;
		this.latch = latch;
	}

	/**
	 * @Description:
	 * @see:�൥�̲߳���==��map��С�� �������ݿ��ʱ��50103ms
	 * @return:void
	 */
	public List<Note> insertToDatabase() {
		final Random random = new Random();
		noteBookGroup.setNoteBookGroupId(1);
		final List<Note> noteList = new CopyOnWriteArrayList<Note>();
		Iterator<String> iter = currentHashMap.keySet().iterator();
		try {
			while (iter.hasNext()) {
				final String title = iter.next();
				final String path = currentHashMap.get(title);
				new Thread() {
					public void run() {
						Note note = new Note();
						NoteBook nb = new NoteBook();
						String content = getHtmlByPath(path);
						Blob clobContent;
						try {
							clobContent = session.getLobHelper().createBlob(content.getBytes("utf-8"));
							note.setBlobContent(clobContent);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						note.setFromUrl(path);

						note.setNoteName(title);
						note.setAuthorName("�߹��");
						note.setCreatedate(new Date());
						if (content.contains("error:httpClients��ȡ��ҳ����ʧ��")) {
							note.setFlag(1);
						} else {
							note.setFlag(0);
						}
						// �����űʼǱ�����
						if (random.nextInt(2) == 1) {
							nb.setNoteBookId(1);
						} else {
							nb.setNoteBookId(2);
						}

						note.setNoteBookGroup(noteBookGroup);
						note.setNoteBook(nb);
						noteList.add(note);
						latch.countDown();
					}
				}.start();
			}

		} catch (Exception e) {
			log.error("����ץȡ��ҳ����д�뵽���ݿ�ʧ�ܣ�" + e.getLocalizedMessage());
		}
		return noteList;
	}

	private String getHtmlByPath(String url) {
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);// get
			// �����ʱ
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).build();
			httpGet.setConfig(requestConfig);
			CloseableHttpResponse response = httpClients.execute(httpGet);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, "utf-8");

		} catch (Exception e) {
			log.error("httpClients��ȡ��ҳ����ʧ�ܣ�" + e.getLocalizedMessage() + ":" + url);
			return "error:httpClients��ȡ��ҳ����ʧ�ܣ�ʧ�ܵ�ַ" + url + "===������Ϣ" + e.getLocalizedMessage();
		}
		finally {
			httpGet.releaseConnection();
		}
	}

}

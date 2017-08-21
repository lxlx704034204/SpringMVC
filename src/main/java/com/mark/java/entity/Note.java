package com.mark.java.entity;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.mark.redis.annation.RedisCache;
import com.mark.redis.annation.RedisFieldNotCache;
import com.mark.redis.annation.RedisQuery;

/**
 * @ClassName:Note.java
 * @Description: �ʼ�ʵ����
 * @author gaoguangjin
 * @Date 2015-3-4 ����11:31:38
 */
@Getter
@Setter
@RedisCache
public class Note implements Serializable {
	@RedisFieldNotCache
	private static final long serialVersionUID = 1L;
	@RedisFieldNotCache
	private static final String className = "Note";
	@RedisFieldNotCache
	private static final String primaryKey = "noteId";

	private int noteId;
	private String noteName;// �ʼ�����
	@RedisQuery
	private String authorName;// ��������
	@RedisQuery
	private String fromUrl;// �ı���Դ
	private String content;// �ı�����
	private NoteBook noteBook;// �ʼǱ�id
	private NoteBookGroup noteBookGroup;// �ʼǱ���
	@RedisQuery
	private Integer flag;// �ŵ�BaseBean���棬�����ȡ����fieldֵ
	@RedisQuery
	private Date createdate;
	@RedisFieldNotCache
	private Blob blobContent;

	public String toString() {
		return "���ֵ==>id=" + noteId + " �ʼǱ����ƣ�" + noteName + "   �ı���Դ��" + fromUrl + "  ��������:" + authorName;
	}

}

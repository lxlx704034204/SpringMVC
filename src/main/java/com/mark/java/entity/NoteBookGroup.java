package com.mark.java.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.mark.redis.annation.RedisCache;
import com.mark.redis.annation.RedisFieldNotCache;
import com.mark.redis.annation.RedisQuery;

/**
 * @ClassName:NoteBookGroup.java
 * @Description: �ʼǱ���ʵ����
 * @author gaoguangjin
 * @Date 2015-5-19 ����10:18:40
 */
@Getter
@Setter
@RedisCache
public class NoteBookGroup implements Serializable {
	@RedisFieldNotCache
	private static final long serialVersionUID = 1L;
	@RedisFieldNotCache
	private static final String className = "NoteBookGroup";
	@RedisFieldNotCache
	private static final String primaryKey = "noteBookGroupId";

	private int noteBookGroupId;
	@RedisQuery
	private String noteBookGroupName;
	private int textSum;// ͳ�ƸñʼǱ��������ж����ı�
	@RedisQuery
	private Integer flag;
	@RedisQuery
	private Date createdate;

}

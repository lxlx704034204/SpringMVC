package com.mark.java.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.mark.redis.annation.RedisCache;
import com.mark.redis.annation.RedisFieldNotCache;
import com.mark.redis.annation.RedisQuery;

/**
 * @ClassName:NoteBook.java
 * @Description: �ʼǱ�ʵ����
 * @author gaoguangjin
 * @Date 2015-5-19 ����10:18:23
 */
@Getter
@Setter
@RedisCache
public class NoteBook implements Serializable {

	@RedisFieldNotCache
	private static final long serialVersionUID = 1L;
	@RedisFieldNotCache
	private static final String className = "NoteBook";
	@RedisFieldNotCache
	private static final String primaryKey = "noteBookId";

	private int noteBookId;
	@RedisQuery
	private String noteBookName;
	private int textSum;// ͳ�ƸñʼǱ������ж����ı�
	private NoteBookGroup noteBookGroup;
	@RedisQuery
	private Integer flag;
	@RedisQuery
	private Date createdate;
}

package com.mark.java.service;

import java.util.List;

import com.mark.java.entity.Note;

public interface NoteService {
	/* �������� */
	Note queryById(String i);
	
	/**
	 * @Description:
	 * @see:���� select * from tcnote where note_name="�ı�����" and author_name="�߹��"
	 * @param note
	 * @return:List<Note>
	 */
	List<Note> queryParamAnd(Note note);
	
}

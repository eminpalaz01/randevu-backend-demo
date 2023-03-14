package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.List;
import com.dershaneproject.randevu.entities.concretes.Teacher;

import lombok.Data;

@Data
public class DepartmentDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = 6735949193159436033L;

	private long id;

	private String name;

	private String compressing;

	private List<TeacherDto> teachers;

}

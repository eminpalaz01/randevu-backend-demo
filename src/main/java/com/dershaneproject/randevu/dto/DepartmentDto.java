package com.dershaneproject.randevu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DepartmentDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = 6735949193159436033L;

	private Long id;

	private String name;

	private String compressing;

	private List<TeacherDto> teachersDto;

}

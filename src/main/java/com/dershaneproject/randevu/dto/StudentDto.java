package com.dershaneproject.randevu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class StudentDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = 2596060541103685317L;

	private Long id;

	private String userName;

	private String password;

	private Long authority;

	private Date createDate;

	private Date lastUpdateDate;

	private String email;

	private String studentNumber;
	
	private List<WeeklyScheduleDto> weeklySchedulesDto;

}

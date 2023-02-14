package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.dershaneproject.randevu.entities.concretes.Schedule;

import lombok.Data;

@Data
public class StudentDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = 2596060541103685317L;

	private long id;

	private String userName;

	private String password;

	private Date createDate;

	private Date lastUpdateDate;

	private String email;

	private String studentNumber;
	
	private List<Schedule> schedules;

}

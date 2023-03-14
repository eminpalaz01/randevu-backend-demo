package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.dershaneproject.randevu.entities.concretes.Schedule;

import lombok.Data;

@Data
public class TeacherDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = 2675692157198241702L;

	private long id;

	private String userName;

	private String password;

	private Date createDate;

	private Date lastUpdateDate;

	private String email;

	private long departmentId;

	private String teacherNumber;
		
	private List<ScheduleDto> schedules;
	
	private List<WeeklyScheduleDto> weeklySchedules;

}

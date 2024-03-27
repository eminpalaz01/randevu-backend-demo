package com.dershaneproject.randevu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TeacherDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = 2675692157198241702L;

	private Long id;

	private String userName;

	private String password;

	private Date createDate;

	private Date lastUpdateDate;

	private String email;

	private Long departmentId;

	private String teacherNumber;
		
	private List<ScheduleDto> schedulesDto;
	
	private List<WeeklyScheduleDto> weeklySchedulesDto;

}

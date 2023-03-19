package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.dershaneproject.randevu.entities.concretes.Schedule;

import lombok.Data;

@Data
public class SystemStaffDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = -7601987594451826218L;

	private Long id;

	private String userName;

	private String password;

	private Date createDate;

	private Integer authority;

	private Date lastUpdateDate;

	private String email;
	
	private List<ScheduleDto> schedules;
	
	private List<WeeklyScheduleDto> weeklySchedules;
}

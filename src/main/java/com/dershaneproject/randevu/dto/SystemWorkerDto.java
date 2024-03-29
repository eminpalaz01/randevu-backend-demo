package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.dershaneproject.randevu.entities.concretes.Schedule;

import lombok.Data;

@Data
public class SystemWorkerDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8157975018805476832L;

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

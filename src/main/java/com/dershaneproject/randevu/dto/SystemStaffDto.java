package com.dershaneproject.randevu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
	
	private List<ScheduleDto> schedulesDto;
	
	private List<WeeklyScheduleDto> weeklySchedulesDto;
}

package com.dershaneproject.randevu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ScheduleDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = -4030124069348513301L;

	private Long id;

	private Boolean full;

	private String description;

	private Date createDate;

	private Date lastUpdateDate;

	private Long teacherId;

	private SystemWorkerDto lastUpdateDateSystemWorker;

	private DayOfWeekDto dayOfWeek;

	private HourDto hour;

}

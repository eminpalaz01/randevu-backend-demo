package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class ScheduleDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = -4030124069348513301L;

	private long id;

	private Boolean full;

	private String description;
    
	private long teacherId;

	private SystemWorkerDto lastUpdateDateSystemWorker;

	private Date createDate;

	private Date lastUpdateDate;

	private DayOfWeekDto dayOfWeek;

	private HourDto hour;

}

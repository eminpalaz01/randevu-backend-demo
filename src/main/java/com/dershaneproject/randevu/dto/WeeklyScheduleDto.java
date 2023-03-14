package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class WeeklyScheduleDto implements Serializable{

	
	/**
	 * Default
	 */
	private static final long serialVersionUID = 1030523203526064259L;
	

	private long id;

	private Boolean full;
	
	private String description;

	private long teacherId;
	
	private long studentId;
	
	/*private long lastUpdateDateSystemWorkerId;*/

	private SystemWorkerDto lastUpdateDateSystemWorker;

	private Date createDate;

	private Date lastUpdateDate;

	private DayOfWeekDto dayOfWeek;

	private HourDto hour;
}

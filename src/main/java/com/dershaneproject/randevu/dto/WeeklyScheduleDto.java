package com.dershaneproject.randevu.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class WeeklyScheduleDto implements Serializable{

	
	/**
	 * Default
	 */
	private static final long serialVersionUID = 1030523203526064259L;

	private Long id;

	private Boolean full;
	
	private String description;

	private Date createDate;

	private Date lastUpdateDate;

	private Long teacherId;

	private Long studentId;

	private SystemWorkerDto lastUpdateDateSystemWorkerDto;

	private DayOfWeekDto dayOfWeekDto;

	private HourDto hourDto;
}

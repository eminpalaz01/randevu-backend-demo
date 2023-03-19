package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class DayOfWeekDto implements Serializable {
	
	/*
	 * Default
	 */
	private static final long serialVersionUID = 1030833002895429287L;

	private Long id;

	private String name;
}

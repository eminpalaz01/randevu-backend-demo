package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.time.LocalTime;

import lombok.Data;

@Data
public class HourDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = -527663666737550753L;

	private long id;

	private LocalTime time;
}


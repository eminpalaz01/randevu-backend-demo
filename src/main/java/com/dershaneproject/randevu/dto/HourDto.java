package com.dershaneproject.randevu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

@Data
public class HourDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = -527663666737550753L;

	private Long id;

	@Schema(type = "String", pattern = "HH:mm")
	@JsonFormat(pattern="HH:mm")
	private LocalTime time;
}


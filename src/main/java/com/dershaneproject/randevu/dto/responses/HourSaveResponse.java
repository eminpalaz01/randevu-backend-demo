package com.dershaneproject.randevu.dto.responses;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HourSaveResponse {
    private Long id;
    private LocalTime time;
}

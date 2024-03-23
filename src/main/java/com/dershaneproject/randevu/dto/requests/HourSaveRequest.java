package com.dershaneproject.randevu.dto.requests;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HourSaveRequest {
    private LocalTime time;
}

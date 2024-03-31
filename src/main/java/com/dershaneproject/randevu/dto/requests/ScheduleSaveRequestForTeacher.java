package com.dershaneproject.randevu.dto.requests;

import lombok.Data;

@Data
public class ScheduleSaveRequestForTeacher {
    private Boolean full;
    private String description;
    private Long dayOfWeekId;
    private Long hourId;
}

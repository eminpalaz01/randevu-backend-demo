package com.dershaneproject.randevu.dto.requests;

import lombok.Data;

@Data
public class WeeklyScheduleSaveRequest {
    private Boolean full;
    private String description;
    private Long teacherId;
    private Long studentId;
    private Long lastUpdateDateSystemWorkerId;
    private Long dayOfWeekId;
    private Long hourId;
}

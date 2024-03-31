package com.dershaneproject.randevu.dto.requests;

import lombok.Data;

@Data
public class ScheduleSaveRequest {
    private Boolean full;
    private String description;
    private Long teacherId;
    private Long lastUpdateDateSystemWorkerId;
    private Long dayOfWeekId;
    private Long hourId;
}

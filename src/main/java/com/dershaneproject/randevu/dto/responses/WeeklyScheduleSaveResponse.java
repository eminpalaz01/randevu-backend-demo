package com.dershaneproject.randevu.dto.responses;

import com.dershaneproject.randevu.entities.concretes.*;
import lombok.Data;

@Data
public class WeeklyScheduleSaveResponse {
    private Long id;
    private Boolean full;
    private String description;
    private Long teacherId;
    private Long studentId;
    private SystemWorker lastUpdateDateSystemWorker;
    private DayOfWeek dayOfWeek;
    private Hour hour;
}

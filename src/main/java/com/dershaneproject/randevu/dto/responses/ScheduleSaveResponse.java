package com.dershaneproject.randevu.dto.responses;

import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.SystemWorker;
import lombok.Data;

@Data
public class ScheduleSaveResponse {
    private Long id;
    private Boolean full;
    private String description;
    private Long teacherId;
    private SystemWorker lastUpdateDateSystemWorker;
    private DayOfWeek dayOfWeek;
    private Hour hour;
}

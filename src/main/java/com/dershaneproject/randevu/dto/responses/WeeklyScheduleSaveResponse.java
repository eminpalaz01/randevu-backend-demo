package com.dershaneproject.randevu.dto.responses;

import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.SystemWorkerDto;
import lombok.Data;

@Data
public class WeeklyScheduleSaveResponse {
    private Long id;
    private Boolean full;
    private String description;
    private Long teacherId;
    private Long studentId;
    private SystemWorkerDto lastUpdateDateSystemWorker;
    private DayOfWeekDto dayOfWeek;
    private HourDto hour;
}

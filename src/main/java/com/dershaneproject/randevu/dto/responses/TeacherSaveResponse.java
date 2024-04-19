package com.dershaneproject.randevu.dto.responses;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TeacherSaveResponse {
    private Long id;
    private String userName;
    private String password;
    private Long authority;
    private Date createDate;
    private Date lastUpdateDate;
    private String email;
    private Long departmentId;
    private String teacherNumber;
    private List<ScheduleSaveResponse> schedules;
    private List<WeeklyScheduleSaveResponse> weeklySchedules;
}

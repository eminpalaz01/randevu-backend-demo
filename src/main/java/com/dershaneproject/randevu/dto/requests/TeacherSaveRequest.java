package com.dershaneproject.randevu.dto.requests;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeacherSaveRequest {
    private String userName;
    private String password;
    private String email;
    private String teacherNumber;
    private Long departmentId;
    private Long lastUpdateDateSystemWorkerId;
    private List<ScheduleSaveRequestForTeacher> schedules = new ArrayList<>();
}

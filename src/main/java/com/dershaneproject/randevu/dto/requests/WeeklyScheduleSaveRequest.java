package com.dershaneproject.randevu.dto.requests;

import lombok.Data;

@Data
public class WeeklyScheduleSaveRequest {
    private String userName;
    private String password;
    private String email;
}

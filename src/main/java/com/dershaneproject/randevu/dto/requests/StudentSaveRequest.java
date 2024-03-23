package com.dershaneproject.randevu.dto.requests;

import lombok.Data;

@Data
public class StudentSaveRequest {
    private String userName;
    private String password;
    private String email;
    private String studentNumber;
}

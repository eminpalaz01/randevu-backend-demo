package com.dershaneproject.randevu.dto.requests;

import lombok.Data;

@Data
public class SystemAdministratorSaveRequest {
    private String userName;
    private String password;
    private String email;
}

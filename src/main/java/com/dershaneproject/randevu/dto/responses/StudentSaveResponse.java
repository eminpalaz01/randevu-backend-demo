package com.dershaneproject.randevu.dto.responses;

import lombok.Data;

import java.util.Date;

@Data
public class StudentSaveResponse {
    private Long id;
    private String userName;
    private String password;
    private Long authority;
    private Date createDate;
    private Date lastUpdateDate;
    private String email;
    private String studentNumber;
}

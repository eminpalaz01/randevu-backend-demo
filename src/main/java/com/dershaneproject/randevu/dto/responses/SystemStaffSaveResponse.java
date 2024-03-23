package com.dershaneproject.randevu.dto.responses;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SystemStaffSaveResponse implements Serializable {
    private Long id;
    private String userName;
    private Date createDate;
    private Integer authority;
    private String email;
}

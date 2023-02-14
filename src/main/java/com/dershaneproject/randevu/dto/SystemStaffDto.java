package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SystemStaffDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = -7601987594451826218L;

	private long id;

	private String userName;

	private String password;

	private Date createDate;

	private int authority;

	private Date lastUpdateDate;

	private String email;
}

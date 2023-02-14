package com.dershaneproject.randevu.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SystemAdministratorDto implements Serializable {

	/*
	 * Default
	 */
	private static final long serialVersionUID = 6255314199739697675L;

	private long id;

	private String userName;

	private String password;

	private Date createDate;

	private int authority;

	private Date lastUpdateDate;

	private String email;

}

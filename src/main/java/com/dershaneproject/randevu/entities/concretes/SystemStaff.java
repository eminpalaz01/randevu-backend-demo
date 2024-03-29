package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "system_staffs")
public class SystemStaff extends SystemWorker {

	/**
	 * 
	 */

	public SystemStaff() {
		// TODO Auto-generated constructor stub
		super();
		super.setAuthority(Authority.STAFF.getValue());
	}

	public SystemStaff(Long id, String userName, String password, Date createDate, Integer authority, Date lastUpdateDate,
			String email) {
		
		super(id, userName, password, createDate, lastUpdateDate, email);
		super.setAuthority(Authority.STAFF.getValue());
	}

}

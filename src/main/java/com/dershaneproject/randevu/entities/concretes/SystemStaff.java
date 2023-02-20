package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
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
	private static final long serialVersionUID = 5203546077615288543L;

	public SystemStaff() {
		// TODO Auto-generated constructor stub
		super();
		super.setAuthority(Authority.STAFF.getValue());
	}

	public SystemStaff(long id, String userName, String password, Date createDate, int authority, Date lastUpdateDate,
			String email) {
		
		super(id, userName, password, createDate, lastUpdateDate, email);
		super.setAuthority(Authority.STAFF.getValue());
	}

}

package com.dershaneproject.randevu.entities.concretes;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "system_staffs")
public class SystemStaff extends SystemWorker {

	public SystemStaff() {
		super();
		super.setAuthority(Authority.STAFF.getValue());
	}

	public SystemStaff(Long id, String userName, String password, Date createDate, Date lastUpdateDate,
			String email) {
		
		super(id, userName, password, createDate, lastUpdateDate, email);
		setAuthority(Authority.STAFF.getValue());
	}

	public static SystemStaff createEmptyWithId(Long id) {
		if (id == null)
			return null;

		SystemStaff systemStaff = new SystemStaff();
		systemStaff.setId(id);
		return systemStaff;
	}

}

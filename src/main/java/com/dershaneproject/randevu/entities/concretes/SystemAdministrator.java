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
@Table(name = "system_administrators")
public class SystemAdministrator extends SystemWorker {

	public SystemAdministrator() {
		super();
		super.setAuthority(Authority.ADMINISTRATOR.getValue());
	}

	public SystemAdministrator(Long id, String userName, String password, Date createDate,
							   Date lastUpdateDate, String email) {
		super(id, userName, password, createDate, lastUpdateDate, email);
		setAuthority(Authority.ADMINISTRATOR.getValue());
	}

	public static SystemAdministrator createEmptyWithId(Long id) {
		if (id == null)
			return null;

		SystemAdministrator systemAdministrator = new SystemAdministrator();
		systemAdministrator.setId(id);
		return systemAdministrator;
	}

}

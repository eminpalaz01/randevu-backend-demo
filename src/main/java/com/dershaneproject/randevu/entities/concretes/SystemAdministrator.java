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
@Table(name = "system_administrators")
public class SystemAdministrator extends SystemWorker {

	/**
	 * 
	 */

	public SystemAdministrator() {
		// TODO Auto-generated constructor stub
		super();
		super.setAuthority(Authority.ADMINISTRATOR.getValue());
	}

	public SystemAdministrator(Long id, String userName, String password, Date createDate, Integer authority,
			Date lastUpdateDate, String email) {
		super(id, userName, password, createDate, lastUpdateDate, email);
		super.setAuthority(Authority.ADMINISTRATOR.getValue());
	}

}

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
@Table(name = "system_administrators")
public class SystemAdministrator extends SystemWorker {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7679491206007942246L;

	public SystemAdministrator() {
		// TODO Auto-generated constructor stub
		super();
		super.setAuthority(Authority.ADMINISTRATOR.getValue());
	}

	public SystemAdministrator(long id, String userName, String password, Date createDate, int authority,
			Date lastUpdateDate, String email) {
		super(id, userName, password, createDate, lastUpdateDate, email);
		super.setAuthority(Authority.ADMINISTRATOR.getValue());
	}

}

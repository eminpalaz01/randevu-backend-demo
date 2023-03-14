package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "system_workers")
@Inheritance(strategy = InheritanceType.JOINED)
public class SystemWorker extends User {

	/**
	 * 
	 */

	@Column(name = "authority")
	private int authority;

	@JsonBackReference("systemWorkerSchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "lastUpdateDateSystemWorker")
	private List<Schedule> schedules;
	
	@JsonBackReference("systemWorkerWeeklySchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "lastUpdateDateSystemWorker")
	private List<WeeklySchedule> weeklySchedules;

	public SystemWorker() {
		// TODO Auto-generated constructor stub
		super();
	}

	public SystemWorker(long id, String userName, String password, Date createDate, Date lastUpdateDate, String email) {
		super(id, userName, password, createDate, lastUpdateDate, email);
	}

	protected void setAuthority(int authority) {
		this.authority = authority;
	}
}

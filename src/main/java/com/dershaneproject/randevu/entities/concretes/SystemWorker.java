package com.dershaneproject.randevu.entities.concretes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "system_workers")
@Inheritance(strategy = InheritanceType.JOINED)
public class SystemWorker extends User {

	/**
	 * 
	 */

	@Column(name = "authority")
	private Integer authority;

	@JsonBackReference("systemWorkerSchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "lastUpdateDateSystemWorker")
	private List<Schedule> schedules;

	@JsonBackReference("systemWorkerWeeklySchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "lastUpdateDateSystemWorker")
	private List<WeeklySchedule> weeklySchedules;

	public SystemWorker() {
		super();
	}

	public SystemWorker(Long id, String userName, String password, Date createDate, Date lastUpdateDate, String email) {
		super(id, userName, password, createDate, lastUpdateDate, email);
	}

	protected void setAuthority(Integer authority) {
		this.authority = authority;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
				"id = " + getId() + ", " +
				"authority = " + getAuthority() + ", " +
				"userName = " + getUserName() + ", " +
				"password = " + getPassword() + ", " +
				"createDate = " + getCreateDate() + ", " +
				"lastUpdateDate = " + getLastUpdateDate() + ", " +
				"email = " + getEmail() + ")";
	}
}

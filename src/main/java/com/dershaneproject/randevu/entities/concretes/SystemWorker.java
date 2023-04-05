package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
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
	private Integer authority;

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

	public SystemWorker(Long id, String userName, String password, Date createDate, Date lastUpdateDate, String email) {
		super(id, userName, password, createDate, lastUpdateDate, email);
	}

	protected void setAuthority(Integer authority) {
		this.authority = authority;
	}
}

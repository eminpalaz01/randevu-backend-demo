package com.dershaneproject.randevu.entities.concretes;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
@Entity
@Table(name = "weekly_schedules")
public class WeeklySchedule implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8994907173993020471L;
	
	@Id
	@SequenceGenerator(name = "weekly_schedule_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "weekly_schedule_id_seq")
	@Column(name = "id")
	private long id;

	// Default olarak false olmasını sağla.
	@Column(name = "full")
	private Boolean full;
	
	@Column(name = "description", nullable = true)
	private String description;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "teacher_id")
	@JsonManagedReference(value = "teacherWeeklySchedulesReference")
	private Teacher teacher;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "student_id", nullable = true)
	@JsonManagedReference(value = "studentWeeklySchedulesReference")
	private Student student;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "last_update_date_system_worker_id")
	@JsonManagedReference(value = "systemWorkerWeeklySchedulesReference")
	private SystemWorker lastUpdateDateSystemWorker;

	@CreationTimestamp
	@Column(name = "create_date")
	private Date createDate;

	@UpdateTimestamp
	@Column(name = "last_update_date")
	private Date lastUpdateDate;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "day_of_week_id")
	@JsonManagedReference(value = "dayOfWeekWeeklySchedulesReference")
	private DayOfWeek dayOfWeek;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "hour_id")
	@JsonManagedReference(value = "hourWeeklySchedulesReference")
	private Hour hour;

}

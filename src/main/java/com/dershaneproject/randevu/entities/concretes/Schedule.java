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
@Table(name = "schedules")
public class Schedule implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2247732292469828441L;

	@Id
	@SequenceGenerator(name = "schedule_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "schedule_id_seq")
	@Column(name = "id")
	private Long id;

	// Default olarak false olmasını sağla.
	@Column(name = "full")
	private Boolean full;
	
	@Column(name = "description", nullable = true)
	private String description;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "teacher_id")
	@JsonManagedReference(value = "teacherSchedulesReference")
	private Teacher teacher;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "last_update_date_system_worker_id")
	@JsonManagedReference(value = "systemWorkerSchedulesReference")
	private SystemWorker lastUpdateDateSystemWorker;

	@CreationTimestamp
	@Column(name = "create_date")
	private Date createDate;

	@UpdateTimestamp
	@Column(name = "last_update_date")
	private Date lastUpdateDate;
    
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "day_of_week_id")
	@JsonManagedReference(value = "dayOfWeekSchedulesReference")
	private DayOfWeek dayOfWeek;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "hour_id")
	@JsonManagedReference(value = "hourSchedulesReference")
	private Hour hour;
}

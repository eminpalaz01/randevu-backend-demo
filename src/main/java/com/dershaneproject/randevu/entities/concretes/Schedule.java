package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "schedules")
public class Schedule {

	@Id
	@SequenceGenerator(name = "schedule_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "schedule_id_seq")
	@Column(name = "id")
	private long id;

	// Default olarak false olmasını sağla.
	@Column(name = "full")
	private Boolean full;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id")
	@JsonBackReference(value = "teacherSchedulesReference")
	private Teacher teacher;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = true)
	@JsonBackReference(value = "studentSchedulesReference")
	private Student student;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "last_update_date_system_worker_id")
	@JsonBackReference(value = "systemWorkerSchedulesReference")
	private SystemWorker lastUpdateDateSystemWorker;

	@CreationTimestamp
	@Column(name = "create_date")
	private Date createDate;

	@UpdateTimestamp
	@Column(name = "last_update_date")
	private Date lastUpdateDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "day_of_week_id")
	@JsonBackReference(value = "dayOfWeekSchedulesReference")
	private DayOfWeek dayOfWeek;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hour_id")
	@JsonBackReference(value = "hourSchedulesReference")
	private Hour hour;
}

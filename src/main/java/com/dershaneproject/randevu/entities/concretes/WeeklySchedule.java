package com.dershaneproject.randevu.entities.concretes;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
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
	private Long id;

	// Default olarak false olmasını sağla.
	@Column(name = "is_full")
	private Boolean full;
	
	@Column(name = "description", nullable = true)
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "teacher_id")
	@JsonManagedReference(value = "teacherWeeklySchedulesReference")
	private Teacher teacher;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "student_id", nullable = true)
	@JsonManagedReference(value = "studentWeeklySchedulesReference")
	private Student student;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "last_update_date_system_worker_id")
	@JsonManagedReference(value = "systemWorkerWeeklySchedulesReference")
	private SystemWorker lastUpdateDateSystemWorker;

	@CreationTimestamp
	@Column(name = "create_date")
	private Date createDate;

	@UpdateTimestamp
	@Column(name = "last_update_date")
	private Date lastUpdateDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "day_of_week_id")
	@JsonManagedReference(value = "dayOfWeekWeeklySchedulesReference")
	private DayOfWeek dayOfWeek;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "hour_id")
	@JsonManagedReference(value = "hourWeeklySchedulesReference")
	private Hour hour;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		WeeklySchedule that = (WeeklySchedule) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}

package com.dershaneproject.randevu.entities.concretes;

import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@Table(name = "schedules")
public class Schedule implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2247732292469828441L;

	public static final String DEFAULT_DESCRIPTION = "DEFAULT DESCRIPTION";

	@Id
	@SequenceGenerator(name = "schedule_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "schedule_id_seq")
	@Column(name = "id")
	private Long id;

	// Default olarak false olmasını sağla.
	@Column(name = "is_full")
	private Boolean full;
	
	@Column(name = "description", nullable = true)
	private String description = DEFAULT_DESCRIPTION;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id")
	@JsonManagedReference(value = "teacherSchedulesReference")
	private Teacher teacher;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "last_update_date_system_worker_id")
	@JsonManagedReference(value = "systemWorkerSchedulesReference")
	private SystemWorker lastUpdateDateSystemWorker;

	@CreationTimestamp
	@Column(name = "create_date")
	private Date createDate;

	@UpdateTimestamp
	@Column(name = "last_update_date")
	private Date lastUpdateDate;
    
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "day_of_week_id")
	@JsonManagedReference(value = "dayOfWeekSchedulesReference")
	private DayOfWeek dayOfWeek;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "hour_id")
	@JsonManagedReference(value = "hourSchedulesReference")
	private Hour hour;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Schedule schedule = (Schedule) o;
		return getId() != null && Objects.equals(getId(), schedule.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}

	public static Schedule createEmptyWithId(Long id) {
		if (id == null)
			return null;

		Schedule schedule = new Schedule();
		schedule.setId(id);
		return schedule;
	}

	public static ScheduleSaveRequest createDefaultScheduleForSave(Long lastUpdateDateSystemWorkerId, Long teacherId, long dayId, long hourId) {
		ScheduleSaveRequest scheduleSaveRequest = new ScheduleSaveRequest();
		scheduleSaveRequest.setDayOfWeekId(dayId);
		scheduleSaveRequest.setHourId(hourId);
		scheduleSaveRequest.setTeacherId(teacherId);
		scheduleSaveRequest.setLastUpdateDateSystemWorkerId(lastUpdateDateSystemWorkerId);
		scheduleSaveRequest.setDescription(Schedule.DEFAULT_DESCRIPTION);
		scheduleSaveRequest.setFull(false);
		return scheduleSaveRequest;
	}
}

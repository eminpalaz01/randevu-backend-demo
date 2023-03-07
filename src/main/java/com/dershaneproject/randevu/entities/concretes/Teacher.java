package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "teachers")
public class Teacher extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2438081625053399401L;

	@JsonBackReference(value = "departmentTeachersReference")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	@Column(name = "teacher_number", length = 20)
	private String teacherNumber;

    @JsonBackReference(value = "teacherSchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "teacher", cascade = CascadeType.REMOVE)
	private List<Schedule> schedules;
    
    @JsonBackReference(value = "teacherWeeklySchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "teacher", cascade = CascadeType.REMOVE)
	private List<WeeklySchedule> weeklySchedules;

	public Teacher() {
	}

	public Teacher(long id, String userName, String password, Date createDate, Date lastUpdateDate, String email,
			Department department, String teacherNumber) {
		// TODO Auto-generated constructor stub
		super(id, userName, password, createDate, lastUpdateDate, email);
		this.teacherNumber = teacherNumber;
		this.department = department;
	}

}

package com.dershaneproject.randevu.entities.concretes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "teachers")
public class Teacher extends User{

	/**
	 * 
	 */
	
	@JsonBackReference(value = "departmentTeachersReference")
	@ManyToOne(fetch = FetchType.LAZY)
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

	public Teacher(Long id, String userName, String password, Date createDate, Date lastUpdateDate, String email,
			Department department, String teacherNumber) {
		super(id, userName, password, createDate, lastUpdateDate, email);
		this.teacherNumber = teacherNumber;
		this.department = department;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
				"id = " + getId() + ", " +
				"teacherNumber = " + getTeacherNumber() + ", " +
				"userName = " + getUserName() + ", " +
				"password = " + getPassword() + ", " +
				"createDate = " + getCreateDate() + ", " +
				"lastUpdateDate = " + getLastUpdateDate() + ", " +
				"email = " + getEmail() + ")";
	}

	public static Teacher createEmptyWithId(Long id){
		if (id == null)
			return null;

		Teacher teacher = new Teacher();
		teacher.setId(id);
		return teacher;
	}
}

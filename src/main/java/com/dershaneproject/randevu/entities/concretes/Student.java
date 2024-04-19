package com.dershaneproject.randevu.entities.concretes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "students")
public class Student extends User {

	/**
	 * 
	 */

	@Column(name = "student_number", length = 20)
	private String studentNumber;
	
	@JsonBackReference(value = "studentWeeklySchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "student", cascade = CascadeType.REFRESH)
	private List<WeeklySchedule> weeklySchedules;

	public Student() { setAuthority(); }

	public Student(Long id, String userName, String password, Date createDate, Date lastUpdateDate, String email,
				   String studentNumber) {
		super(id, userName, password, createDate, lastUpdateDate, email);
		setAuthority();
		this.studentNumber = studentNumber;
	}

	private void setAuthority() {
		setAuthority(Authority.STUDENT.getValue());
	}

	public static Student createEmptyWithId(Long id) {
		if (id == null)
			return null;

		Student student = new Student();
		student.setId(id);
		return student;
	}
}

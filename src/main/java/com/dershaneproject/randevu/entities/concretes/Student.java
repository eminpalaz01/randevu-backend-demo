package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

	public Student() {
	}

	public Student(Long id, String userName, String password, Date createDate, Date lastUpdateDate, String email,
			String studentNumber) {
		super(id, userName, password, createDate, lastUpdateDate, email);
		this.studentNumber = studentNumber;
	}

}

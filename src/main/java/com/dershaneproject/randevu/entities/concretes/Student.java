package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
	private static final long serialVersionUID = 8412857069251001759L;

	@Column(name = "student_number", length = 20)
	private String studentNumber;

	@JsonBackReference(value = "studentSchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "student", cascade = CascadeType.REFRESH)
	private List<Schedule> schedules;

	public Student() {
	}

	public Student(long id, String userName, String password, Date createDate, Date lastUpdateDate, String email,
			String studentNumber) {
		super(id, userName, password, createDate, lastUpdateDate, email);
		this.studentNumber = studentNumber;
	}

}

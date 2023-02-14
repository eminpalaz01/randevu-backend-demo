package com.dershaneproject.randevu.entities.concretes;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "teachers")
public class Teacher extends User {

	@JsonBackReference(value = "departmentTeachersReference")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	@Column(name = "teacher_number", length = 20)
	private String teacherNumber;

    @JsonManagedReference(value = "teacherSchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "teacher", cascade = CascadeType.REMOVE)
	private List<Schedule> schedules;

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
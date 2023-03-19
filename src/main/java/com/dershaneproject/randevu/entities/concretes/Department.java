package com.dershaneproject.randevu.entities.concretes;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "departments")
public class Department implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5861384129261828888L;

	@Id
	@SequenceGenerator(name = "department_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "department_id_seq")
	@Column(name = "id")
	private Long id;

	@Column(name = "name", length = 50)
	private String name;

	@Column(name = "compressing", length = 20)
	private String compressing;

	@JsonManagedReference(value = "departmentTeachersReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "department", cascade = CascadeType.REFRESH)
	private List<Teacher> teachers;

	public Department() {
	}

	public Department(Long id, String name, String compressing) {
		super();
		this.id = id;
		this.name = name;
		this.compressing = compressing;
	}

}

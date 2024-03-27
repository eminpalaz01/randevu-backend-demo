package com.dershaneproject.randevu.entities.concretes;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
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
	@ToString.Exclude
	private List<Teacher> teachers;

	public Department() {
	}

	public Department(Long id, String name, String compressing) {
		super();
		this.id = id;
		this.name = name;
		this.compressing = compressing;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Department that = (Department) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}

	public static Department createEmptyWithId(Long id){
		Department department = new Department();
		department.setId(id);
		return department;
	}
}

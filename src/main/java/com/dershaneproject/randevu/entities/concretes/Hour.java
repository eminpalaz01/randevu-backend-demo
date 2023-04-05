package com.dershaneproject.randevu.entities.concretes;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "hours")
public class Hour implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6977120390546533101L;

	@Id
	@SequenceGenerator(name = "hour_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "hour_id_seq")
	@Column(name = "id")
	private Long id;

	@Column(name = "time")
	private LocalTime time;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Hour hour = (Hour) o;
		return getId() != null && Objects.equals(getId(), hour.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}

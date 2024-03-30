package com.dershaneproject.randevu.entities.concretes;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "days_of_week")
public class DayOfWeek implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 767738165750845173L;

	@Id
	@SequenceGenerator(name = "day_of_week_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "day_of_week_id_seq")
	@Column(name = "id")
	private Long id;

	@Column(name = "name", length = 10)
	private String name;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		DayOfWeek dayOfWeek = (DayOfWeek) o;
		return getId() != null && Objects.equals(getId(), dayOfWeek.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}

	public static DayOfWeek createEmptyWithId(Long id) {
		if (id == null)
			return null;

		DayOfWeek dayOfWeek = new DayOfWeek();
		dayOfWeek.setId(id);
		return dayOfWeek;
	}
}

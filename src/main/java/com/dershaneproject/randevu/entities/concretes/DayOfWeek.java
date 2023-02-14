package com.dershaneproject.randevu.entities.concretes;

import java.util.List;
import javax.persistence.Column;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "days_of_week")
public class DayOfWeek {

	@Id
	@SequenceGenerator(name = "day_of_week_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "day_of_week_id_seq")
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 10)
	private String name;
	
	@JsonManagedReference(value = "dayOfWeekSchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dayOfWeek")
	private List<Schedule> schedules;

}

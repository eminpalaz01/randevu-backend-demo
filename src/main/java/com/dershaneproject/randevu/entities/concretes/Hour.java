package com.dershaneproject.randevu.entities.concretes;

import java.time.LocalTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.Column;
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
@Table(name = "hours")
public class Hour {

	@Id
	@SequenceGenerator(name = "hour_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "hour_id_seq")
	@Column(name = "id")
	private long id;

	@Column(name = "time")
	private LocalTime time;
	
	@JsonManagedReference(value = "hourSchedulesReference")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hour")
	private List<Schedule> schedules;

}

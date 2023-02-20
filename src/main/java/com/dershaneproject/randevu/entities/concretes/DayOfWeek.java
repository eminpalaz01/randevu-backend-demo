package com.dershaneproject.randevu.entities.concretes;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Data
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
	private long id;

	@Column(name = "name", length = 10)
	private String name;
	
}

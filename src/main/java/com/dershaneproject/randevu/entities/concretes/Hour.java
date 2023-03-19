package com.dershaneproject.randevu.entities.concretes;

import java.io.Serializable;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Data
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
	
}

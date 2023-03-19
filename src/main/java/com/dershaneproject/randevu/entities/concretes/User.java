package com.dershaneproject.randevu.entities.concretes;

import java.io.Serializable;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6635335373357059844L;

	@Id
	@SequenceGenerator(name = "user_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "user_id_seq")
	@Column(name = "user_id")
	private Long id;

	@Column(name = "username", length = 30)
	private String userName;

	@Column(name = "password", length = 30)
	private String password;

	@CreationTimestamp
	@Column(name = "create_date")
	private Date createDate;

	@UpdateTimestamp
	@Column(name = "last_update_date")
	private Date lastUpdateDate;

	@Column(name = "email", length = 50)
	private String email;

	public User() {
	}

	public User(Long id, String userName, String password, Date createDate, Date lastUpdateDate, String email) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.createDate = createDate;
		this.lastUpdateDate = lastUpdateDate;
		this.email = email;
	}

}

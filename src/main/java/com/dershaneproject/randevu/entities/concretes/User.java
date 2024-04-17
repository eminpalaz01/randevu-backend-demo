package com.dershaneproject.randevu.entities.concretes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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

	@Column(name = "authority")
	private Integer authority;

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

	protected void setAuthority(Integer authority) {
		this.authority = authority;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		User user = (User) o;
		return getId() != null && Objects.equals(getId(), user.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}

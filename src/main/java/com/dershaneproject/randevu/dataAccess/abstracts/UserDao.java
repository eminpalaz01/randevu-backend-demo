package com.dershaneproject.randevu.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.dershaneproject.randevu.entities.concretes.User;

@NoRepositoryBean
public interface UserDao extends JpaRepository<User,Long>{
	
	

}

package com.dershaneproject.randevu.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dershaneproject.randevu.entities.concretes.Teacher;

@Repository
public interface TeacherDao extends JpaRepository<Teacher,Long> {

	// Buradaki syntax için kodlama io 8.kayıta bak.
	List<Teacher> getByDepartmentId(long departmentId);
}

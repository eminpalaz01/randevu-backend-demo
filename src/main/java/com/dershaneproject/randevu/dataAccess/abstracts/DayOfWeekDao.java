package com.dershaneproject.randevu.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;

@Repository
public interface DayOfWeekDao extends JpaRepository<DayOfWeek,Long>{

}

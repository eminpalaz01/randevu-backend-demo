package com.dershaneproject.randevu.dataAccess.abstracts;

import com.dershaneproject.randevu.entities.concretes.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleDao extends JpaRepository<Schedule,Long> {
    @Query(value = "SELECT schedule FROM Schedule schedule " +
                   "WHERE schedule.id IN (:idList) " +
                   "ORDER BY schedule.dayOfWeek.id ASC, schedule.hour.id ASC")
    List<Schedule> findAllByIdListSorted(List<Long> idList);

}

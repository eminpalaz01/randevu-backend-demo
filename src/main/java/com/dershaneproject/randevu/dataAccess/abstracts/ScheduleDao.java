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
                   "ORDER BY schedule.teacher.id ASC,schedule.dayOfWeek.id ASC, schedule.hour.id ASC")
    List<Schedule> findAllByIdSorted(List<Long> idList);

    @Query(value = "SELECT schedule FROM Schedule schedule " +
                   "WHERE schedule.teacher.id = :teacherId " +
                   "ORDER BY schedule.dayOfWeek.id ASC, schedule.hour.id ASC")
    List<Schedule> findAllByTeacherIdSorted(Long teacherId);

    @Query(value = "SELECT schedule FROM Schedule schedule " +
            "WHERE schedule.lastUpdateDateSystemWorker.id = :systemWorkerId " +
            "ORDER BY schedule.dayOfWeek.id ASC, schedule.hour.id ASC")
    List<Schedule> findAllBySystemWorkerIdSorted(Long systemWorkerId);

}

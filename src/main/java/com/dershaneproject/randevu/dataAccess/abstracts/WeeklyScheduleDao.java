package com.dershaneproject.randevu.dataAccess.abstracts;

import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyScheduleDao extends JpaRepository<WeeklySchedule,Long>{

    @Query(value = "SELECT weeklySchedule FROM WeeklySchedule weeklySchedule " +
                   "WHERE weeklySchedule.id IN (:idList) " +
                   "ORDER BY weeklySchedule.dayOfWeek.id ASC, weeklySchedule.hour.id ASC")
    List<WeeklySchedule> findAllByIdSorted(List<Long> idList);

    @Query(value = "SELECT weeklySchedule FROM WeeklySchedule weeklySchedule " +
                   "WHERE weeklySchedule.teacher.id = :teacherId " +
                   "ORDER BY weeklySchedule.dayOfWeek.id ASC, weeklySchedule.hour.id ASC")
    List<WeeklySchedule> findAllByTeacherIdSorted(Long teacherId);

    @Query(value = "SELECT weeklySchedule FROM WeeklySchedule weeklySchedule " +
                   "WHERE weeklySchedule.student.id = :studentId " +
                   "ORDER BY weeklySchedule.dayOfWeek.id ASC, weeklySchedule.hour.id ASC")
    List<WeeklySchedule> findAllByStudentIdSorted(Long studentId);

}

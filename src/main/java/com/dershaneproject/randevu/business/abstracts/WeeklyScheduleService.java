package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;

public interface WeeklyScheduleService {

	DataResult<WeeklyScheduleDto> save(WeeklySchedule weeklySchedule);

	Result deleteById(long id);

	DataResult<List<WeeklyScheduleDto>> findAll();

	DataResult<WeeklyScheduleDto> findById(long id);

	DataResult<WeeklyScheduleDto> updateFullById(long id, Boolean full);

	DataResult<WeeklyScheduleDto> updateTeacherById(long id, long teacherId);

	DataResult<WeeklyScheduleDto> updateStudentById(long id, long studentId);

	DataResult<WeeklyScheduleDto> updateLastUpdateDateSystemWorkerById(long id, long lastUpdateDateSystemWorkerId);

	DataResult<WeeklyScheduleDto> updateDayOfWeekById(long id, long dayOfWeekId);
	
	DataResult<WeeklyScheduleDto> updateHourById(long id, long hourId);
	
	DataResult<Long> getCount();
}

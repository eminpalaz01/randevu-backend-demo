package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;

public interface WeeklyScheduleService {

	DataResult<WeeklyScheduleDto> save(WeeklyScheduleDto weeklyScheduleDto);
	
	DataResult<List<WeeklyScheduleDto>> saveAll(List<WeeklyScheduleDto> weeklySchedulesDto);

	Result deleteById(long id);

	DataResult<List<WeeklyScheduleDto>> findAll();

	DataResult<WeeklyScheduleDto> findById(long id);

	DataResult<WeeklyScheduleDto> updateFullById(long id, Boolean full);

	DataResult<WeeklyScheduleDto> updateTeacherById(long id, Long teacherId);

	DataResult<WeeklyScheduleDto> updateStudentById(long id, Long studentId);
	
	DataResult<WeeklyScheduleDto> updateDescriptionById(long id, String description);

	DataResult<WeeklyScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId);

	DataResult<WeeklyScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId);
	
	DataResult<WeeklyScheduleDto> updateHourById(long id, Long hourId);
	
	DataResult<Long> getCount();
}

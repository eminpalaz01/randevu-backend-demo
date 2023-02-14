package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.ScheduleDto;

public interface ScheduleService {

	DataResult<ScheduleDto> save(ScheduleDto scheduleDto);

	Result deleteById(long id);

	DataResult<List<ScheduleDto>> findAll();

	DataResult<ScheduleDto> findById(long id);

	DataResult<ScheduleDto> updateFullById(long id, Boolean full);

	DataResult<ScheduleDto> updateTeacherById(long id, long teacherId);

	DataResult<ScheduleDto> updateStudentById(long id, long studentId);

	DataResult<ScheduleDto> updateLastUpdateDateSystemWorkerById(long id, long lastUpdateDateSystemWorkerId);

	DataResult<ScheduleDto> updateDayOfWeekById(long id, long dayOfWeekId);
	
	DataResult<ScheduleDto> updateHourById(long id, long hourId);
	
	DataResult<Long> getCount();
}

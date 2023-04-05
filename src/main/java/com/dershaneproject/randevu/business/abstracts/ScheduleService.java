package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;

public interface ScheduleService {

	DataResult<ScheduleDto> save(ScheduleDto scheduleDto);

	DataResult<List<ScheduleDto>> saveAll(List<ScheduleDto> schedulesDto);
	
	DataResult<List<ScheduleDto>> saveAllForCreateTeacher(List<ScheduleDto> schedulesDto);

	Result deleteById(long id);

	DataResult<List<ScheduleDto>> findAll();

	DataResult<ScheduleDto> findById(long id);

	DataResult<ScheduleDto> updateFullById(long id, Boolean full);

	DataResult<ScheduleDto> updateTeacherById(long id, Long teacherId);

	DataResult<ScheduleDto> updateDescriptionById(long id, String description);

	DataResult<ScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId);

	DataResult<ScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId);
	
	DataResult<ScheduleDto> updateHourById(long id, Long hourId);
	
	DataResult<Long> getCount();
}

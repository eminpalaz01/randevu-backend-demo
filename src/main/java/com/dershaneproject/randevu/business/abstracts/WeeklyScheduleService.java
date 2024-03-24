package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.WeeklyScheduleSaveResponse;

import java.util.List;

public interface WeeklyScheduleService {

	DataResult<WeeklyScheduleSaveResponse> save(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest);
	
	DataResult<List<WeeklyScheduleSaveResponse>> saveAll(List<WeeklyScheduleSaveRequest> weeklyScheduleSaveRequestList);

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

    DataResult<List<WeeklyScheduleDto>> findAllByTeacherId(long teacherId);

	DataResult<List<WeeklyScheduleDto>> findAllByStudentId(long studentId);
}

package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.WeeklyScheduleSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.util.List;

public interface WeeklyScheduleService {

	DataResult<WeeklyScheduleSaveResponse> save(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) throws BusinessException;
	
	DataResult<List<WeeklyScheduleSaveResponse>> saveAll(List<WeeklyScheduleSaveRequest> weeklyScheduleSaveRequestList) throws BusinessException;

	Result deleteById(long id) throws BusinessException;

	DataResult<List<WeeklyScheduleDto>> findAll() throws BusinessException;

	DataResult<WeeklyScheduleDto> findById(long id) throws BusinessException;

	DataResult<WeeklyScheduleDto> updateFullById(long id, Boolean full) throws BusinessException;

	DataResult<WeeklyScheduleDto> updateTeacherById(long id, Long teacherId) throws BusinessException;

	DataResult<WeeklyScheduleDto> updateStudentById(long id, Long studentId) throws BusinessException;
	
	DataResult<WeeklyScheduleDto> updateDescriptionById(long id, String description) throws BusinessException;

	DataResult<WeeklyScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId) throws BusinessException;

	DataResult<WeeklyScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) throws BusinessException;
	
	DataResult<WeeklyScheduleDto> updateHourById(long id, Long hourId) throws BusinessException;
	
    DataResult<List<WeeklyScheduleDto>> findAllByTeacherId(long teacherId) throws BusinessException;

    DataResult<List<WeeklyScheduleDto>> findAllBySystemWorkerId(long systemWorkerId) throws BusinessException;

    DataResult<List<WeeklyScheduleDto>> findAllByStudentId(long studentId) throws BusinessException;

	DataResult<Long> getCount();
}

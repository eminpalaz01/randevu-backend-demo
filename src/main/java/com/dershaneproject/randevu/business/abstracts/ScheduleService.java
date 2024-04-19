package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.ScheduleSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.util.List;

public interface ScheduleService {

	DataResult<ScheduleSaveResponse> save(ScheduleSaveRequest scheduleSaveRequest) throws BusinessException;

	DataResult<List<ScheduleSaveResponse>> saveAll(List<ScheduleSaveRequest> scheduleSaveRequestList) throws BusinessException;

	Result deleteById(long id) throws BusinessException;

	DataResult<List<ScheduleDto>> findAll() throws BusinessException;

	DataResult<ScheduleDto> findById(long id) throws BusinessException;

	DataResult<ScheduleDto> updateFullById(long id, Boolean full) throws BusinessException;

	DataResult<ScheduleDto> updateTeacherById(long id, Long teacherId) throws BusinessException;

	DataResult<ScheduleDto> updateDescriptionById(long id, String description) throws BusinessException;

	DataResult<ScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId) throws BusinessException;

	DataResult<ScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) throws BusinessException;
	
	DataResult<ScheduleDto> updateHourById(long id, Long hourId) throws BusinessException;
	
	DataResult<List<ScheduleDto>> findAllByTeacherId(long teacherId) throws BusinessException;

    DataResult<List<ScheduleDto>> findAllBySystemWorkerId(long systemWorkerId) throws BusinessException;

    DataResult<Long> getCount();
}

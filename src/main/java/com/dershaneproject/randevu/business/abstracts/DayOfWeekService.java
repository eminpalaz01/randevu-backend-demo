package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.requests.DayOfWeekSaveRequest;
import com.dershaneproject.randevu.dto.responses.DayOfWeekSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.util.List;

public interface DayOfWeekService {

	DataResult<DayOfWeekSaveResponse> save(DayOfWeekSaveRequest dayOfWeekSaveRequest);

	Result deleteById(long id) throws BusinessException;

	DataResult<List<DayOfWeekDto>> findAll() throws BusinessException;

	DataResult<DayOfWeekDto> findById(long id) throws BusinessException;

	DataResult<DayOfWeekDto> updateNameById(long id, String name) throws BusinessException;

	DataResult<Long> getCount();
}

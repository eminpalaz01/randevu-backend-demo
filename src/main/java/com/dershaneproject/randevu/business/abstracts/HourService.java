package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.requests.HourSaveRequest;
import com.dershaneproject.randevu.dto.responses.HourSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.time.LocalTime;
import java.util.List;

public interface HourService {
	
	DataResult<HourSaveResponse> save(HourSaveRequest hourSaveRequest);

	Result deleteById(long id) throws BusinessException;

	DataResult<List<HourDto>> findAll() throws BusinessException;

	DataResult<HourDto> findById(long id) throws BusinessException;

	DataResult<HourDto> updateTimeById(long id, LocalTime time) throws BusinessException;
	
	DataResult<Long> getCount();
}

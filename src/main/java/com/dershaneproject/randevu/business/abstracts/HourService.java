package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.requests.HourSaveRequest;
import com.dershaneproject.randevu.dto.responses.HourSaveResponse;

import java.time.LocalTime;
import java.util.List;

public interface HourService {
	
	DataResult<HourSaveResponse> save(HourSaveRequest hourSaveRequest);

	Result deleteById(long id);

	DataResult<List<HourDto>> findAll();

	DataResult<HourDto> findById(long id);

	DataResult<HourDto> updateTimeById(long id, LocalTime time);
	
	DataResult<Long> getCount();
}

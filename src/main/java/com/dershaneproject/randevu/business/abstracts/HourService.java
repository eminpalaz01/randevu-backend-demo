package com.dershaneproject.randevu.business.abstracts;

import java.time.LocalTime;
import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.HourDto;

public interface HourService {
	
	DataResult<HourDto> save(HourDto hourDto);

	Result deleteById(long id);

	DataResult<List<HourDto>> findAll();

	DataResult<HourDto> findById(long id);

	DataResult<HourDto> updateTimeById(long id, LocalTime time);
	
	DataResult<Long> getCount();
}

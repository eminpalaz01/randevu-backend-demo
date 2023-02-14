package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DayOfWeekDto;

public interface DayOfWeekService {
	
	DataResult<DayOfWeekDto> save(DayOfWeekDto dayOfWeekDto);

	Result deleteById(long id);

	DataResult<List<DayOfWeekDto>> findAll();

	DataResult<DayOfWeekDto> findById(long id);

	DataResult<DayOfWeekDto> updateNameById(long id, String name);
	
	DataResult<Long> getCount();
}

package com.dershaneproject.randevu.validations.abstracts;

import java.util.List;

import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.ScheduleDto;

public interface ScheduleValidationService {

	Result isValidateResult(ScheduleDto scheduleDto);
			
	Result areValidateForCreateTeacherResult(List<ScheduleDto> schedulesDto);
}

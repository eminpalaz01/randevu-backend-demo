package com.dershaneproject.randevu.validations.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;

import java.util.List;

public interface ScheduleValidationService {

	Result isValidateResult(ScheduleSaveRequest scheduleDto);
			
	Result areValidateResult(List<ScheduleSaveRequest> scheduleSaveRequestList);
}

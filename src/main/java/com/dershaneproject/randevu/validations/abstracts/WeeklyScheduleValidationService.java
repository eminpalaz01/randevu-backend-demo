package com.dershaneproject.randevu.validations.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;

public interface WeeklyScheduleValidationService {

    Result isValidateResult(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest);

    Result studentExistById(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest);
}

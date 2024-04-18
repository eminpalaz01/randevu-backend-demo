package com.dershaneproject.randevu.validations.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;
import com.dershaneproject.randevu.exceptions.BusinessException;

public interface WeeklyScheduleValidationService {

    Result isValidateResult(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) throws BusinessException;

    Result studentExistById(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) throws BusinessException ;
}

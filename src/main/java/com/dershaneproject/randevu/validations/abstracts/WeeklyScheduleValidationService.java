package com.dershaneproject.randevu.validations.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;

public interface WeeklyScheduleValidationService {

    public Result isValidateResult(WeeklyScheduleDto weeklyScheduleDto);

    public Result studentExistById(WeeklyScheduleDto weeklyScheduleDto);
}

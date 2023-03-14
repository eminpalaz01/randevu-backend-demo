package com.dershaneproject.randevu.validations.abstracts;

import com.dershaneproject.randevu.dto.TeacherDto;

public interface TeacherValidationService {
	
	boolean isValid(TeacherDto teacherDto);

}

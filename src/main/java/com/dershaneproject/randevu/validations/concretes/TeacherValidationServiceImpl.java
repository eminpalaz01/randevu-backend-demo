package com.dershaneproject.randevu.validations.concretes;

import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.validations.abstracts.TeacherValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TeacherValidationServiceImpl implements TeacherValidationService{

	@Override
	public boolean isValid(TeacherDto teacherDto) {
		// TODO Auto-generated method stub
		return false;
	}

}

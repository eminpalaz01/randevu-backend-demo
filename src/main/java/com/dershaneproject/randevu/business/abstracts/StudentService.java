package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.StudentDto;
import com.dershaneproject.randevu.dto.requests.StudentSaveRequest;
import com.dershaneproject.randevu.dto.responses.StudentSaveResponse;

import java.util.List;

public interface StudentService {
	
	DataResult<StudentSaveResponse> save(StudentSaveRequest studentDto);

	Result deleteById(long id);

	DataResult<List<StudentDto>> findAll();
	
	DataResult<List<StudentDto>> findAllWithWeeklySchedules();

	DataResult<StudentDto> findById(long id);
	
	DataResult<StudentDto> findByIdWithWeeklySchedules(long id);

	DataResult<StudentDto> updateUserNameById(long id, String userName);

	DataResult<StudentDto> updatePasswordById(long id, String password);

	DataResult<StudentDto> updateEmailById(long id, String email);

	DataResult<StudentDto> updateStudentNumberById(long id, String studentNumber);
	
	DataResult<Long> getCount();

}

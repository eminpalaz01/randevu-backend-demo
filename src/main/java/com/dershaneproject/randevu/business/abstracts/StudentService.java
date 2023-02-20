package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.StudentDto;

public interface StudentService {
	
	DataResult<StudentDto> save(StudentDto studentDto);

	Result deleteById(long id);

	DataResult<List<StudentDto>> findAll();

	DataResult<StudentDto> findById(long id);
	
	DataResult<StudentDto> findByIdWithSchedules(long id);

	DataResult<StudentDto> updateUserNameById(long id, String userName);

	DataResult<StudentDto> updatePasswordById(long id, String password);

	DataResult<StudentDto> updateEmailById(long id, String email);

	DataResult<StudentDto> updateStudentNumberById(long id, String studentNumber);
	
	DataResult<Long> getCount();

}

package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.StudentDto;
import com.dershaneproject.randevu.dto.requests.StudentSaveRequest;
import com.dershaneproject.randevu.dto.responses.StudentSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.util.List;

public interface StudentService {
	
	DataResult<StudentSaveResponse> save(StudentSaveRequest studentDto);

	Result deleteById(long id) throws BusinessException;

	DataResult<List<StudentDto>> findAll() throws BusinessException;
	
	DataResult<List<StudentDto>> findAllWithWeeklySchedules() throws BusinessException;

	DataResult<StudentDto> findById(long id) throws BusinessException;
	
	DataResult<StudentDto> findByIdWithWeeklySchedules(long id) throws BusinessException;

	DataResult<StudentDto> updateUserNameById(long id, String userName) throws BusinessException;

	DataResult<StudentDto> updatePasswordById(long id, String password) throws BusinessException;

	DataResult<StudentDto> updateEmailById(long id, String email) throws BusinessException;

	DataResult<StudentDto> updateStudentNumberById(long id, String studentNumber) throws BusinessException;
	
	DataResult<Long> getCount();

}

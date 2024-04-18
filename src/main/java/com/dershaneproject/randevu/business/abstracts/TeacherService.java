package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.dto.requests.TeacherSaveRequest;
import com.dershaneproject.randevu.dto.responses.TeacherSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.util.List;

public interface TeacherService {

	DataResult<TeacherSaveResponse> save(TeacherSaveRequest teacherSaveRequest) throws BusinessException;

	Result deleteById(long id) throws BusinessException;

	DataResult<List<TeacherDto>> findAll() throws BusinessException;

	DataResult<TeacherDto> findById(long id) throws BusinessException;
	
	DataResult<TeacherDto> findByIdWithAllSchedules(long id) throws BusinessException;
	
	DataResult<TeacherDto> findByIdWithSchedules(long id) throws BusinessException;
	
	DataResult<TeacherDto> findByIdWithWeeklySchedules(long id) throws BusinessException;
	
	DataResult<List<TeacherDto>> getByDepartmentId(long departmentId) throws BusinessException;

	DataResult<TeacherDto> updateUserNameById(long id, String userName) throws BusinessException;

	DataResult<TeacherDto> updatePasswordById(long id, String password) throws BusinessException;

	DataResult<TeacherDto> updateEmailById(long id, String email) throws BusinessException;

	DataResult<TeacherDto> updateTeacherNumberById(long id, String teacherNumber) throws BusinessException;

	DataResult<TeacherDto> updateDepartmentById(long id, Long departmentId) throws BusinessException;

	DataResult<Long> getCount();

}

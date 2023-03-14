package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.TeacherDto;

public interface TeacherService {

	DataResult<TeacherDto> save(TeacherDto teacherDto);

	Result deleteById(long id);

	DataResult<List<TeacherDto>> findAll();

	DataResult<TeacherDto> findById(long id);
	
	DataResult<TeacherDto> findByIdWithAllSchedules(long id);
	
	DataResult<TeacherDto> findByIdWithSchedules(long id);
	
	DataResult<TeacherDto> findByIdWithWeeklySchedules(long id);
	
	DataResult<List<TeacherDto>> getByDepartmentId(long departmentId);

	DataResult<TeacherDto> updateUserNameById(long id, String userName);

	DataResult<TeacherDto> updatePasswordById(long id, String password);

	DataResult<TeacherDto> updateEmailById(long id, String email);

	DataResult<TeacherDto> updateTeacherNumberById(long id, String teacherNumber);

	DataResult<TeacherDto> updateDepartmentById(long id, long departmentId);
	
	DataResult<Long> getCount();

}

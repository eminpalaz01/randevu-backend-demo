package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.dto.requests.DepartmentSaveRequest;
import com.dershaneproject.randevu.dto.responses.DepartmentSaveResponse;

import java.util.List;

public interface DepartmentService {
	
	DataResult<DepartmentSaveResponse> save(DepartmentSaveRequest departmentSaveRequest);

	Result deleteById(long id);

	DataResult<List<DepartmentDto>> findAllWithTeachers();

	DataResult<List<DepartmentDto>> findAll();

	DataResult<DepartmentDto> findWithTeachersById(long id);

	DataResult<DepartmentDto> findById(long id);

	DataResult<DepartmentDto> updateCompressingById(long id, String compressing);

	DataResult<DepartmentDto> updateNameById(long id, String name);
	
	DataResult<Long> getCount();

}

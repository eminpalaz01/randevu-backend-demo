package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DepartmentDto;

public interface DepartmentService {
	
	DataResult<DepartmentDto> save(DepartmentDto departmentDto);

	Result deleteById(long id);

	DataResult<List<DepartmentDto>> findAllWithTeachers();

	DataResult<List<DepartmentDto>> findAll();

	DataResult<DepartmentDto> findWithTeachersById(long id);

	DataResult<DepartmentDto> findById(long id);

	DataResult<DepartmentDto> updateCompressingById(long id, String compressing);

	DataResult<DepartmentDto> updateNameById(long id, String name);
	
	DataResult<Long> getCount();

}

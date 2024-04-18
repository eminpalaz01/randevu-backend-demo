package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.dto.requests.DepartmentSaveRequest;
import com.dershaneproject.randevu.dto.responses.DepartmentSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.util.List;

public interface DepartmentService {
	
	DataResult<DepartmentSaveResponse> save(DepartmentSaveRequest departmentSaveRequest);

	Result deleteById(long id) throws BusinessException;

	DataResult<List<DepartmentDto>> findAllWithTeachers() throws BusinessException;

	DataResult<List<DepartmentDto>> findAll() throws BusinessException;

	DataResult<DepartmentDto> findWithTeachersById(long id) throws BusinessException;

	DataResult<DepartmentDto> findById(long id) throws BusinessException;

	DataResult<DepartmentDto> updateCompressingById(long id, String compressing) throws BusinessException;

	DataResult<DepartmentDto> updateNameById(long id, String name) throws BusinessException;
	
	DataResult<Long> getCount();

}

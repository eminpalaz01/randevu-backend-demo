package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemStaffDto;
import com.dershaneproject.randevu.dto.requests.SystemStaffSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemStaffSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.util.List;

public interface SystemStaffService {

	DataResult<SystemStaffSaveResponse> save(SystemStaffSaveRequest systemStaffSaveRequest);

	Result deleteById(long id) throws BusinessException;

	DataResult<List<SystemStaffDto>> findAll() throws BusinessException;
	
    DataResult<List<SystemStaffDto>> findAllWithAllSchedules() throws BusinessException;
    
    DataResult<List<SystemStaffDto>> findAllWithSchedules() throws BusinessException;
    
    DataResult<List<SystemStaffDto>> findAllWithWeeklySchedules() throws BusinessException;
    
	DataResult<SystemStaffDto> findById(long id) throws BusinessException;
	
	DataResult<SystemStaffDto> findByIdWithAllSchedules(long id) throws BusinessException;
	
	DataResult<SystemStaffDto> findByIdWithSchedules(long id) throws BusinessException;
	
	DataResult<SystemStaffDto> findByIdWithWeeklySchedules(long id) throws BusinessException;

	DataResult<SystemStaffDto> updateUserNameById(long id, String userName) throws BusinessException;

	DataResult<SystemStaffDto> updatePasswordById(long id, String password) throws BusinessException;

	DataResult<SystemStaffDto> updateEmailById(long id, String email) throws BusinessException;
	
	DataResult<Long> getCount();

}

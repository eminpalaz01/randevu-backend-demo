package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.dto.requests.SystemAdministratorSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemAdministratorSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;

import java.util.List;

public interface SystemAdministratorService {

	DataResult<SystemAdministratorSaveResponse> save(SystemAdministratorSaveRequest systemAdministratorSaveRequest);

	Result deleteById(long id) throws BusinessException;

	DataResult<List<SystemAdministratorDto>> findAll() throws BusinessException;
	
	DataResult<List<SystemAdministratorDto>> findAllWithAllSchedules() throws BusinessException;
	
	DataResult<List<SystemAdministratorDto>> findAllWithSchedules() throws BusinessException;
	
	DataResult<List<SystemAdministratorDto>> findAllWithWeeklySchedules() throws BusinessException;

	DataResult<SystemAdministratorDto> findById(long id) throws BusinessException;
	
	DataResult<SystemAdministratorDto> findByIdWithAllSchedules(long id) throws BusinessException;
	
	DataResult<SystemAdministratorDto> findByIdWithSchedules(long id) throws BusinessException;
	
	DataResult<SystemAdministratorDto> findByIdWithWeeklySchedules(long id) throws BusinessException;

	DataResult<SystemAdministratorDto> updateUserNameById(long id, String userName) throws BusinessException;

	DataResult<SystemAdministratorDto> updatePasswordById(long id, String password) throws BusinessException;

	DataResult<SystemAdministratorDto> updateEmailById(long id, String email) throws BusinessException;
	
	DataResult<Long> getCount();
	
}

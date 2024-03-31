package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.dto.requests.SystemAdministratorSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemAdministratorSaveResponse;

import java.util.List;

public interface SystemAdministratorService {

	DataResult<SystemAdministratorSaveResponse> save(SystemAdministratorSaveRequest systemAdministratorSaveRequest);

	Result deleteById(long id);

	DataResult<List<SystemAdministratorDto>> findAll();
	
	DataResult<List<SystemAdministratorDto>> findAllWithAllSchedules();
	
	DataResult<List<SystemAdministratorDto>> findAllWithSchedules();
	
	DataResult<List<SystemAdministratorDto>> findAllWithWeeklySchedules();

	DataResult<SystemAdministratorDto> findById(long id);
	
	DataResult<SystemAdministratorDto> findByIdWithAllSchedules(long id);
	
	DataResult<SystemAdministratorDto> findByIdWithSchedules(long id);
	
	DataResult<SystemAdministratorDto> findByIdWithWeeklySchedules(long id);

	DataResult<SystemAdministratorDto> updateUserNameById(long id, String userName);

	DataResult<SystemAdministratorDto> updatePasswordById(long id, String password);

	DataResult<SystemAdministratorDto> updateEmailById(long id, String email);
	
	DataResult<Long> getCount();
	
}

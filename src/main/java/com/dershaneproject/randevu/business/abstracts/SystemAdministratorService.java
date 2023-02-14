package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemAdministratorDto;

public interface SystemAdministratorService {

	DataResult<SystemAdministratorDto> save(SystemAdministratorDto systemAdministratorDto);

	Result deleteById(long id);

	DataResult<List<SystemAdministratorDto>> findAll();

	DataResult<SystemAdministratorDto> findById(long id);

	DataResult<SystemAdministratorDto> updateUserNameById(long id, String userName);

	DataResult<SystemAdministratorDto> updatePasswordById(long id, String password);

	DataResult<SystemAdministratorDto> updateEmailById(long id, String email);
	
	DataResult<Long> getCount();
}

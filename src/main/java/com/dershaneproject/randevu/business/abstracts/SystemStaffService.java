package com.dershaneproject.randevu.business.abstracts;

import java.util.List;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemStaffDto;

public interface SystemStaffService {

	DataResult<SystemStaffDto> save(SystemStaffDto systemStaffDto);

	Result deleteById(long id);

	DataResult<List<SystemStaffDto>> findAll();
	
    DataResult<List<SystemStaffDto>> findAllWithSchedules();
    
	DataResult<SystemStaffDto> findById(long id);
	
	DataResult<SystemStaffDto> findByIdWithSchedules(long id);

	DataResult<SystemStaffDto> updateUserNameById(long id, String userName);

	DataResult<SystemStaffDto> updatePasswordById(long id, String password);

	DataResult<SystemStaffDto> updateEmailById(long id, String email);
	
	DataResult<Long> getCount();

}

package com.dershaneproject.randevu.business.abstracts;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemStaffDto;
import com.dershaneproject.randevu.dto.requests.SystemStaffSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemStaffSaveResponse;

import java.util.List;

public interface SystemStaffService {

	DataResult<SystemStaffSaveResponse> save(SystemStaffSaveRequest systemStaffSaveRequest);

	Result deleteById(long id);

	DataResult<List<SystemStaffDto>> findAll();
	
    DataResult<List<SystemStaffDto>> findAllWithAllSchedules();
    
    DataResult<List<SystemStaffDto>> findAllWithSchedules();
    
    DataResult<List<SystemStaffDto>> findAllWithWeeklySchedules();
    
	DataResult<SystemStaffDto> findById(long id);
	
	DataResult<SystemStaffDto> findByIdWithAllSchedules(long id);
	
	DataResult<SystemStaffDto> findByIdWithSchedules(long id);
	
	DataResult<SystemStaffDto> findByIdWithWeeklySchedules(long id);

	DataResult<SystemStaffDto> updateUserNameById(long id, String userName);

	DataResult<SystemStaffDto> updatePasswordById(long id, String password);

	DataResult<SystemStaffDto> updateEmailById(long id, String email);
	
	DataResult<Long> getCount();

}

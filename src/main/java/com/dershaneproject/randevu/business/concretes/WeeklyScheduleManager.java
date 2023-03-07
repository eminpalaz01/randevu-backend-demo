package com.dershaneproject.randevu.business.concretes;

import java.util.List;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;

@Service
public class WeeklyScheduleManager implements WeeklyScheduleService{

	// Buradan devam edilecek 7 Mart 2023 23:31
	@Override
	public DataResult<WeeklyScheduleDto> save(WeeklySchedule weeklySchedule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<WeeklyScheduleDto> findById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateFullById(long id, Boolean full) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateTeacherById(long id, long teacherId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateStudentById(long id, long studentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateLastUpdateDateSystemWorkerById(long id,
			long lastUpdateDateSystemWorkerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateDayOfWeekById(long id, long dayOfWeekId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateHourById(long id, long hourId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		return null;
	}

}

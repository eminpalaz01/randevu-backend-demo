package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.SystemAdministratorService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemAdministratorDao;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.dto.requests.SystemAdministratorSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemAdministratorSaveResponse;
import com.dershaneproject.randevu.entities.concretes.SystemAdministrator;
import com.dershaneproject.randevu.mappers.ScheduleMapper;
import com.dershaneproject.randevu.mappers.SystemAdministratorMapper;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemAdministratorManager implements SystemAdministratorService {

	private final SystemAdministratorDao systemAdministratorDao;

	private final SystemAdministratorMapper systemAdministratorMapper;
	private final ScheduleMapper scheduleMapper;
	private final WeeklyScheduleMapper weeklyScheduleMapper;

	@Override
	public DataResult<SystemAdministratorSaveResponse> save(SystemAdministratorSaveRequest systemAdministratorSaveRequest) {
		try {
			SystemAdministrator systemAdministrator = systemAdministratorDao.save(systemAdministratorMapper.toEntity(systemAdministratorSaveRequest));
			SystemAdministratorSaveResponse systemAdministratorSaveResponse = systemAdministratorMapper.toSaveResponse(systemAdministrator);
			return new DataResult<SystemAdministratorSaveResponse>(systemAdministratorSaveResponse, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorSaveResponse>(false, e.getMessage());
		}

	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);
			if (systemAdministrator.isPresent()) {
				systemAdministratorDao.deleteById(id);
				return new Result(true, id + " id'li sistem yöneticisi silindi.");
			}

			return new Result(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}
	}



	@Override
	public DataResult<SystemAdministratorDto> findById(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (systemAdministrator.isPresent()) {
				SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());
				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}	
		
	@Override
	public DataResult<SystemAdministratorDto> findByIdWithAllSchedules(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (systemAdministrator.isPresent()) {
				SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

				List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemAdministrator.get().getSchedules());
				List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemAdministrator.get().getWeeklySchedules());
				systemAdministratorDto.setSchedules(schedulesDto);
				systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> findByIdWithWeeklySchedules(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (systemAdministrator.isPresent()) {
				SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

				List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemAdministrator.get().getWeeklySchedules());
				systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<SystemAdministratorDto> findByIdWithSchedules(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (systemAdministrator.isPresent()) {
				SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

				List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemAdministrator.get().getSchedules());
				systemAdministratorDto.setSchedules(schedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemAdministratorDto>> findAll() {
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (!systemAdministrators.isEmpty()) {
				List<SystemAdministratorDto> systemAdministratorsDto = systemAdministratorMapper.toDtoList(systemAdministrators);
				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");
			} else {
				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithSchedules() {
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (!systemAdministrators.isEmpty()) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator);

					List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemAdministrator.getSchedules());
					systemAdministratorDto.setSchedules(schedulesDto);

					systemAdministratorsDto.add(systemAdministratorDto);
				});
				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");
			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}
		
	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithAllSchedules() {
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (!systemAdministrators.isEmpty()) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator);

					List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemAdministrator.getSchedules());
					List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemAdministrator.getWeeklySchedules());

					systemAdministratorDto.setSchedules(schedulesDto);
					systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

					systemAdministratorsDto.add(systemAdministratorDto);
				});
				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");
			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithWeeklySchedules() {
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (!systemAdministrators.isEmpty()) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator);

					List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemAdministrator.getWeeklySchedules());
					systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

					systemAdministratorsDto.add(systemAdministratorDto);
				});
				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");
			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updateUserNameById(long id, String userName) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (systemAdministrator.isPresent()) {
				systemAdministrator.get().setUserName(userName);
				systemAdministratorDao.save(systemAdministrator.get());
				SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisinin kullanıcı adı güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updatePasswordById(long id, String password) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (systemAdministrator.isPresent()) {
				systemAdministrator.get().setPassword(password);
				systemAdministratorDao.save(systemAdministrator.get());
				SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisinin şifresi güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updateEmailById(long id, String email) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (systemAdministrator.isPresent()) {
				systemAdministrator.get().setEmail(email);
				systemAdministratorDao.save(systemAdministrator.get());
				SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());
				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisinin maili güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		try {
			return new DataResult<Long>(systemAdministratorDao.count(), true, "Sistem yöneticilerinin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}	
	}
}

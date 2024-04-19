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
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.mappers.ScheduleMapper;
import com.dershaneproject.randevu.mappers.SystemAdministratorMapper;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
		SystemAdministrator systemAdministrator = systemAdministratorDao.save(systemAdministratorMapper.toEntity(systemAdministratorSaveRequest));
		SystemAdministratorSaveResponse systemAdministratorSaveResponse = systemAdministratorMapper.toSaveResponse(systemAdministrator);
		return new DataResult<SystemAdministratorSaveResponse>(systemAdministratorSaveResponse, "Veritabanına kaydedildi.");
	}

	@Override
	public Result deleteById(long id) throws BusinessException {
		Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);
		if (systemAdministrator.isPresent()) {
			systemAdministratorDao.deleteById(id);
			return new Result(id + " id'li sistem yöneticisi silindi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem yöneticisi bulunamadı."));
	}



	@Override
	public DataResult<SystemAdministratorDto> findById(long id) throws BusinessException {
		Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

		if (systemAdministrator.isPresent()) {
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());
			return new DataResult<SystemAdministratorDto>(systemAdministratorDto,
					id + " id'li sistem yöneticisi getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem yöneticisi bulunamadı."));
	}	
		
	@Override
	public DataResult<SystemAdministratorDto> findByIdWithAllSchedules(long id) throws BusinessException {
		Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

		if (systemAdministrator.isPresent()) {
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemAdministrator.get().getSchedules());
			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemAdministrator.get().getWeeklySchedules());
			systemAdministratorDto.setSchedules(schedulesDto);
			systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

			return new DataResult<SystemAdministratorDto>(systemAdministratorDto,
					id + " id'li sistem yöneticisi getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem yöneticisi bulunamadı."));
	}

	@Override
	public DataResult<SystemAdministratorDto> findByIdWithWeeklySchedules(long id) throws BusinessException {
		Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

		if (systemAdministrator.isPresent()) {
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemAdministrator.get().getWeeklySchedules());
			systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

			return new DataResult<SystemAdministratorDto>(systemAdministratorDto,
					id + " id'li sistem yöneticisi getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem yöneticisi bulunamadı."));
	}
	
	@Override
	public DataResult<SystemAdministratorDto> findByIdWithSchedules(long id) throws BusinessException {
		Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

		if (systemAdministrator.isPresent()) {
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemAdministrator.get().getSchedules());
			systemAdministratorDto.setSchedules(schedulesDto);

			return new DataResult<SystemAdministratorDto>(systemAdministratorDto,
					id + " id'li sistem yöneticisi getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem yöneticisi bulunamadı."));
	}

	@Override
	public DataResult<List<SystemAdministratorDto>> findAll() throws BusinessException {
		List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
		if (systemAdministrators.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Sistem yöneticisi bulunamadı."));

		List<SystemAdministratorDto> systemAdministratorsDto = systemAdministratorMapper.toDtoList(systemAdministrators);
		return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, "Sistem yöneticileri getirildi.");
	}
	
	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithSchedules() throws BusinessException {
		List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
		if (systemAdministrators.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Sistem yöneticisi bulunamadı."));

		List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();
		systemAdministrators.forEach(systemAdministrator -> {
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator);

			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemAdministrator.getSchedules());
			systemAdministratorDto.setSchedules(schedulesDto);

			systemAdministratorsDto.add(systemAdministratorDto);
		});
		return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto,  "Sistem yöneticileri getirildi.");

	}
		
	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithAllSchedules() throws BusinessException {
		List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
		if (systemAdministrators.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Sistem yöneticisi bulunamadı."));

		List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();
		systemAdministrators.forEach(systemAdministrator -> {
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator);

			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemAdministrator.getSchedules());
			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemAdministrator.getWeeklySchedules());

			systemAdministratorDto.setSchedules(schedulesDto);
			systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

			systemAdministratorsDto.add(systemAdministratorDto);
		});
		return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto,  "Sistem yöneticileri getirildi.");
	}

	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithWeeklySchedules() throws BusinessException {
		List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
		if (systemAdministrators.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Sistem yöneticisi bulunamadı."));

		List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();
		systemAdministrators.forEach(systemAdministrator -> {
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator);

			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemAdministrator.getWeeklySchedules());
			systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

			systemAdministratorsDto.add(systemAdministratorDto);
		});
		return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto,  "Sistem yöneticileri getirildi.");
	}

	@Override
	public DataResult<SystemAdministratorDto> updateUserNameById(long id, String userName) throws BusinessException {
		Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

		if (systemAdministrator.isPresent()) {
			systemAdministrator.get().setUserName(userName);
			systemAdministratorDao.save(systemAdministrator.get());
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

			return new DataResult<SystemAdministratorDto>(systemAdministratorDto,
					id + " id'li sistem yöneticisinin kullanıcı adı güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem yöneticisi bulunamadı."));
	}

	@Override
	public DataResult<SystemAdministratorDto> updatePasswordById(long id, String password) throws BusinessException {
		Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

		if (systemAdministrator.isPresent()) {
			systemAdministrator.get().setPassword(password);
			systemAdministratorDao.save(systemAdministrator.get());
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());

			return new DataResult<SystemAdministratorDto>(systemAdministratorDto,
					id + " id'li sistem yöneticisinin şifresi güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem yöneticisi bulunamadı."));
	}

	@Override
	public DataResult<SystemAdministratorDto> updateEmailById(long id, String email) throws BusinessException {
		Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

		if (systemAdministrator.isPresent()) {
			systemAdministrator.get().setEmail(email);
			systemAdministratorDao.save(systemAdministrator.get());
			SystemAdministratorDto systemAdministratorDto = systemAdministratorMapper.toDto(systemAdministrator.get());
			return new DataResult<SystemAdministratorDto>(systemAdministratorDto,
					id + " id'li sistem yöneticisinin maili güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem yöneticisi bulunamadı."));
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(systemAdministratorDao.count(), "Sistem yöneticilerinin sayısı getirildi.");
	}
}

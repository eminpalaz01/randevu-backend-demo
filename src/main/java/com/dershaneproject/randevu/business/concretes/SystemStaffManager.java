package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.SystemStaffService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemStaffDao;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.SystemStaffDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.dto.requests.SystemStaffSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemStaffSaveResponse;
import com.dershaneproject.randevu.entities.concretes.SystemStaff;
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.mappers.ScheduleMapper;
import com.dershaneproject.randevu.mappers.SystemStaffMapper;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemStaffManager implements SystemStaffService {

	private final  SystemStaffDao systemStaffDao;

	private final SystemStaffMapper systemStaffMapper;
	private final ScheduleMapper scheduleMapper;
	private final WeeklyScheduleMapper weeklyScheduleMapper;

	@Override
	public DataResult<SystemStaffSaveResponse> save(SystemStaffSaveRequest systemStaffSaveRequest) {
		SystemStaff systemStaff = systemStaffDao.save(systemStaffMapper.toEntity(systemStaffSaveRequest));
		SystemStaffSaveResponse systemStaffSaveResponse = systemStaffMapper.toSaveResponse(systemStaff);
		return new DataResult<SystemStaffSaveResponse>(systemStaffSaveResponse, "Veritabanına kaydedildi.");
	}

	@Override
	public Result deleteById(long id) {
		Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);
		if (systemStaff.isPresent()) {
			systemStaffDao.deleteById(id);
			return new Result(id + " id'li sistem çalışanı silindi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem çalışanı bulunamadı."));
	}

	@Override
	public DataResult<SystemStaffDto> findById(long id) throws BusinessException {
		Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

		if (systemStaff.isPresent()) {
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());
			return new DataResult<SystemStaffDto>(systemStaffDto, id + " id'li sistem çalışanı getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem çalışanı bulunamadı."));
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithAllSchedules(long id) throws BusinessException {
		Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

		if (systemStaff.isPresent()) {
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());

			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemStaff.get().getSchedules());
			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemStaff.get().getWeeklySchedules());

			systemStaffDto.setSchedules(schedulesDto);
			systemStaffDto.setWeeklySchedules(weeklySchedulesDto);

			return new DataResult<SystemStaffDto>(systemStaffDto, id + " id'li sistem çalışanı getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem çalışanı bulunamadı."));
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithWeeklySchedules(long id) throws BusinessException {
		Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

		if (systemStaff.isPresent()) {
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());

			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemStaff.get().getWeeklySchedules());
			systemStaffDto.setWeeklySchedules(weeklySchedulesDto);

			return new DataResult<SystemStaffDto>(systemStaffDto, id + " id'li sistem çalışanı getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem çalışanı bulunamadı."));
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithSchedules(long id) throws BusinessException {
		Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

		if (systemStaff.isPresent()) {
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());

			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemStaff.get().getSchedules());
			systemStaffDto.setSchedules(schedulesDto);

			return new DataResult<SystemStaffDto>(systemStaffDto, id + " id'li sistem çalışanı getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem çalışanı bulunamadı."));
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAll() throws BusinessException {
		List<SystemStaff> systemStaffs = systemStaffDao.findAll();
		if (systemStaffs.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Sistem çalışanı bulunamadı."));

		List<SystemStaffDto> systemStaffsDto = systemStaffMapper.toDtoList(systemStaffs);
		return new DataResult<List<SystemStaffDto>>(systemStaffsDto, "Sistem çalışanları getirildi.");
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithAllSchedules() throws BusinessException {
		List<SystemStaff> systemStaffs = systemStaffDao.findAll();
		if (systemStaffs.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Sistem çalışanı bulunamadı."));

		List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();
		systemStaffs.forEach(systemStaff -> {
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff);

			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemStaff.getSchedules());
			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemStaff.getWeeklySchedules());

			systemStaffDto.setSchedules(schedulesDto);
			systemStaffDto.setWeeklySchedules(weeklySchedulesDto);

			systemStaffsDto.add(systemStaffDto);
		});

		return new DataResult<List<SystemStaffDto>>(systemStaffsDto, "Sistem çalışanları getirildi.");
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithSchedules() throws BusinessException {
		List<SystemStaff> systemStaffs = systemStaffDao.findAll();
		if (systemStaffs.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Sistem çalışanı bulunamadı."));

		List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

		systemStaffs.forEach(systemStaff -> {
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff);

			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemStaff.getSchedules());
			systemStaffDto.setSchedules(schedulesDto);

			systemStaffsDto.add(systemStaffDto);
		});

		return new DataResult<List<SystemStaffDto>>(systemStaffsDto, "Sistem çalışanları getirildi.");
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithWeeklySchedules() throws BusinessException {
		List<SystemStaff> systemStaffs = systemStaffDao.findAll();
		if (systemStaffs.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Sistem çalışanı bulunamadı."));

		List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

		systemStaffs.forEach(systemStaff -> {
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff);

			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemStaff.getWeeklySchedules());
			systemStaffDto.setWeeklySchedules(weeklySchedulesDto);

			systemStaffsDto.add(systemStaffDto);
		});

		return new DataResult<List<SystemStaffDto>>(systemStaffsDto, "Sistem çalışanları getirildi.");
	}

	@Override
	public DataResult<SystemStaffDto> updateUserNameById(long id, String userName) throws BusinessException {
		Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

		if (systemStaff.isPresent()) {
			systemStaff.get().setUserName(userName);
			systemStaffDao.save(systemStaff.get());
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());
			return new DataResult<SystemStaffDto>(systemStaffDto,
					id + " id'li sistem çalışanının kullanıcı adı güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem çalışanı bulunamadı."));
	}

	@Override
	public DataResult<SystemStaffDto> updatePasswordById(long id, String password) throws BusinessException {
		Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

		if (systemStaff.isPresent()) {
			systemStaff.get().setPassword(password);
			systemStaffDao.save(systemStaff.get());
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());
			return new DataResult<SystemStaffDto>(systemStaffDto,
					id + " id'li sistem çalışanının şifresi güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem çalışanı bulunamadı."));
	}

	@Override
	public DataResult<SystemStaffDto> updateEmailById(long id, String email) throws BusinessException {
		Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

		if (systemStaff.isPresent()) {
			systemStaff.get().setEmail(email);
			systemStaffDao.save(systemStaff.get());
			SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());
			return new DataResult<SystemStaffDto>(systemStaffDto,
					id + " id'li sistem çalışanının maili güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li sistem çalışanı bulunamadı."));
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(systemStaffDao.count(), "Sistem çalışanlarının sayısı getirildi.");
	}
}

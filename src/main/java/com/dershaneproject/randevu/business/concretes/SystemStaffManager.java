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
import com.dershaneproject.randevu.mappers.ScheduleMapper;
import com.dershaneproject.randevu.mappers.SystemStaffMapper;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import lombok.RequiredArgsConstructor;
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
		try {
			SystemStaff systemStaff = systemStaffDao.save(systemStaffMapper.toEntity(systemStaffSaveRequest));
			SystemStaffSaveResponse systemStaffSaveResponse = systemStaffMapper.toSaveResponse(systemStaff);

			return new DataResult<SystemStaffSaveResponse>(systemStaffSaveResponse, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			return new DataResult<SystemStaffSaveResponse>(false, e.getMessage());
		}
	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);
			if (systemStaff.isPresent()) {
				systemStaffDao.deleteById(id);
				return new Result(true, id + " id'li sistem çalışanı silindi.");
			}
			return new Result(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> findById(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (systemStaff.isPresent()) {
				SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());
				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithAllSchedules(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (systemStaff.isPresent()) {
				SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());

				List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemStaff.get().getSchedules());
				List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemStaff.get().getWeeklySchedules());

				systemStaffDto.setSchedulesDto(schedulesDto);
				systemStaffDto.setWeeklySchedulesDto(weeklySchedulesDto);

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithWeeklySchedules(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (systemStaff.isPresent()) {
				SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());

				List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemStaff.get().getWeeklySchedules());
				systemStaffDto.setWeeklySchedulesDto(weeklySchedulesDto);

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithSchedules(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (systemStaff.isPresent()) {
				SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());

				List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemStaff.get().getSchedules());
				systemStaffDto.setSchedulesDto(schedulesDto);

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAll() {
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (!systemStaffs.isEmpty()) {
				List<SystemStaffDto> systemStaffsDto = systemStaffMapper.toDtoList(systemStaffs);
				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithAllSchedules() {
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (!systemStaffs.isEmpty()) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff);

					List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemStaff.getSchedules());
					List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemStaff.getWeeklySchedules());

					systemStaffDto.setSchedulesDto(schedulesDto);
					systemStaffDto.setWeeklySchedulesDto(weeklySchedulesDto);

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithSchedules() {
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (!systemStaffs.isEmpty()) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff);

					List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(systemStaff.getSchedules());
					systemStaffDto.setSchedulesDto(schedulesDto);

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithWeeklySchedules() {
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (!systemStaffs.isEmpty()) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff);

					List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(systemStaff.getWeeklySchedules());
					systemStaffDto.setWeeklySchedulesDto(weeklySchedulesDto);

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updateUserNameById(long id, String userName) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (systemStaff.isPresent()) {
				systemStaff.get().setUserName(userName);
				systemStaffDao.save(systemStaff.get());
				SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());
				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının kullanıcı adı güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updatePasswordById(long id, String password) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (systemStaff.isPresent()) {
				systemStaff.get().setPassword(password);
				systemStaffDao.save(systemStaff.get());
				SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());
				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının şifresi güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updateEmailById(long id, String email) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (systemStaff.isPresent()) {
				systemStaff.get().setEmail(email);
				systemStaffDao.save(systemStaff.get());
				SystemStaffDto systemStaffDto = systemStaffMapper.toDto(systemStaff.get());
				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının maili güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		try {
			return new DataResult<Long>(systemStaffDao.count(), true, "Sistem çalışanlarının sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}
}

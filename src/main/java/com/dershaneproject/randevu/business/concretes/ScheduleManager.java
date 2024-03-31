package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.ScheduleSaveResponse;
import com.dershaneproject.randevu.entities.concretes.*;
import com.dershaneproject.randevu.mappers.ScheduleMapper;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleManager implements ScheduleService {

	private final ScheduleDao scheduleDao;
	private final TeacherDao teacherDao;
	private final DayOfWeekDao dayOfWeekDao;
	private final HourDao hourDao;
	private final SystemWorkerDao systemWorkerDao;

	private final ScheduleValidationService scheduleValidationService;

	private final ScheduleMapper scheduleMapper;

	@Override
	public DataResult<ScheduleSaveResponse> save(ScheduleSaveRequest scheduleSaveRequest) {
		try {
			Result validateResult = scheduleValidationService.isValidateResult(scheduleSaveRequest);
			if (validateResult.isSuccess()) {
				Schedule schedule = scheduleDao.save(scheduleMapper.toEntity(scheduleSaveRequest));
				ScheduleSaveResponse scheduleSaveResponse = scheduleMapper.toSaveResponse(schedule);
				return new DataResult<ScheduleSaveResponse>(scheduleSaveResponse, true, "Program veritabanına eklendi.");
			} else {
				return new DataResult<>(false, validateResult.getMessage());
			}
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<ScheduleSaveResponse>> saveAll(List<ScheduleSaveRequest> scheduleSaveRequestList) {
		// Firstly, scheduleSaveRequestList is validating one by one
		for (ScheduleSaveRequest scheduleSaveRequest : scheduleSaveRequestList) {
			Result resultValidation = scheduleValidationService.isValidateResult(scheduleSaveRequest);
			if(!(resultValidation.isSuccess())) {
				return new DataResult<>(false, resultValidation.getMessage());
			}
		}
		List<Schedule> schedules = new ArrayList<Schedule>();
		try {
			// Schedules will create and add to list
            for (ScheduleSaveRequest scheduleSaveRequest : scheduleSaveRequestList) {
                schedules.add(scheduleMapper.toEntity(scheduleSaveRequest));
            }
			// Schedules created and added to list

			// Schedule saved and id and dates return to list
			schedules = scheduleDao.saveAll(schedules);
			schedules = scheduleDao.findAllByIdSorted(schedules.stream()
					.map(Schedule::getId)
					.collect(Collectors.toList()));
			List<ScheduleSaveResponse> scheduleSaveResponseList = scheduleMapper.toSaveResponseList(schedules);
			return new DataResult<List<ScheduleSaveResponse>>(scheduleSaveResponseList, true, "Programlar veritabanına eklendi.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			if (!(schedule.equals(Optional.empty()))) {
				scheduleDao.deleteById(id);
				return new Result(true, id + " id'li program silindi.");
			}

			return new Result(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<ScheduleDto>> findAll() {
			List<Schedule> schedules = scheduleDao.findAll();
			if (!schedules.isEmpty()) {
				List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(schedules);
				return new DataResult<List<ScheduleDto>>(schedulesDto, true, "Programlar getirildi.");
			} else {
				return new DataResult<>(false, "Program bulunamadı.");
			}
	}

	@Override
	public DataResult<ScheduleDto> findById(long id) {
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			if (schedule.isPresent()) {
				ScheduleDto scheduleDto = scheduleMapper.toDto(schedule.get());
				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li program getirildi.");
			}
			return new DataResult<>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateFullById(long id, Boolean full) {
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);

			if (schedule.isPresent()) {
				schedule.get().setFull(full);
				ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın doluluğu güncellendi.");
			}
			return new DataResult<>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<ScheduleDto> updateTeacherById(long id, Long teacherId) {
		try {
			if(teacherId == null){
				return new DataResult<>(false, "Öğretmen Boş olamaz.");
			}
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<Teacher> teacher = teacherDao.findById(teacherId);

			if (schedule.isPresent() && teacher.isPresent()) {
				schedule.get().setTeacher(teacher.get());
				ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın öğretmeni güncellendi.");
			} else {
				if (schedule.equals(Optional.empty())) {
					return new DataResult<>(false, id + " id'li program bulunamadı.");

				}
			}
			return new DataResult<>(false,
					id + " id'li program için verdiğiniz öğretmen id'sini kontrol ediniz.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<ScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId) {
		if (lastUpdateDateSystemWorkerId == null){
			return new DataResult<>(false, "Son güncelleme yapan sistem çalışanı boş bırakılamaz.");
		}
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<SystemWorker> systemWorker = systemWorkerDao.findById(lastUpdateDateSystemWorkerId);

			if (schedule.isPresent() && systemWorker.isPresent()) {
				schedule.get().setLastUpdateDateSystemWorker(systemWorker.get());
				ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
				return new DataResult<ScheduleDto>(scheduleDto, true,
						id + " id'li programın üstünde son değişilik yapan sistem çalışanı güncellendi.");
			} else {
				if (schedule.isEmpty()) {
					return new DataResult<>(false, id + " id'li program bulunamadı.");

				}
			}

			return new DataResult<>(false,
					id + " id'li program için verdiğiiz sistem çalışanını kontrol ediniz.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) {

		if (dayOfWeekId == null) {
			return new DataResult<>(false, "Gün boş bırakılamaz.");
		}
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(dayOfWeekId);

			if (schedule.isPresent() && dayOfWeek.isPresent()) {
				schedule.get().setDayOfWeek(dayOfWeek.get());
				ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın günü güncellendi.");
			} else {
				if (schedule.isEmpty()) {
					return new DataResult<>(false, id + " id'li program bulunamadı.");
				}
			}
			return new DataResult<>(false,
					id + " id'li program için verdiğiniz gün id'sini kontrol ediniz.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateHourById(long id, Long hourId) {

		if (hourId == null) {
			return new DataResult<>(Boolean.FALSE, "Saat boş bırakılamaz.");
		}

		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<Hour> hour = hourDao.findById(hourId);

			if (schedule.isPresent() && hour.isPresent()) {
				schedule.get().setHour(hour.get());
				ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın saati güncellendi.");
			} else {
				if (schedule.isEmpty()) {
					return new DataResult<>(false, id + " id'li program bulunamadı.");
				}
			}
			return new DataResult<>(false,
					id + " id'li program için verdiğiniz saat id'sini kontrol ediniz.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateDescriptionById(long id, String description) {
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);

			if (schedule.isPresent()) {
				schedule.get().setDescription(description);
				ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın açıklaması güncellendi.");
			}
			return new DataResult<>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(scheduleDao.count(), true, "Programların sayısı getirildi.");
	}

	@Override
	public DataResult<List<ScheduleDto>> findAllByTeacherId(long teacherId) {
		List<Schedule> schedules = scheduleDao.findAllByTeacherIdSorted(teacherId);

		if (!schedules.isEmpty()) {
			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(schedules);
			return new DataResult<List<ScheduleDto>>(schedulesDto, true, "Programlar getirildi.");

		} else {
			return new DataResult<>(false, "Program bulunamadı.");
		}
	}

}

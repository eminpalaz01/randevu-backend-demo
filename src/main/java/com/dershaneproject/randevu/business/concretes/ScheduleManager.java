package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.ScheduleSaveResponse;
import com.dershaneproject.randevu.entities.concretes.*;
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.mappers.ScheduleMapper;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleManager implements ScheduleService {

	@PersistenceContext
	private EntityManager entityManager;

	private final ScheduleDao scheduleDao;
	private final TeacherDao teacherDao;
	private final DayOfWeekDao dayOfWeekDao;
	private final HourDao hourDao;
	private final SystemWorkerDao systemWorkerDao;

	private final ScheduleValidationService scheduleValidationService;

	private final ScheduleMapper scheduleMapper;

	@Override
	public DataResult<ScheduleSaveResponse> save(ScheduleSaveRequest scheduleSaveRequest) throws BusinessException {
		scheduleValidationService.isValidateResult(scheduleSaveRequest);

		Schedule schedule = scheduleDao.save(scheduleMapper.toEntity(scheduleSaveRequest));
		ScheduleSaveResponse scheduleSaveResponse = scheduleMapper.toSaveResponse(schedule);

		return new DataResult<ScheduleSaveResponse>(scheduleSaveResponse, "Program veritabanına eklendi.");
	}

	@Override
	public DataResult<List<ScheduleSaveResponse>> saveAll(List<ScheduleSaveRequest> scheduleSaveRequestList) throws BusinessException {
		// Firstly, scheduleSaveRequestList is validating one by one
		scheduleSaveRequestList.forEach(scheduleValidationService::isValidateResult);
		List<Schedule> schedules = new ArrayList<Schedule>();

		// Schedules will create and add to list
		for (ScheduleSaveRequest scheduleSaveRequest : scheduleSaveRequestList) {
			schedules.add(scheduleMapper.toEntity(scheduleSaveRequest));
		}
		// Schedules created and added to list

		// Schedule saved and id and dates return to list
		schedules = scheduleDao.saveAllAndFlush(schedules);
		// it goes to db every step so using clear for now
//		for (Schedule schedule : schedules) {
//			entityManager.refresh(schedule);
//		}
		entityManager.clear();
		schedules = scheduleDao.findAllByIdSorted(schedules.stream()
				.map(Schedule::getId)
				.collect(Collectors.toList()));

		List<ScheduleSaveResponse> scheduleSaveResponseList = scheduleMapper.toSaveResponseList(schedules);
		return new DataResult<List<ScheduleSaveResponse>>(scheduleSaveResponseList, "Programlar veritabanına eklendi.");
	}

	@Override
	public Result deleteById(long id) throws BusinessException {
		Optional<Schedule> schedule = scheduleDao.findById(id);
		if (schedule.isPresent()) {
			scheduleDao.deleteById(id);
			return new Result(id + " id'li program silindi.");
		}

		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li program bulunamadı."));
	}

	@Override
	public DataResult<List<ScheduleDto>> findAll() throws BusinessException {
		List<Schedule> schedules = scheduleDao.findAll();
		if (schedules.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Program bulunamadı."));

		List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(schedules);
		return new DataResult<List<ScheduleDto>>(schedulesDto, "Programlar getirildi.");
	}

	@Override
	public DataResult<ScheduleDto> findById(long id) throws BusinessException {
		Optional<Schedule> schedule = scheduleDao.findById(id);
		if (schedule.isPresent()) {
			ScheduleDto scheduleDto = scheduleMapper.toDto(schedule.get());
			return new DataResult<ScheduleDto>(scheduleDto, id + " id'li program getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li program bulunamadı."));
	}

	@Override
	public DataResult<ScheduleDto> updateFullById(long id, Boolean full) throws BusinessException {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			if (schedule.isPresent()) {
				schedule.get().setFull(full);
				ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
				return new DataResult<ScheduleDto>(scheduleDto, id + " id'li programın doluluğu güncellendi.");
			}
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li program bulunamadı."));
	}

	@Override
	public DataResult<ScheduleDto> updateTeacherById(long id, Long teacherId) throws BusinessException {
		if(teacherId == null){
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(("Öğretmen Boş olamaz.")));
		}
		Optional<Schedule> schedule = scheduleDao.findById(id);
		if (schedule.isEmpty()) {
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li program bulunamadı."));
		}
		Optional<Teacher> teacher = teacherDao.findById(teacherId);
		if (teacher.isEmpty()) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(id + " id'li program için verdiğiniz öğretmen id'sini kontrol ediniz."));
		}
		schedule.get().setTeacher(teacher.get());
		ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
		return new DataResult<ScheduleDto>(scheduleDto, id + " id'li programın öğretmeni güncellendi.");
	}

	@Override
	public DataResult<ScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId) throws BusinessException {
		if (lastUpdateDateSystemWorkerId == null){
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Son güncelleme yapan sistem çalışanı boş bırakılamaz."));
		}
		Optional<Schedule> schedule = scheduleDao.findById(id);
		if (schedule.isEmpty()) {
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li program bulunamadı."));
		}
		Optional<SystemWorker> systemWorker = systemWorkerDao.findById(lastUpdateDateSystemWorkerId);
		if (systemWorker.isEmpty()) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(id + " id'li program için verdiğiiz sistem çalışanını kontrol ediniz."));
		}
		schedule.get().setLastUpdateDateSystemWorker(systemWorker.get());
		ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
		return new DataResult<ScheduleDto>(scheduleDto, id + " id'li programın üstünde son değişilik yapan sistem çalışanı güncellendi.");
	}

	@Override
	public DataResult<ScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) throws BusinessException {
		if (dayOfWeekId == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Gün boş bırakılamaz."));
		}
		Optional<Schedule> schedule = scheduleDao.findById(id);
		Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(dayOfWeekId);

		if (schedule.isPresent() && dayOfWeek.isPresent()) {
			schedule.get().setDayOfWeek(dayOfWeek.get());
			ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
			return new DataResult<ScheduleDto>(scheduleDto, id + " id'li programın günü güncellendi.");
		} else {
			if (schedule.isEmpty()) {
				throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li program bulunamadı."));
			}
		}
		throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(id + " id'li program için verdiğiniz gün id'sini kontrol ediniz."));
	}

	@Override
	public DataResult<ScheduleDto> updateHourById(long id, Long hourId) throws BusinessException {
		if (hourId == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Saat boş bırakılamaz."));
		}

		Optional<Schedule> schedule = scheduleDao.findById(id);
		Optional<Hour> hour = hourDao.findById(hourId);

		if (schedule.isPresent() && hour.isPresent()) {
			schedule.get().setHour(hour.get());
			ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
			return new DataResult<ScheduleDto>(scheduleDto, id + " id'li programın saati güncellendi.");
		} else {
			if (schedule.isEmpty()) {
				throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li program bulunamadı."));
			}
		}
		throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(id + " id'li program için verdiğiniz saat id'sini kontrol ediniz."));
	}

	@Override
	public DataResult<ScheduleDto> updateDescriptionById(long id, String description) throws BusinessException {
		Optional<Schedule> schedule = scheduleDao.findById(id);
		if (schedule.isPresent()) {
			schedule.get().setDescription(description);
			ScheduleDto scheduleDto = scheduleMapper.toDto(scheduleDao.save(schedule.get()));
			return new DataResult<ScheduleDto>(scheduleDto, id + " id'li programın açıklaması güncellendi.");
		}
		throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(id + " id'li program bulunamadı."));
	}

	@Override
	public DataResult<List<ScheduleDto>> findAllByTeacherId(long teacherId) throws BusinessException {
		List<Schedule> schedules = scheduleDao.findAllByTeacherIdSorted(teacherId);
		if (schedules.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Program bulunamadı."));

		List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(schedules);
		return new DataResult<List<ScheduleDto>>(schedulesDto, "Programlar getirildi.");
	}

	@Override
	public DataResult<List<ScheduleDto>> findAllBySystemWorkerId(long systemWorkerId) throws BusinessException {
		List<Schedule> schedules = scheduleDao.findAllBySystemWorkerIdSorted(systemWorkerId);
		if (schedules.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Program bulunamadı."));

		List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(schedules);
		return new DataResult<List<ScheduleDto>>(schedulesDto, "Programlar getirildi.");
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(scheduleDao.count(), "Programların sayısı getirildi.");
	}
}

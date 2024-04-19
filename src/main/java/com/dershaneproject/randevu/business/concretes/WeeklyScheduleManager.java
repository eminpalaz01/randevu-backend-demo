package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.WeeklyScheduleSaveResponse;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.Student;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import com.dershaneproject.randevu.validations.abstracts.WeeklyScheduleValidationService;
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
public class WeeklyScheduleManager implements WeeklyScheduleService{

	@PersistenceContext
	private EntityManager entityManager;

	private final  WeeklyScheduleDao weeklyScheduleDao;
	private final  TeacherDao teacherDao;
	private final  DayOfWeekDao dayOfWeekDao;
	private final  HourDao hourDao;
	private final  StudentDao studentDao;
	private final  SystemWorkerDao systemWorkerDao;

	private final  WeeklyScheduleValidationService weeklyScheduleValidationService;

	private final WeeklyScheduleMapper weeklyScheduleMapper;

	@Override
	public DataResult<WeeklyScheduleSaveResponse> save(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) throws BusinessException {
		weeklyScheduleValidationService.isValidateResult(weeklyScheduleSaveRequest);
		if(weeklyScheduleSaveRequest.getStudentId() != null)
			weeklyScheduleValidationService.studentExistById(weeklyScheduleSaveRequest);

		WeeklySchedule weeklySchedule = weeklyScheduleDao.save(weeklyScheduleMapper.toEntity(weeklyScheduleSaveRequest));
		WeeklyScheduleSaveResponse weeklyScheduleSaveResponse = weeklyScheduleMapper.toSaveResponse(weeklySchedule);
		return new DataResult<WeeklyScheduleSaveResponse>(weeklyScheduleSaveResponse, "Program veritabanına eklendi.");
	}
	
	@Override // add student to system after check again
	public DataResult<List<WeeklyScheduleSaveResponse>> saveAll(List<WeeklyScheduleSaveRequest> weeklyScheduleSaveRequestList) throws BusinessException {
		// Firstly, scheduleSaveRequestList is validating one by one
        weeklyScheduleSaveRequestList.forEach(weeklyScheduleSaveRequest -> {
            weeklyScheduleValidationService.isValidateResult(weeklyScheduleSaveRequest);
            if (weeklyScheduleSaveRequest.getStudentId() != null)
                weeklyScheduleValidationService.studentExistById(weeklyScheduleSaveRequest);

        });

		// Weekly Schedules will create and add to list
		List<WeeklySchedule> weeklySchedules = new ArrayList<WeeklySchedule>();
		for (WeeklyScheduleSaveRequest weeklyScheduleSaveRequest : weeklyScheduleSaveRequestList) {
			weeklySchedules.add(weeklyScheduleMapper.toEntity(weeklyScheduleSaveRequest));
		}
		// Weekly Schedules created and added to list

		// Schedule saved. Id and dates return to list
		weeklySchedules = weeklyScheduleDao.saveAllAndFlush(weeklySchedules);
		entityManager.clear();
		weeklySchedules = weeklyScheduleDao.findAllByIdSorted(weeklySchedules.stream()
				.map(WeeklySchedule::getId)
				.collect(Collectors.toList()));
		List<WeeklyScheduleSaveResponse> weeklyScheduleSaveResponseList = weeklyScheduleMapper.toSaveResponseList(weeklySchedules);
		return new DataResult<List<WeeklyScheduleSaveResponse>>(weeklyScheduleSaveResponseList, "Programlar veritabanına eklendi.");
	}

	@Override
	public Result deleteById(long id) throws BusinessException {
		if (weeklyScheduleDao.existsById(id)) {
			weeklyScheduleDao.deleteById(id);
			return new Result(id + " id'li haftalık program silindi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	}

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAll() throws BusinessException {
		List<WeeklySchedule> weeklySchedules = weeklyScheduleDao.findAll();
		if (weeklySchedules.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Haftalık program bulunamadı."));

		List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(weeklySchedules);
		return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, "Haftalık programlar getirildi.");
	}

	@Override
	public DataResult<WeeklyScheduleDto> findById(long id) throws BusinessException {
		Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
		if (weeklySchedule.isPresent()) {
			WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
			return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, id + " id'li haftalık program getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateFullById(long id, Boolean full) throws BusinessException {
		Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
		if (weeklySchedule.isPresent()) {
			weeklySchedule.get().setFull(full);
			weeklyScheduleDao.save(weeklySchedule.get());
			WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
			return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, id + " id'li haftalık programın doluluğu güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateTeacherById(long id, Long teacherId) throws BusinessException {
		if(teacherId == null)
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Öğretmen boş olamaz."));

		if(!teacherDao.existsById(teacherId))
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(teacherId + " id'li öğretmen bulunamadı."));


		Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
		if (weeklySchedule.isPresent()) {
			weeklySchedule.get().getTeacher().setId(teacherId);
			weeklyScheduleDao.save(weeklySchedule.get());
			WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
			return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, id + " id'li haftalık programın hangi öğretmene ait olduğu güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateStudentById(long id, Long studentId) throws BusinessException {
		if(studentId != null)
			if(!studentDao.existsById(studentId))
				throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(studentId + " id li öğrenci bulunamadı."));

		Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
		if (weeklySchedule.isPresent()) {
			if(weeklySchedule.get().getStudent() == null)
				weeklySchedule.get().setStudent(new Student());

			if(studentId == null)
				weeklySchedule.get().setStudent(null);
			else
				weeklySchedule.get().getStudent().setId(studentId);

			weeklyScheduleDao.save(weeklySchedule.get());
			WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
			return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, id + " id'li haftalık programın hangi"
					+ " öğrenciye ait olduğu güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId) throws BusinessException {
		if (lastUpdateDateSystemWorkerId == null)
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Sistem çalışanı boş bırakılamaz."));

		if(!systemWorkerDao.existsById(lastUpdateDateSystemWorkerId) )
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(lastUpdateDateSystemWorkerId + " id li sistem çalışanı bulunamadı."));

		Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
		if (weeklySchedule.isPresent()) {
			weeklySchedule.get().getLastUpdateDateSystemWorker().setId(lastUpdateDateSystemWorkerId);
			weeklyScheduleDao.save(weeklySchedule.get());
			WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
			return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, id + " id'li haftalık programında en"
					+ " son değişiklik yapan sistem çalışanı güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) throws BusinessException {
		if (dayOfWeekId == null)
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Gün boş bırakılamaz."));

		Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
		if (weeklySchedule.isPresent()) {
			if(!dayOfWeekDao.existsById(dayOfWeekId))
				throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Verdiğiniz gün id'sini kontrol ediniz."));

			Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(dayOfWeekId);
			if(dayOfWeek.isEmpty())
				throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Verdiğiniz gün bulunamadi id'sini kontrol ediniz."));

			weeklySchedule.get().setDayOfWeek(dayOfWeek.get());
			weeklyScheduleDao.save(weeklySchedule.get());
			WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
			return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, id + " id'li haftalık programın "
					+ "günü güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateHourById(long id, Long hourId) throws BusinessException {
		if (hourId == null)
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Saat boş bırakılamaz."));

		Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
		if (weeklySchedule.isPresent()) {
			if(!hourDao.existsById(hourId))
				throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Verdiğiniz saat id'sini kontrol ediniz."));

			Optional<Hour> hour = hourDao.findById(hourId);
			if(hour.isEmpty())
				throw new BusinessException(HttpStatus.BAD_REQUEST, List.of("Verdiğiniz saat bulunamadi id'sini kontrol ediniz."));

			weeklySchedule.get().setHour(hour.get());
			weeklyScheduleDao.save(weeklySchedule.get());
			WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
			return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, id + " id'li haftalık programın "
					+ "saati güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	}
	
	@Override
	public DataResult<WeeklyScheduleDto> updateDescriptionById(long id, String description) throws BusinessException {
		Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
		if (weeklySchedule.isPresent()) {
			weeklySchedule.get().setDescription(description);
			weeklyScheduleDao.save(weeklySchedule.get());
			WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
			return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, id + " id'li haftalık programın"
					+ " açıklaması güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li haftalık program bulunamadı."));
	}

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAllByTeacherId(long teacherId) throws BusinessException {
		List<WeeklySchedule> weeklySchedules = weeklyScheduleDao.findAllByTeacherIdSorted(teacherId);
		if (weeklySchedules.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Haftalık program bulunamadı."));

		List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(weeklySchedules);
		return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, "Haftalık programlar getirildi.");
	}

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAllBySystemWorkerId(long systemWorkerId) throws BusinessException {
		List<WeeklySchedule> weeklySchedules = weeklyScheduleDao.findAllBySystemWorkerIdSorted(systemWorkerId);
		if (weeklySchedules.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Haftalık program bulunamadı."));

		List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(weeklySchedules);
		return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, "Haftalık programlar getirildi.");
	}

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAllByStudentId(long studentId) throws BusinessException {
		List<WeeklySchedule> weeklySchedules = weeklyScheduleDao.findAllByStudentIdSorted(studentId);
		if (weeklySchedules.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Haftalık program bulunamadı."));

		List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(weeklySchedules);
		return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, "Haftalık programlar getirildi.");
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(weeklyScheduleDao.count(),  "Haftalık programların sayısı getirildi.");
	}
}

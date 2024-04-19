package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.business.abstracts.TeacherService;
import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequestForTeacher;
import com.dershaneproject.randevu.dto.requests.TeacherSaveRequest;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.ScheduleSaveResponse;
import com.dershaneproject.randevu.dto.responses.TeacherSaveResponse;
import com.dershaneproject.randevu.dto.responses.WeeklyScheduleSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Department;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.mappers.ScheduleMapper;
import com.dershaneproject.randevu.mappers.TeacherMapper;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeacherManager implements TeacherService {

	private final  TeacherDao teacherDao;
	private final  DepartmentDao departmentDao;
	private final  HourDao hourDao;
	private final  DayOfWeekDao dayOfWeekDao;
	private final  SystemWorkerDao systemWorkerDao;

	private final  ScheduleValidationService scheduleValidationService;

	private final  ScheduleService scheduleService;
	private final  WeeklyScheduleService weeklyScheduleService;

	private final TeacherMapper teacherMapper;
	private final WeeklyScheduleMapper weeklyScheduleMapper;
	private final ScheduleMapper scheduleMapper;


	@Transactional
	@Override
	public DataResult<TeacherSaveResponse> save(TeacherSaveRequest teacherSaveRequest) throws BusinessException {
		if (!departmentDao.existsById(teacherSaveRequest.getDepartmentId()))
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of( "öğretmen kaydı başarısız departman id'sini kontrol ediniz."));

		if (teacherSaveRequest.getSchedules() == null)
			teacherSaveRequest.setSchedules(new ArrayList<ScheduleSaveRequestForTeacher>());

		Teacher teacher = teacherDao.saveAndFlush(teacherMapper.toEntity(teacherSaveRequest));
		List<ScheduleSaveResponse> scheduleSaveResponseList = null;
		List<WeeklyScheduleSaveResponse> weeklyScheduleSaveResponseList = null;
		try {
			// scheduleSaveRequestsForTeacher are updating for register
			DataResult<List<ScheduleSaveRequest>> resultUpdateSchedulesDto = updateScheduleSaveRequestListForTeacher(teacherSaveRequest, teacher.getId());

			// scheduleSaveRequestsForTeacher are validating
			Result resultValidationSchedulesDto = scheduleValidationService.areValidateResult(resultUpdateSchedulesDto.getData());

			// scheduleSaveRequestsForTeacher are saving and updating
			DataResult<List<ScheduleSaveResponse>> resultScheduleSaveResponseList = scheduleService.saveAll(resultUpdateSchedulesDto.getData());
			scheduleSaveResponseList = resultScheduleSaveResponseList.getData();

			// scheduleSaveRequestsForTeacher mapping to WeeklyScheduleSaveRequestList
			List<WeeklyScheduleSaveRequest> weeklyScheduleSaveRequestList = new ArrayList<>();
			scheduleSaveResponseList.forEach(scheduleSaveResponse -> {
				weeklyScheduleSaveRequestList.add(createWeeklyScheduleSaveRequest(scheduleSaveResponse));
			});

			// weeklyScheduleSaveRequestList are saving and updating
			DataResult<List<WeeklyScheduleSaveResponse>> resultResponseWeeklySchedulesDto = weeklyScheduleService.saveAll(weeklyScheduleSaveRequestList);
			weeklyScheduleSaveResponseList = resultResponseWeeklySchedulesDto.getData();
		} catch (BusinessException exception){
            List<String> errorMessages = Arrays.asList(exception.getMessage());
			errorMessages.add("Öğretmen veritabanına eklenirken hata oluştu.");
			throw new BusinessException(exception.getHttpStatus(), errorMessages);
		}

		//Teacher save response ready
		TeacherSaveResponse teacherSaveResponse = teacherMapper.toSaveResponse(teacher);
		teacherSaveResponse.setSchedules(scheduleSaveResponseList);
		teacherSaveResponse.setWeeklySchedules(weeklyScheduleSaveResponseList);

		teacherSaveResponse.setId(teacher.getId());
		teacherSaveResponse.setCreateDate(teacher.getCreateDate());
		teacherSaveResponse.setLastUpdateDate(teacher.getLastUpdateDate());
		return new DataResult<TeacherSaveResponse>(teacherSaveResponse, "Öğretmen veritabanına eklendi.");
	}

	private static WeeklyScheduleSaveRequest createWeeklyScheduleSaveRequest(ScheduleSaveResponse scheduleSaveResponse) {
		WeeklyScheduleSaveRequest weeklyScheduleSaveRequest = new WeeklyScheduleSaveRequest();
		weeklyScheduleSaveRequest.setTeacherId(scheduleSaveResponse.getTeacherId());
		weeklyScheduleSaveRequest.setLastUpdateDateSystemWorkerId(scheduleSaveResponse.getLastUpdateDateSystemWorker().getId());
		weeklyScheduleSaveRequest.setDayOfWeekId(scheduleSaveResponse.getDayOfWeek().getId());
		weeklyScheduleSaveRequest.setHourId(scheduleSaveResponse.getHour().getId());
		weeklyScheduleSaveRequest.setFull(scheduleSaveResponse.getFull());
		weeklyScheduleSaveRequest.setDescription(scheduleSaveResponse.getDescription());
		return weeklyScheduleSaveRequest;
	}

	@Override
	public Result deleteById(long id) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);
		if (teacher.isPresent()) {
			teacherDao.deleteById(id);
			return new Result(id + " id'li öğretmen silindi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	@Override
	public DataResult<TeacherDto> findById(long id) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);
		if (teacher.isPresent()) {
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());

			return new DataResult<TeacherDto>(teacherDto, id + " id'li öğretmen getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	@Override
	public DataResult<TeacherDto> findByIdWithSchedules(long id) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);

		if (teacher.isPresent()) {
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());
			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(teacher.get().getSchedules());

			// SchedulesDto are sorting here
			schedulesDto.sort((o1, o2) -> {
				Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());

				if (dayOfWeekCompare == 0) {
					Long s1HourId = o1.getHour().getId();
					return s1HourId.compareTo(o2.getHour().getId());
				}
				return dayOfWeekCompare;
			});
			teacherDto.setSchedules(schedulesDto);

			return new DataResult<TeacherDto>(teacherDto, id + " id'li öğretmen getirildi.");
		}
	throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	@Override
	public DataResult<TeacherDto> findByIdWithAllSchedules(long id) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);

		if (teacher.isPresent()) {
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());
			List<ScheduleDto> schedulesDto = scheduleMapper.toDtoList(teacher.get().getSchedules());
			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(teacher.get().getWeeklySchedules());

			schedulesDto.sort((o1, o2) -> {
				Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());

				if (dayOfWeekCompare == 0) {
					Long s1HourId = o1.getHour().getId();
					return s1HourId.compareTo(o2.getHour().getId());
				}
				return dayOfWeekCompare;
			});
			weeklySchedulesDto.sort((o1, o2) -> {
				Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());

				if (dayOfWeekCompare == 0) {
					Long s1HourId = o1.getHour().getId();
					return s1HourId.compareTo(o2.getHour().getId());
				}
				return dayOfWeekCompare;
			});
			teacherDto.setSchedules(schedulesDto);
			teacherDto.setWeeklySchedules(weeklySchedulesDto);

			return new DataResult<TeacherDto>(teacherDto, id + " id'li öğretmen getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	@Override
	public DataResult<TeacherDto> findByIdWithWeeklySchedules(long id) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);

		if (teacher.isPresent()) {
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());
			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(teacher.get().getWeeklySchedules());

			weeklySchedulesDto.sort((o1, o2) -> {
				Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());

				if (dayOfWeekCompare == 0) {
					Long s1HourId = o1.getHour().getId();
					return s1HourId.compareTo(o2.getHour().getId());
				}
				return dayOfWeekCompare;
			});
			teacherDto.setWeeklySchedules(weeklySchedulesDto);

			return new DataResult<TeacherDto>(teacherDto, id + " id'li öğretmen getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	// schedules değer verilirse eğer öğretmen başına 105 satır değer olucak buda
	// performans sorunu yaratır.
	// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
	@Override
	public DataResult<List<TeacherDto>> getByDepartmentId(long departmentId) throws BusinessException {
		List<Teacher> teachers = teacherDao.getByDepartmentId(departmentId);
		if (teachers.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of(departmentId + " departman id'li bir öğretmen bulunamadı."));

		List<TeacherDto> teachersDto = teacherMapper.toDtoList(teachers);
		return new DataResult<List<TeacherDto>>(teachersDto,  departmentId + " departman id'li öğretmenler getirildi.");
	}

	// schedules deger verilirse eğer öğretmen başına 105 satır değer olucak buda
	// performans sorunu yaratır.
	// Bu yüzden ögretmeni bireysel olarak çektigimizde gönderdim sadece
	@Override
	public DataResult<List<TeacherDto>> findAll() throws BusinessException {
		List<Teacher> teachers = teacherDao.findAll();
		if (teachers.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Öğretmen bulunamadı."));

		List<TeacherDto> teachersDto = teacherMapper.toDtoList(teachers);
		return new DataResult<List<TeacherDto>>(teachersDto, "Öğretmenler getirildi.");
	}

	@Override
	public DataResult<TeacherDto> updateEmailById(long id, String email) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);
		if (teacher.isPresent()) {
			teacher.get().setEmail(email);
			teacherDao.save(teacher.get());
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());
			return new DataResult<TeacherDto>(teacherDto, id + " id'li öğretmenin maili güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	@Override
	public DataResult<TeacherDto> updateUserNameById(long id, String userName) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);
		if (teacher.isPresent()) {
			teacher.get().setUserName(userName);
			teacherDao.save(teacher.get());
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());
			return new DataResult<TeacherDto>(teacherDto,
					id + " id'li öğretmenin kullanıcı adı güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	@Override
	public DataResult<TeacherDto> updatePasswordById(long id, String password) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);
		if (teacher.isPresent()) {
			teacher.get().setPassword(password);
			teacherDao.save(teacher.get());
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());
			return new DataResult<TeacherDto>(teacherDto, id + " id'li öğretmenin şifresi güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	@Override
	public DataResult<TeacherDto> updateTeacherNumberById(long id, String teacherNumber) throws BusinessException {
		Optional<Teacher> teacher = teacherDao.findById(id);
		if (teacher.isPresent()) {
			teacher.get().setTeacherNumber(teacherNumber);
			teacherDao.save(teacher.get());
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());
			return new DataResult<TeacherDto>(teacherDto, id + " id'li öğretmenin numarası güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	@Override
	public DataResult<TeacherDto> updateDepartmentById(long id, Long departmentId) throws BusinessException {
		if (departmentId == null) {
			return new DataResult<TeacherDto>("Departman boş bırakılamaz.");
		}

		Optional<Teacher> teacher = teacherDao.findById(id);
		Optional<Department> department = departmentDao.findById(departmentId);

		if (teacher.isPresent() && department.isPresent()) {
			teacher.get().setDepartment(department.get());
			teacherDao.save(teacher.get());
			TeacherDto teacherDto = teacherMapper.toDto(teacher.get());
			return new DataResult<TeacherDto>(teacherDto, id + " id'li öğretmenin departmanı güncellendi.");
		} else {
			if (department.isEmpty()) {
				throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(id + " id'li öğretmen için verdiğiniz departman id'sini kontrol ediniz."));
			}
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğretmen bulunamadı."));
	}

	public DataResult<List<ScheduleSaveRequest>> updateScheduleSaveRequestListForTeacher(TeacherSaveRequest teacherSaveRequest, Long teacherId) throws BusinessException {
		// Eklerken hiç schedules verilmeme olasılığı olduğu için önce onu kontrol ediyorum ve
		// ScheduleValidationService de sadece create edilirken kullanılması için system çalışanı olmayan halini
		// yazdım bu arada. Eğerki eklenmişse Schedule bununda sistem çalışanı kontrol ediliyor var mı yok mu diye.
		// ve de teacher oluşturulurken tüm programların sistem çalışanı aynı olmalı ondan buraya özel kontrol yazdım.
		if (!systemWorkerDao.existsById(teacherSaveRequest.getLastUpdateDateSystemWorkerId())) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of( "Eklediğiniz programlardaki sistem çalışanı bulunamadı kontrol ediniz."));
		}

		// Dao dan toplam sayılarını getiriyor.
		long dayOfWeekCount = dayOfWeekDao.count();
		long hourCount = hourDao.count();

		List<ScheduleSaveRequest> willSaveSchedules = new ArrayList<>();

		for (int i = 0; i < dayOfWeekCount; i++) {

			for (int k = 0; k < hourCount; k++) {
				final long dayId = i + 1;
				final long hourId = k + 1;

				List<ScheduleSaveRequestForTeacher> scheduleSaveRequestForTeacherList = teacherSaveRequest.getSchedules().stream().filter(
						schedule -> schedule.getDayOfWeekId() == dayId && schedule.getHourId() == hourId)
						.toList();

				if (scheduleSaveRequestForTeacherList.size() > 1) {
					throw new BusinessException(HttpStatus.BAD_REQUEST, List.of( "Eklediğiniz programlarda gün ve saati aynı olan programlar var kontrol ediniz."));
				}
				if (scheduleSaveRequestForTeacherList.size() == 1) {
					ScheduleSaveRequest scheduleSaveRequest = scheduleMapper.toSaveRequest(scheduleSaveRequestForTeacherList.getFirst());
					scheduleSaveRequest.setTeacherId(teacherId);
					scheduleSaveRequest.setLastUpdateDateSystemWorkerId(teacherSaveRequest.getLastUpdateDateSystemWorkerId());

					willSaveSchedules.add(scheduleSaveRequest);
				} else {
					ScheduleSaveRequest scheduleSaveRequest = Schedule.createDefaultScheduleForSave(teacherSaveRequest.getLastUpdateDateSystemWorkerId(), teacherId, dayId, hourId);
					willSaveSchedules.add(scheduleSaveRequest);
				}
			}
		}
		return new DataResult<List<ScheduleSaveRequest>>(willSaveSchedules, "Öğretmenin programları ayarlandı.");
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(teacherDao.count(), "Öğretmenlerin sayısı getirildi.");
	}
}

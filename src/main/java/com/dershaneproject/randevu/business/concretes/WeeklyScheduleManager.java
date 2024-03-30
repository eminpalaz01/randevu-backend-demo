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
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import com.dershaneproject.randevu.validations.abstracts.WeeklyScheduleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyScheduleManager implements WeeklyScheduleService{

	private final  WeeklyScheduleDao weeklyScheduleDao;
	private final  TeacherDao teacherDao;
	private final  DayOfWeekDao dayOfWeekDao;
	private final  HourDao hourDao;
	private final  StudentDao studentDao;
	private final  SystemWorkerDao systemWorkerDao;

	private final  WeeklyScheduleValidationService weeklyScheduleValidationService;

	private final WeeklyScheduleMapper weeklyScheduleMapper;

	@Override
	public DataResult<WeeklyScheduleSaveResponse> save(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) {
		try {
			Result validateResult = weeklyScheduleValidationService.isValidateResult(weeklyScheduleSaveRequest);
			if (validateResult.isSuccess()) {
				if(weeklyScheduleSaveRequest.getStudentId() != null) {
					Result studentExistResult = weeklyScheduleValidationService.studentExistById(weeklyScheduleSaveRequest);
					if (!studentExistResult.isSuccess()) {
						return new DataResult<>(false, studentExistResult.getMessage());
					}
				}
				WeeklySchedule weeklySchedule = weeklyScheduleDao.save(weeklyScheduleMapper.toEntity(weeklyScheduleSaveRequest));
				WeeklyScheduleSaveResponse weeklyScheduleSaveResponse = weeklyScheduleMapper.toSaveResponse(weeklySchedule);
				return new DataResult<WeeklyScheduleSaveResponse>(weeklyScheduleSaveResponse, true, "Program veritabanına eklendi.");
			} else {
				return new DataResult<>(false, validateResult.getMessage());
			}
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}
	
	@Override // add student to system after check again
	public DataResult<List<WeeklyScheduleSaveResponse>> saveAll(List<WeeklyScheduleSaveRequest> weeklyScheduleSaveRequestList) {
		// Firstly, scheduleSaveRequestList is validating one by one
		for (WeeklyScheduleSaveRequest weeklyScheduleSaveRequest : weeklyScheduleSaveRequestList) {
			Result resultValidation = weeklyScheduleValidationService.isValidateResult(weeklyScheduleSaveRequest);
			if(!(resultValidation.isSuccess())) {
				return new DataResult<>(false, resultValidation.getMessage());
			} else {
				if(weeklyScheduleSaveRequest.getStudentId() != null) {
					Result studentExistResult = weeklyScheduleValidationService.studentExistById(weeklyScheduleSaveRequest);
					if (!studentExistResult.isSuccess()) {
						return new DataResult<>(false, studentExistResult.getMessage());
					}
				}
			}
		}
		List<WeeklySchedule> weeklySchedules = new ArrayList<WeeklySchedule>();
		try {
			// Weekly Schedules will create and add to list
			for (WeeklyScheduleSaveRequest weeklyScheduleSaveRequest : weeklyScheduleSaveRequestList) {
				weeklySchedules.add(weeklyScheduleMapper.toEntity(weeklyScheduleSaveRequest));
			}
			// Weekly Schedules created and added to list

			// Schedule saved. Id and dates return to list
			weeklySchedules = weeklyScheduleDao.saveAll(weeklySchedules);
			weeklySchedules = weeklyScheduleDao.findAllByIdSorted(weeklySchedules.stream()
					.map(WeeklySchedule::getId)
					.collect(Collectors.toList()));
			List<WeeklyScheduleSaveResponse> weeklyScheduleSaveResponseList = weeklyScheduleMapper.toSaveResponseList(weeklySchedules);
			return new DataResult<List<WeeklyScheduleSaveResponse>>(weeklyScheduleSaveResponseList, true, "Programlar veritabanına eklendi.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public Result deleteById(long id) {
		try {
			boolean weeklyScheduleIsFull = weeklyScheduleDao.existsById(id);
			if (weeklyScheduleIsFull) {
				weeklyScheduleDao.deleteById(id);
				return new Result(true, id + " id'li haftalık program silindi.");
			}
			return new Result(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAll() {
		try {
			List<WeeklySchedule> weeklySchedules = weeklyScheduleDao.findAll();
			if (!weeklySchedules.isEmpty()) {
				List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(weeklySchedules);
				return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, true, "Haftalık programlar getirildi.");
			} else {
				return new DataResult<>(false, "Haftalık program bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> findById(long id) {
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (weeklySchedule.isPresent()) {
				WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık program getirildi.");
			}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateFullById(long id, Boolean full) {
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (weeklySchedule.isPresent()) {
				weeklySchedule.get().setFull(full);
				weeklyScheduleDao.save(weeklySchedule.get());
				WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık programın doluluğu güncellendi.");
			}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateTeacherById(long id, Long teacherId) {
		if(teacherId != null){
			if(!teacherDao.existsById(teacherId)){
				return new DataResult<>(false, teacherId
						+ " id li öğretmen bulunamadı.");
			}
		}
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (weeklySchedule.isPresent()) {
				 if(teacherId == null){
					 return new DataResult<>(false, "Öğretmen boş olamaz.");
				 }else{
					 weeklySchedule.get().getTeacher().setId(teacherId);
				 }
				weeklyScheduleDao.save(weeklySchedule.get());
				WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık programın hangi "
						+ "öğretmene ait olduğu güncellendi.");

		}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateStudentById(long id, Long studentId) {
		if(studentId != null){
			if(!studentDao.existsById(studentId)){
				return new DataResult<>(false, studentId
						+ " id li öğrenci bulunamadı.");
			}
		}
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (weeklySchedule.isPresent()) {
				if(weeklySchedule.get().getStudent() == null){
					weeklySchedule.get().setStudent(new Student());
				}
				if(studentId == null){
					weeklySchedule.get().setStudent(null);
				}else{
					weeklySchedule.get().getStudent().setId(studentId);
				}
				weeklyScheduleDao.save(weeklySchedule.get());
				WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık programın hangi"
						+ " öğrenciye ait olduğu güncellendi.");
			}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId) {
		if (lastUpdateDateSystemWorkerId == null) {
			return new DataResult<>(false, "Sistem çalışanı boş bırakılamaz.");
		}
		if(!systemWorkerDao.existsById(lastUpdateDateSystemWorkerId) ){
			return new DataResult<>(false, lastUpdateDateSystemWorkerId
					+ " id li sistem çalışanı bulunamadı.");
		}
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (weeklySchedule.isPresent()) {
				weeklySchedule.get().getLastUpdateDateSystemWorker().setId(lastUpdateDateSystemWorkerId);
				weeklyScheduleDao.save(weeklySchedule.get());
				WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık programında en"
						+ " son değişiklik yapan sistem çalışanı güncellendi.");
			}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) {
		if (dayOfWeekId == null) {
			return new DataResult<>(false, "Gün boş bırakılamaz.");
		}
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (weeklySchedule.isPresent()) {
				if(!dayOfWeekDao.existsById(dayOfWeekId)){
					return new DataResult<>(false, "Verdiğiniz gün id'sini kontrol ediniz.");
				}
				Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(dayOfWeekId);
				if(dayOfWeek.isEmpty()){
					return new DataResult<>(false, "Verdiğiniz gün bulunamadi id'sini kontrol ediniz.");
				}
				weeklySchedule.get().setDayOfWeek(dayOfWeek.get());
				weeklyScheduleDao.save(weeklySchedule.get());
				WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık programın "
						+ "günü güncellendi.");
			}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateHourById(long id, Long hourId) {
		if (hourId == null) {
			return new DataResult<>(false,"Saat boş bırakılamaz.");
		}
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (weeklySchedule.isPresent()) {
				if(!hourDao.existsById(hourId)){
					return new DataResult<>(false, "Verdiğiniz saat id'sini kontrol ediniz.");
				}
				Optional<Hour> hour = hourDao.findById(hourId);
				if(hour.isEmpty()){
					return new DataResult<>(false, "Verdiğiniz saat bulunamadi id'sini kontrol ediniz.");
				}
				weeklySchedule.get().setHour(hour.get());
				weeklyScheduleDao.save(weeklySchedule.get());
				WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık programın "
						+ "saati güncellendi.");
			}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<WeeklyScheduleDto> updateDescriptionById(long id, String description) {
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (weeklySchedule.isPresent()) {
				weeklySchedule.get().setDescription(description);
				weeklyScheduleDao.save(weeklySchedule.get());
				WeeklyScheduleDto weeklyScheduleDto = weeklyScheduleMapper.toDto(weeklySchedule.get());
				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık programın"
						+ " açıklaması güncellendi.");
			}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {	
		return new DataResult<Long>(weeklyScheduleDao.count(), true,
				"Haftalık programların sayısı getirildi."); 
    }

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAllByTeacherId(long teacherId) {
		try {
			List<WeeklySchedule> weeklySchedules = weeklyScheduleDao.findAllByTeacherIdSorted(teacherId);
			if (!weeklySchedules.isEmpty()) {
				List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(weeklySchedules);
				return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, true, "Haftalık programlar getirildi.");
			} else {
				return new DataResult<>(false, "Haftalık program bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAllByStudentId(long studentId) {
		try {
			List<WeeklySchedule> weeklySchedules = weeklyScheduleDao.findAllByStudentIdSorted(studentId);
			if (!weeklySchedules.isEmpty()) {
				List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(weeklySchedules);
				return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, true, "Haftalık programlar getirildi.");
			} else {
				return new DataResult<>(false, "Haftalık program bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}
}

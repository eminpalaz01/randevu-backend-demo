package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.*;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.WeeklyScheduleSaveResponse;
import com.dershaneproject.randevu.entities.concretes.*;
import com.dershaneproject.randevu.validations.abstracts.WeeklyScheduleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyScheduleManager implements WeeklyScheduleService{

	private final  ModelMapperServiceWithTypeMappingConfigs modelMapperService;
	private final  WeeklyScheduleDao weeklyScheduleDao;
	private final  TeacherDao teacherDao;
	private final  DayOfWeekDao dayOfWeekDao;
	private final  HourDao hourDao;
	private final  StudentDao studentDao;
	private final  SystemWorkerDao systemWorkerDao;
	private final  WeeklyScheduleValidationService weeklyScheduleValidationService;

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
				WeeklySchedule weeklySchedule = weeklyScheduleDao.save(createWeeklyScheduleForSave(weeklyScheduleSaveRequest));
				WeeklyScheduleSaveResponse weeklyScheduleSaveResponse = modelMapperService.forResponse().map(
						weeklySchedule, WeeklyScheduleSaveResponse.class);
				// convert edemezse acilacak
//				scheduleSaveResponse.setTeacher(schedule.getTeacher());
//				scheduleSaveResponse.setLastUpdateDateSystemWorker(schedule.getLastUpdateDateSystemWorker());
//				scheduleSaveResponse.setDayOfWeek(schedule.getDayOfWeek());
//				scheduleSaveResponse.setHour(schedule.getHour());

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
				weeklySchedules.add(createWeeklyScheduleForSave(weeklyScheduleSaveRequest));
			}
			// Weekly Schedules created and added to list

			// Schedule saved and id and dates return to list
			weeklySchedules = weeklyScheduleDao.saveAll(weeklySchedules);
			weeklySchedules = weeklyScheduleDao.findAllByIdSorted(weeklySchedules.stream()
					.map(WeeklySchedule::getId)
					.collect(Collectors.toList()));

			List<WeeklyScheduleSaveResponse> weeklyScheduleSaveResponseList = new LinkedList<WeeklyScheduleSaveResponse>();
			// Schedule's id and dates are set for scheduleSaveRequestList
			for (WeeklySchedule weeklySchedule : weeklySchedules) {
				WeeklyScheduleSaveResponse weeklyScheduleSaveResponse = modelMapperService.forResponse().map(
						weeklySchedule, WeeklyScheduleSaveResponse.class);
				weeklyScheduleSaveResponseList.add(weeklyScheduleSaveResponse);
			}

			// Weekly Schedules are sending here
			return new DataResult<List<WeeklyScheduleSaveResponse>>(weeklyScheduleSaveResponseList, true, "Programlar veritabanına eklendi.");

		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	private WeeklySchedule createWeeklyScheduleForSave(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) {
		DayOfWeek dayOfWeek = new DayOfWeek();
		dayOfWeek.setId(weeklyScheduleSaveRequest.getDayOfWeekId());

		Hour hour = new Hour();
		hour.setId(weeklyScheduleSaveRequest.getHourId());

		SystemWorker systemWorker = new SystemWorker();
		systemWorker.setId(weeklyScheduleSaveRequest.getLastUpdateDateSystemWorkerId());

		Teacher teacher = new Teacher();
		teacher.setId(weeklyScheduleSaveRequest.getTeacherId());

		Student student = new Student();
		student.setId(weeklyScheduleSaveRequest.getStudentId());

		WeeklySchedule weeklySchedule = new WeeklySchedule();
		weeklySchedule.setFull(weeklyScheduleSaveRequest.getFull());
		// if description is null set default description
		if(weeklyScheduleSaveRequest.getDescription() != null && !weeklyScheduleSaveRequest.getDescription().isEmpty()) {
			weeklySchedule.setDescription(weeklyScheduleSaveRequest.getDescription());
		}
		weeklySchedule.setTeacher(teacher);
		if(student.getId() != null) {
			weeklySchedule.setStudent(student);
		}
		weeklySchedule.setLastUpdateDateSystemWorker(systemWorker);
		weeklySchedule.setDayOfWeek(dayOfWeek);
		weeklySchedule.setHour(hour);
		return weeklySchedule;
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
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<WeeklyScheduleDto>();

				weeklySchedules.forEach(weeklySchedule -> {
					Student student = weeklySchedule.getStudent();
					SystemWorker lastUpdateDateSystemWorker = weeklySchedule.getLastUpdateDateSystemWorker();
                    
                    HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class);
                    DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class);
                    
					WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
					weeklyScheduleDto.setId(weeklySchedule.getId());
					weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
					weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
					weeklyScheduleDto.setHour(hourDto);
					weeklyScheduleDto.setFull(weeklySchedule.getFull());
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

					if (student == null) {
						weeklyScheduleDto.setStudentId(null);

					} else {
						weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());

					}

					if (lastUpdateDateSystemWorker == null) {
						weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

					} else {
						// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
						SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
						systemWorkerDto.setId(weeklySchedule.getLastUpdateDateSystemWorker().getId());
						systemWorkerDto.setUserName(weeklySchedule.getLastUpdateDateSystemWorker().getUserName());
						systemWorkerDto.setEmail(weeklySchedule.getLastUpdateDateSystemWorker().getEmail());
						systemWorkerDto.setPassword(weeklySchedule.getLastUpdateDateSystemWorker().getPassword());
						systemWorkerDto.setCreateDate(weeklySchedule.getLastUpdateDateSystemWorker().getCreateDate());
						systemWorkerDto.setLastUpdateDate(weeklySchedule.getLastUpdateDateSystemWorker().getLastUpdateDate());
						systemWorkerDto.setAuthority(weeklySchedule.getLastUpdateDateSystemWorker().getAuthority());

						weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);
					}

					weeklySchedulesDto.add(weeklyScheduleDto);
				});

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

			if (!(weeklySchedule.equals(Optional.empty()))) {
				Student student = weeklySchedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = weeklySchedule.get().getLastUpdateDateSystemWorker();
				
                HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.get().getHour(), HourDto.class);
                DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.get().getDayOfWeek(), DayOfWeekDto.class);

				WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
				weeklyScheduleDto.setId(weeklySchedule.get().getId());
				weeklyScheduleDto.setTeacherId(weeklySchedule.get().getTeacher().getId());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
				weeklyScheduleDto.setFull(weeklySchedule.get().getFull());
				weeklyScheduleDto.setCreateDate(weeklySchedule.get().getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDate());
				weeklyScheduleDto.setDescription(weeklySchedule.get().getDescription());

				if (student == null) {
					weeklyScheduleDto.setStudentId(null);

				} else {
					weeklyScheduleDto.setStudentId(weeklySchedule.get().getStudent().getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.get().getLastUpdateDateSystemWorker().getAuthority());

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

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

			if (!(weeklySchedule.equals(Optional.empty()))) {
				weeklySchedule.get().setFull(full);
				weeklyScheduleDao.save(weeklySchedule.get());

				Student student = weeklySchedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = weeklySchedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.get().getDayOfWeek(), DayOfWeekDto.class);
				
				WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
				weeklyScheduleDto.setId(weeklySchedule.get().getId());
				weeklyScheduleDto.setTeacherId(weeklySchedule.get().getTeacher().getId());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
				weeklyScheduleDto.setFull(weeklySchedule.get().getFull());
				weeklyScheduleDto.setCreateDate(weeklySchedule.get().getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDate());
				weeklyScheduleDto.setDescription(weeklySchedule.get().getDescription());
				
				if (student == null) {
					weeklyScheduleDto.setStudentId(null);

				} else {
					weeklyScheduleDto.setStudentId(student.getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.get().getLastUpdateDateSystemWorker().getAuthority());

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

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

			if (!(weeklySchedule.equals(Optional.empty()))) {
				 if(teacherId == null){
					 return new DataResult<>(false, "Öğretmen boş olamaz.");
				 }else{
					 weeklySchedule.get().getTeacher().setId(teacherId);
				 }

				weeklyScheduleDao.save(weeklySchedule.get());

				Student student = weeklySchedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = weeklySchedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.get().getDayOfWeek(), DayOfWeekDto.class);
				
				WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
				weeklyScheduleDto.setId(weeklySchedule.get().getId());
				weeklyScheduleDto.setTeacherId(weeklySchedule.get().getTeacher().getId());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
				weeklyScheduleDto.setFull(weeklySchedule.get().getFull());
				weeklyScheduleDto.setCreateDate(weeklySchedule.get().getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDate());
				weeklyScheduleDto.setDescription(weeklySchedule.get().getDescription());
				
				if (student == null) {
					weeklyScheduleDto.setStudentId(null);

				} else {
					weeklyScheduleDto.setStudentId(weeklySchedule.get().getStudent().getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.get().getLastUpdateDateSystemWorker().getAuthority());

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

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

			if (!(weeklySchedule.equals(Optional.empty()))) {
				if(weeklySchedule.get().getStudent() == null){
					weeklySchedule.get().setStudent(new Student());
				}

				if(studentId == null){
					weeklySchedule.get().setStudent(null);
				}else{
					weeklySchedule.get().getStudent().setId(studentId);
				}

				weeklyScheduleDao.save(weeklySchedule.get());

				Student student = weeklySchedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = weeklySchedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.get().getDayOfWeek(), DayOfWeekDto.class);
				
				WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
				weeklyScheduleDto.setId(weeklySchedule.get().getId());
				weeklyScheduleDto.setTeacherId(weeklySchedule.get().getTeacher().getId());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
				weeklyScheduleDto.setFull(weeklySchedule.get().getFull());
				weeklyScheduleDto.setCreateDate(weeklySchedule.get().getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDate());
				weeklyScheduleDto.setDescription(weeklySchedule.get().getDescription());
				
				if (student == null) {
					weeklyScheduleDto.setStudentId(null);

				} else {
					weeklyScheduleDto.setStudentId(weeklySchedule.get().getStudent().getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.get().getLastUpdateDateSystemWorker().getAuthority());

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, id + " id'li haftalık programın hangi"
						+ " öğrenciye ait olduğu güncellendi.");
			}
			return new DataResult<>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateLastUpdateDateSystemWorkerById(long id,
																			  Long lastUpdateDateSystemWorkerId) {
		if (lastUpdateDateSystemWorkerId == null) {
			return new DataResult<>(false, "Sistem çalışanı boş bırakılamaz.");
		}
		if(!systemWorkerDao.existsById(lastUpdateDateSystemWorkerId) ){
			return new DataResult<>(false, lastUpdateDateSystemWorkerId
					+ " id li sistem çalışanı bulunamadı.");
		}
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);

			if (!(weeklySchedule.equals(Optional.empty()))) {
				weeklySchedule.get().getLastUpdateDateSystemWorker().setId(lastUpdateDateSystemWorkerId);
				weeklyScheduleDao.save(weeklySchedule.get());

				Student student = weeklySchedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = weeklySchedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.get().getDayOfWeek(), DayOfWeekDto.class);
				
				WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
				weeklyScheduleDto.setId(weeklySchedule.get().getId());
				weeklyScheduleDto.setTeacherId(weeklySchedule.get().getTeacher().getId());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
				weeklyScheduleDto.setFull(weeklySchedule.get().getFull());
				weeklyScheduleDto.setCreateDate(weeklySchedule.get().getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDate());
				weeklyScheduleDto.setDescription(weeklySchedule.get().getDescription());
				
				if (student == null) {
					weeklyScheduleDto.setStudentId(null);

				} else {
					weeklyScheduleDto.setStudentId(weeklySchedule.get().getStudent().getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.get().getLastUpdateDateSystemWorker().getAuthority());

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

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

			if (!(weeklySchedule.equals(Optional.empty()))) {
				if(!dayOfWeekDao.existsById(dayOfWeekId)){
					return new DataResult<>(false, "Verdiğiniz gün id'sini kontrol ediniz.");
				}

				DayOfWeek dayOfWeek = dayOfWeekDao.findById(dayOfWeekId).get();

				weeklySchedule.get().setDayOfWeek(dayOfWeek);
				weeklyScheduleDao.save(weeklySchedule.get());

				Student student = weeklySchedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = weeklySchedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.get().getDayOfWeek(), DayOfWeekDto.class);
				
				WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
				weeklyScheduleDto.setId(weeklySchedule.get().getId());
				weeklyScheduleDto.setTeacherId(weeklySchedule.get().getTeacher().getId());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
				weeklyScheduleDto.setFull(weeklySchedule.get().getFull());
				weeklyScheduleDto.setCreateDate(weeklySchedule.get().getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDate());
				weeklyScheduleDto.setDescription(weeklySchedule.get().getDescription());
				
				if (student == null) {
					weeklyScheduleDto.setStudentId(null);

				} else {
					weeklyScheduleDto.setStudentId(weeklySchedule.get().getStudent().getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.get().getLastUpdateDateSystemWorker().getAuthority());

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

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

			if (!(weeklySchedule.equals(Optional.empty()))) {
				if(!hourDao.existsById(hourId)){
					return new DataResult<>(false, "Verdiğiniz saat id'sini kontrol ediniz.");
				}

				Hour hour = hourDao.findById(hourId).get();

				weeklySchedule.get().setHour(hour);
				weeklyScheduleDao.save(weeklySchedule.get());

				Student student = weeklySchedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = weeklySchedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.get().getDayOfWeek(), DayOfWeekDto.class);
				
				WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
				weeklyScheduleDto.setId(weeklySchedule.get().getId());
				weeklyScheduleDto.setTeacherId(weeklySchedule.get().getTeacher().getId());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
				weeklyScheduleDto.setFull(weeklySchedule.get().getFull());
				weeklyScheduleDto.setCreateDate(weeklySchedule.get().getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDate());
				weeklyScheduleDto.setDescription(weeklySchedule.get().getDescription());
				
				if (student == null) {
					weeklyScheduleDto.setStudentId(null);

				} else {
					weeklyScheduleDto.setStudentId(weeklySchedule.get().getStudent().getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.get().getLastUpdateDateSystemWorker().getAuthority());

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

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

			if (!(weeklySchedule.equals(Optional.empty()))) {
				weeklySchedule.get().setDescription(description);
				weeklyScheduleDao.save(weeklySchedule.get());

				Student student = weeklySchedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = weeklySchedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.get().getDayOfWeek(), DayOfWeekDto.class);
				
				WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
				weeklyScheduleDto.setId(weeklySchedule.get().getId());
				weeklyScheduleDto.setTeacherId(weeklySchedule.get().getTeacher().getId());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
				weeklyScheduleDto.setFull(weeklySchedule.get().getFull());
				weeklyScheduleDto.setCreateDate(weeklySchedule.get().getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDate());
				weeklyScheduleDto.setDescription(weeklySchedule.get().getDescription());
				
				if (student == null) {
					weeklyScheduleDto.setStudentId(null);

				} else {
					weeklyScheduleDto.setStudentId(weeklySchedule.get().getStudent().getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.get().getLastUpdateDateSystemWorker().getAuthority());

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

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
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<WeeklyScheduleDto>();

				weeklySchedules.forEach(weeklySchedule -> {
					Student student = weeklySchedule.getStudent();
					SystemWorker lastUpdateDateSystemWorker = weeklySchedule.getLastUpdateDateSystemWorker();

					HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class);
					DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class);

					WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
					weeklyScheduleDto.setId(weeklySchedule.getId());
					weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
					weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
					weeklyScheduleDto.setHour(hourDto);
					weeklyScheduleDto.setFull(weeklySchedule.getFull());
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

					if (student == null) {
						weeklyScheduleDto.setStudentId(null);

					} else {
						weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());

					}

					if (lastUpdateDateSystemWorker == null) {
						weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

					} else {
						// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
						SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
						systemWorkerDto.setId(weeklySchedule.getLastUpdateDateSystemWorker().getId());
						systemWorkerDto.setUserName(weeklySchedule.getLastUpdateDateSystemWorker().getUserName());
						systemWorkerDto.setEmail(weeklySchedule.getLastUpdateDateSystemWorker().getEmail());
						systemWorkerDto.setPassword(weeklySchedule.getLastUpdateDateSystemWorker().getPassword());
						systemWorkerDto.setCreateDate(weeklySchedule.getLastUpdateDateSystemWorker().getCreateDate());
						systemWorkerDto.setLastUpdateDate(weeklySchedule.getLastUpdateDateSystemWorker().getLastUpdateDate());
						systemWorkerDto.setAuthority(weeklySchedule.getLastUpdateDateSystemWorker().getAuthority());

						weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);
					}

					weeklySchedulesDto.add(weeklyScheduleDto);
				});

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
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<WeeklyScheduleDto>();

				weeklySchedules.forEach(weeklySchedule -> {
					Student student = weeklySchedule.getStudent();
					SystemWorker lastUpdateDateSystemWorker = weeklySchedule.getLastUpdateDateSystemWorker();

					HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class);
					DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class);

					WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();
					weeklyScheduleDto.setId(weeklySchedule.getId());
					weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
					weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
					weeklyScheduleDto.setHour(hourDto);
					weeklyScheduleDto.setFull(weeklySchedule.getFull());
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

					if (student == null) {
						weeklyScheduleDto.setStudentId(null);

					} else {
						weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());

					}

					if (lastUpdateDateSystemWorker == null) {
						weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

					} else {
						// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
						SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
						systemWorkerDto.setId(weeklySchedule.getLastUpdateDateSystemWorker().getId());
						systemWorkerDto.setUserName(weeklySchedule.getLastUpdateDateSystemWorker().getUserName());
						systemWorkerDto.setEmail(weeklySchedule.getLastUpdateDateSystemWorker().getEmail());
						systemWorkerDto.setPassword(weeklySchedule.getLastUpdateDateSystemWorker().getPassword());
						systemWorkerDto.setCreateDate(weeklySchedule.getLastUpdateDateSystemWorker().getCreateDate());
						systemWorkerDto.setLastUpdateDate(weeklySchedule.getLastUpdateDateSystemWorker().getLastUpdateDate());
						systemWorkerDto.setAuthority(weeklySchedule.getLastUpdateDateSystemWorker().getAuthority());

						weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);
					}

					weeklySchedulesDto.add(weeklyScheduleDto);
				});

				return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, true, "Haftalık programlar getirildi.");

			} else {

				return new DataResult<>(false, "Haftalık program bulunamadı.");
			}

		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

}

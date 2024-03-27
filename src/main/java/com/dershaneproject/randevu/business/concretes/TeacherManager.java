package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.business.abstracts.TeacherService;
import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.*;
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
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherManager implements TeacherService {

	private final  TeacherDao teacherDao;
	private final  DepartmentDao departmentDao;
	private final  HourDao hourDao;
	private final  DayOfWeekDao dayOfWeekDao;
	private final  ScheduleService scheduleService;
	private final  ScheduleValidationService scheduleValidationService;
	private final  WeeklyScheduleService weeklyScheduleService;
	private final  SystemWorkerDao systemWorkerDao;
	private final  ModelMapperServiceWithTypeMappingConfigs modelMapperService;

	@Transactional
	@Override
	public DataResult<TeacherSaveResponse> save(TeacherSaveRequest teacherSaveRequest) {
		try {
			if (teacherSaveRequest.getSchedules() == null) {
				teacherSaveRequest.setSchedules(new ArrayList<ScheduleSaveRequestForTeacher>());
			}

			if (!departmentDao.existsById(teacherSaveRequest.getDepartmentId())) {
				return new DataResult<>(false,
						"Veritabanına öğretmen kaydı başarısız departman id'sini kontrol ediniz.");
			}

			Teacher teacher = teacherDao.saveAndFlush(createTeacherForSave(teacherSaveRequest));

            // scheduleSaveRequestsForTeacher are updating for register
            DataResult<List<ScheduleSaveRequest>> resultUpdateSchedulesDto = updateScheduleSaveRequestListForTeacher(teacherSaveRequest, teacher.getId());

            if (resultUpdateSchedulesDto.isSuccess()) {
                // scheduleSaveRequestsForTeacher are validating
                Result resultValidationSchedulesDto = scheduleValidationService
                        .areValidateResult(resultUpdateSchedulesDto.getData());

                if (resultValidationSchedulesDto.isSuccess()) {
                    // scheduleSaveRequestsForTeacher are saving and updating
                    DataResult<List<ScheduleSaveResponse>> resultScheduleSaveResponseList = scheduleService
                            .saveAll(resultUpdateSchedulesDto.getData());

                    if (resultScheduleSaveResponseList.isSuccess()) {
                        List<ScheduleSaveResponse> scheduleSaveResponseList = resultScheduleSaveResponseList.getData();
                        List<WeeklyScheduleSaveRequest> weeklyScheduleSaveRequestList = new ArrayList<>();

                        // scheduleSaveRequestsForTeacher mapping to WeeklyScheduleSaveRequestList
                        scheduleSaveResponseList.forEach(scheduleSaveResponse -> {
							WeeklyScheduleSaveRequest weeklyScheduleSaveRequest = modelMapperService.forResponse()
                                    .map(scheduleSaveResponse,  WeeklyScheduleSaveRequest.class);
                            weeklyScheduleSaveRequestList.add(weeklyScheduleSaveRequest);
                        });

                        // weeklyScheduleSaveRequestList are saving and updating
                        DataResult<List<WeeklyScheduleSaveResponse>> resultResponseWeeklySchedulesDto
                                = weeklyScheduleService.saveAll(weeklyScheduleSaveRequestList);
                         if(resultResponseWeeklySchedulesDto.isSuccess()){
							 TeacherSaveResponse teacherSaveResponse = modelMapperService.forResponse().map(teacherSaveRequest, TeacherSaveResponse.class);
							 teacherSaveResponse.setSchedules(scheduleSaveResponseList);
							 teacherSaveResponse.setWeeklySchedules(resultResponseWeeklySchedulesDto.getData());

							 teacherSaveResponse.setId(teacher.getId());
							 teacherSaveResponse.setCreateDate(teacher.getCreateDate());
							 teacherSaveResponse.setLastUpdateDate(teacher.getLastUpdateDate());
                             return new DataResult<TeacherSaveResponse>(teacherSaveResponse, true, "Öğretmen veritabanına eklendi.(Gelen response'da"
                                     + " schedule ların date lerinde 1 2 saniye yanılma payı vardır "
                                     + "sadece oluşturulurken date leri getirmediği için tekrar istek atmak yerine "
                                     + "database e kendim anlık tarihi koydum tüm schedule larda ama database de tamamen doğru"
                                     + " şekildedir bir dahaki isteklerde yanılma payı yoktur.) null olmasıda tercih edilebilirdi.");
                         }else{
                             teacherDao.deleteById(teacher.getId());
                             return new DataResult<>(false, resultResponseWeeklySchedulesDto.getMessage());
                         }

                    } else {
                        teacherDao.deleteById(teacher.getId());
                        return new DataResult<>(false, resultScheduleSaveResponseList.getMessage());
                    }

                } else {
                    teacherDao.deleteById(teacher.getId());
                    return new DataResult<>(false, resultValidationSchedulesDto.getMessage());
                }

            } else {
                teacherDao.deleteById(teacher.getId());
                return new DataResult<>(false, resultUpdateSchedulesDto.getMessage());
            }


        } catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	private Teacher createTeacherForSave(TeacherSaveRequest teacherSaveRequest) {
		Department department = new Department();
		department.setId(teacherSaveRequest.getDepartmentId());

		Teacher teacher = new Teacher();
		teacher.setUserName(teacherSaveRequest.getUserName());
		teacher.setPassword(teacherSaveRequest.getPassword());
		teacher.setEmail(teacherSaveRequest.getEmail());

		teacher.setDepartment(department);
		teacher.setTeacherNumber(teacherSaveRequest.getTeacherNumber());
		return teacher;
	}

	@Override
	public Result deleteById(long id) {

		try {
			Optional<Teacher> teacher = teacherDao.findById(id);
			if (!(teacher.equals(Optional.empty()))) {
				teacherDao.deleteById(id);
				return new Result(true, id + " id'li öğretmen silindi.");
			}
			return new Result(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findById(long id) {
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setSchedulesDto(null);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findByIdWithSchedules(long id) {
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				TeacherDto teacherDto = new TeacherDto();

				List<Schedule> schedules = teacher.get().getSchedules();
				List<ScheduleDto> schedulesDto = new ArrayList<>();
				schedules.forEach(schedule -> {
					ScheduleDto scheduleDto = new ScheduleDto();

					scheduleDto.setId(schedule.getId());
					scheduleDto.setTeacherId(schedule.getTeacher().getId());
					scheduleDto.setFull(schedule.getFull());
					scheduleDto.setDescription(schedule.getDescription());

					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(schedule.getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(schedule.getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(schedule.getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(schedule.getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(schedule.getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(schedule.getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(schedule.getLastUpdateDateSystemWorker().getAuthority());

					scheduleDto.setLastUpdateDateSystemWorkerDto(systemWorkerDto);
					scheduleDto.setCreateDate(schedule.getCreateDate());
					scheduleDto.setLastUpdateDate(schedule.getLastUpdateDate());
					scheduleDto.setDayOfWeekDto(modelMapperService.forResponse().map(schedule.getDayOfWeek(), DayOfWeekDto.class));
					scheduleDto.setHourDto(modelMapperService.forResponse().map(schedule.getHour(), HourDto.class));

					schedulesDto.add(scheduleDto);
				});

				// SchedulesDto are sorting here
				Collections.sort(schedulesDto, (o1, o2) -> {
					Long s1DayOfWeekId = o1.getDayOfWeekDto().getId();
					int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeekDto().getId());

					if (dayOfWeekCompare == 0) {
						Long s1HourId = o1.getHourDto().getId();
						int hourCompare = s1HourId.compareTo(o2.getHourDto().getId());
						return hourCompare;
					}
					return dayOfWeekCompare;
				});
				// SchedulesDto sorted

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setSchedulesDto(schedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<TeacherDto> findByIdWithAllSchedules(long id) {
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				TeacherDto teacherDto = new TeacherDto();

				List<Schedule> schedules = teacher.get().getSchedules();
				List<ScheduleDto> schedulesDto = new ArrayList<>();

				List<WeeklySchedule> weeklySchedules = teacher.get().getWeeklySchedules();
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();

				schedules.forEach(schedule -> {
					ScheduleDto scheduleDto = new ScheduleDto();

					scheduleDto.setId(schedule.getId());
					scheduleDto.setTeacherId(schedule.getTeacher().getId());
					scheduleDto.setFull(schedule.getFull());
					scheduleDto.setDescription(schedule.getDescription());

					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(schedule.getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(schedule.getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(schedule.getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(schedule.getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(schedule.getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(schedule.getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(schedule.getLastUpdateDateSystemWorker().getAuthority());

					scheduleDto.setLastUpdateDateSystemWorkerDto(systemWorkerDto);
					scheduleDto.setCreateDate(schedule.getCreateDate());
					scheduleDto.setLastUpdateDate(schedule.getLastUpdateDate());
					scheduleDto.setDayOfWeekDto(modelMapperService.forResponse().map(schedule.getDayOfWeek(), DayOfWeekDto.class));
					scheduleDto.setHourDto(modelMapperService.forResponse().map(schedule.getHour(), HourDto.class));

					schedulesDto.add(scheduleDto);});

				    // SchedulesDto are sorting here
				    Collections.sort(schedulesDto, (o1, o2) -> {
				    	Long s1DayOfWeekId = o1.getDayOfWeekDto().getId();
				    	int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeekDto().getId());

				    	if (dayOfWeekCompare == 0) {
				    		Long s1HourId = o1.getHourDto().getId();
				    		int hourCompare = s1HourId.compareTo(o2.getHourDto().getId());
				    		return hourCompare;
				    	}
				    	return dayOfWeekCompare;
				    });
				    // SchedulesDto sorted


				weeklySchedules.forEach(weeklySchedule -> {
					WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();

					weeklyScheduleDto.setId(weeklySchedule.getId());
					weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
					weeklyScheduleDto.setFull(weeklySchedule.getFull());
					weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.getLastUpdateDateSystemWorker().getAuthority());

					if(!(weeklySchedule.getStudent() == null)){
						weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());
					}

					weeklyScheduleDto.setLastUpdateDateSystemWorkerDto(systemWorkerDto);
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDayOfWeekDto(modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class));
					weeklyScheduleDto.setHourDto(modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class));

					weeklySchedulesDto.add(weeklyScheduleDto);});

				    // WeeklySchedulesDto are sorting here
				    weeklySchedulesDto.sort((o1, o2) -> {
				    	Long s1DayOfWeekId = o1.getDayOfWeekDto().getId();
				    	int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeekDto().getId());
				    	if (dayOfWeekCompare == 0) {
				    		Long s1HourId = o1.getHourDto().getId();
				    		int hourCompare = s1HourId.compareTo(o2.getHourDto().getId());
				    		return hourCompare;
				    	}
				    	return dayOfWeekCompare;
				    });
				    // WeeklySchedulesDto sorted

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setSchedulesDto(schedulesDto);
				teacherDto.setWeeklySchedulesDto(weeklySchedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findByIdWithWeeklySchedules(long id) {
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				TeacherDto teacherDto = new TeacherDto();

				List<WeeklySchedule> weeklySchedules = teacher.get().getWeeklySchedules();
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();

				weeklySchedules.forEach(weeklySchedule -> {
					WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();

					weeklyScheduleDto.setId(weeklySchedule.getId());
					weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
					weeklyScheduleDto.setFull(weeklySchedule.getFull());
					weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(weeklySchedule.getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(weeklySchedule.getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(weeklySchedule.getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(weeklySchedule.getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(weeklySchedule.getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(weeklySchedule.getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(weeklySchedule.getLastUpdateDateSystemWorker().getAuthority());

					if(!(weeklySchedule.getStudent() == null)){
						weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());
					}

					weeklyScheduleDto.setLastUpdateDateSystemWorkerDto(systemWorkerDto);
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDayOfWeekDto(modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class));
					weeklyScheduleDto.setHourDto(modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class));

					weeklySchedulesDto.add(weeklyScheduleDto);});

				    // WeeklySchedulesDto are sorting here
				    weeklySchedulesDto.sort((o1, o2) -> {
				    	Long s1DayOfWeekId = o1.getDayOfWeekDto().getId();
				    	int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeekDto().getId());
				    	if (dayOfWeekCompare == 0) {
				    		Long s1HourId = o1.getHourDto().getId();
				    		int hourCompare = s1HourId.compareTo(o2.getHourDto().getId());
				    		return hourCompare;
				    	}
				    	return dayOfWeekCompare;
				    });
				    // WeeklySchedulesDto sorted

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setWeeklySchedulesDto(weeklySchedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	// schedules değer verilirse eğer öğretmen başına 105 satır değer olucak buda
	// performans sorunu yaratır.
	// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
	@Override
	public DataResult<List<TeacherDto>> getByDepartmentId(long departmentId) {
		try {
			List<Teacher> teachers = teacherDao.getByDepartmentId(departmentId);
			if (teachers.size() != 0) {
				List<TeacherDto> teachersDto = new ArrayList<TeacherDto>();

				teachers.forEach(teacher -> {

					TeacherDto teacherDto = new TeacherDto();
					teacherDto.setId(teacher.getId());
					teacherDto.setUserName(teacher.getUserName());
					teacherDto.setEmail(teacher.getEmail());
					teacherDto.setPassword(teacher.getPassword());
					teacherDto.setCreateDate(teacher.getCreateDate());
					teacherDto.setLastUpdateDate(teacher.getLastUpdateDate());
					teacherDto.setDepartmentId(teacher.getDepartment().getId());
					teacherDto.setTeacherNumber(teacher.getTeacherNumber());

					// Burası verilirse eğer öğretmen başına 105 satır değer olucak buda performans
					// sorunu yaratır.
					// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
					teacherDto.setSchedulesDto(null);
					teachersDto.add(teacherDto);
				});

				return new DataResult<List<TeacherDto>>(teachersDto, true,
						departmentId + " departman id'li öğretmenler getirildi.");

			} else {

				return new DataResult<>(false,
						departmentId + " departman id'li bir öğretmen bulunamadı.");
			}

		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	// schedules değer verilirse eğer öğretmen başına 105 satır değer olucak buda
	// performans sorunu yaratır.
	// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
	@Override
	public DataResult<List<TeacherDto>> findAll() {
		try {
			List<Teacher> teachers = teacherDao.findAll();
			if (teachers.size() != 0) {
				List<TeacherDto> teachersDto = new ArrayList<TeacherDto>();

				teachers.forEach(teacher -> {

					TeacherDto teacherDto = new TeacherDto();
					teacherDto.setId(teacher.getId());
					teacherDto.setUserName(teacher.getUserName());
					teacherDto.setEmail(teacher.getEmail());
					teacherDto.setPassword(teacher.getPassword());
					teacherDto.setCreateDate(teacher.getCreateDate());
					teacherDto.setLastUpdateDate(teacher.getLastUpdateDate());
					teacherDto.setDepartmentId(teacher.getDepartment().getId());
					teacherDto.setTeacherNumber(teacher.getTeacherNumber());

					// Burası verilirse eğer öğretmen başına 105 satır değer olucak buda performans
					// sorunu yaratır.
					// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
					teacherDto.setSchedulesDto(null);
					teachersDto.add(teacherDto);
				});

				return new DataResult<List<TeacherDto>>(teachersDto, true, "Öğretmenler getirildi.");

			} else {

				return new DataResult<>(false, "Öğretmen bulunamadı.");
			}

		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateEmailById(long id, String email) {

		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				teacher.get().setEmail(email);

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmenin maili güncellendi.");
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateUserNameById(long id, String userName) {
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				teacher.get().setUserName(userName);

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true,
						id + " id'li öğretmenin kullanıcı adı güncellendi.");
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updatePasswordById(long id, String password) {
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				teacher.get().setPassword(password);

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmenin şifresi güncellendi.");
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// xTODO: handle exception
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateTeacherNumberById(long id, String teacherNumber) {
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				teacher.get().setTeacherNumber(teacherNumber);

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmenin numarası güncellendi.");
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateDepartmentById(long id, Long departmentId) {

		if (departmentId == null) {
			return new DataResult<TeacherDto>(false, "Departman boş bırakılamaz.");
		}
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);
			Optional<Department> department = departmentDao.findById(departmentId);

			if (!(teacher.equals(Optional.empty())) && !(department.equals(Optional.empty()))) {
				teacher.get().setDepartment(department.get());

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmenin departmanı güncellendi.");
			} else {
				if (department.equals(Optional.empty())) {
					return new DataResult<>(false,
							id + " id'li öğretmen için verdiğiniz departman id'sini kontrol ediniz.");
				}
			}
			return new DataResult<>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	public DataResult<List<ScheduleSaveRequest>> updateScheduleSaveRequestListForTeacher(TeacherSaveRequest teacherSaveRequest, Long teacherId) {
		// Eklerken hiç schedules verilmeme olasılığı olduğu için önce onu kontrol ediyorum ve
		// ScheduleValidationService de sadece create edilirken kullanılması için system çalışanı olmayan halini
		// yazdım bu arada. Eğerki eklenmişse Schedule bununda sistem çalışanı kontrol ediliyor var mı yok mu diye.
		// ve de teacher oluşturulurken tüm programların sistem çalışanı aynı olmalı ondan buraya özel kontrol yazdım.

		if (!systemWorkerDao.existsById(teacherSaveRequest.getLastUpdateDateSystemWorkerId())) {
			return new DataResult<>(false,
					"Eklediğiniz programlardaki sistem çalışanı bulunamadı kontrol ediniz.");
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
					return new DataResult<>(false,
							"Eklediğiniz programlarda gün ve saati aynı olan programlar var kontrol ediniz.");
				}
				if (scheduleSaveRequestForTeacherList.size() == 1) {
					ScheduleSaveRequest scheduleSaveRequest = modelMapperService.forRequest()
							.map(scheduleSaveRequestForTeacherList.getFirst(), ScheduleSaveRequest.class);
					scheduleSaveRequest.setTeacherId(teacherId);
					scheduleSaveRequest.setLastUpdateDateSystemWorkerId(teacherSaveRequest.getLastUpdateDateSystemWorkerId());

					willSaveSchedules.add(scheduleSaveRequest);
				} else {
					ScheduleSaveRequest emptyScheduleSaveRequest = createEmptyScheduleSaveRequest(teacherSaveRequest, teacherId, dayId, hourId);

					willSaveSchedules.add(emptyScheduleSaveRequest);
				}
			}
		}

		return new DataResult<List<ScheduleSaveRequest>>(willSaveSchedules, true, "Öğretmenin programları ayarlandı.");
	}

	private ScheduleSaveRequest createEmptyScheduleSaveRequest(TeacherSaveRequest teacherSaveRequest, Long teacherId, long dayId, long hourId) {
		ScheduleSaveRequest emptyScheduleSaveRequest = new ScheduleSaveRequest();
		emptyScheduleSaveRequest.setDayOfWeekId(dayId);
		emptyScheduleSaveRequest.setHourId(hourId);
		emptyScheduleSaveRequest.setTeacherId(teacherId);
		emptyScheduleSaveRequest.setLastUpdateDateSystemWorkerId(teacherSaveRequest.getLastUpdateDateSystemWorkerId());
		emptyScheduleSaveRequest.setDescription(Schedule.DEFAULT_DESCRIPTION);
		emptyScheduleSaveRequest.setFull(false);
		return emptyScheduleSaveRequest;
	}

	@Override
	public DataResult<Long> getCount() {
		// xTODO Auto-generated method stub
		try {
			return new DataResult<Long>(teacherDao.count(), true, "Öğretmenlerin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

}

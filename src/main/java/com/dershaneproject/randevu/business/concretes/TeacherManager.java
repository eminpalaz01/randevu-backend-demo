package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.dershaneproject.randevu.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.business.abstracts.TeacherService;
import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.DepartmentDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemWorkerDao;
import com.dershaneproject.randevu.dataAccess.abstracts.TeacherDao;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Department;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;

@Service
public class TeacherManager implements TeacherService {

	private TeacherDao teacherDao;
	private DepartmentDao departmentDao;
	private HourDao hourDao;
	private DayOfWeekDao dayOfWeekDao;
	private ScheduleService scheduleService;
	private ScheduleValidationService scheduleValidationService;
	private WeeklyScheduleService weeklyScheduleService;
	private SystemWorkerDao systemWorkerDao;
	private ModelMapperServiceWithTypeMappingConfigs modelMapperService;

	@Autowired
	public TeacherManager(TeacherDao teacherDao, DepartmentDao departmentDao, HourDao hourDao,
			DayOfWeekDao dayOfWeekDao, ScheduleService scheduleService, WeeklyScheduleService weeklyScheduleService,
			ModelMapperServiceWithTypeMappingConfigs modelMapperService, SystemWorkerDao systemWorkerDao,
			ScheduleValidationService scheduleValidationService) {
		this.teacherDao = teacherDao;
		this.departmentDao = departmentDao;
		this.hourDao = hourDao;
		this.dayOfWeekDao = dayOfWeekDao;
		this.scheduleService = scheduleService;
		this.scheduleValidationService = scheduleValidationService;
		this.weeklyScheduleService = weeklyScheduleService;
		this.modelMapperService = modelMapperService;
		this.systemWorkerDao = systemWorkerDao;
	}

	@Transactional
	@Override
	public DataResult<TeacherDto> save(TeacherDto teacherDto) {
		try {
			Optional<Department> department = departmentDao.findById(teacherDto.getDepartmentId());
			List<ScheduleDto> schedulesDto = teacherDto.getSchedules();

			if (schedulesDto == null) {
				schedulesDto = new ArrayList<ScheduleDto>();
			}

			if (department.equals(Optional.empty())) {
				return new DataResult<TeacherDto>(false,
						"Veritabanına öğretmen kaydı başarısız departman id'sini kontrol ediniz.");
			} else {
				Teacher teacher = new Teacher();
				teacher.setUserName(teacherDto.getUserName());
				teacher.setPassword(teacherDto.getPassword());
				teacher.setEmail(teacherDto.getEmail());

				teacher.setDepartment(department.get());
				teacher.setTeacherNumber(teacherDto.getTeacherNumber());

				Teacher teacherDb = teacherDao.save(teacher);
				
				// if teacherDb is not null set id, dates and save schedulesDto of teacherDto
				if (teacherDb != null) {

					// schedulesDto are updating for register
					DataResult<List<ScheduleDto>> resultUpdateSchedulesDto = updateSchedulesDtoForTeacher(schedulesDto,
							teacherDb.getId());

					if (resultUpdateSchedulesDto.isSuccess()) {
						// schedulesDto are validating
						Result resultValidationSchedulesDto = scheduleValidationService
								.areValidateForCreateTeacherResult(resultUpdateSchedulesDto.getData());

						if (resultValidationSchedulesDto.isSuccess()) {
							// schedulesDto are saving and updating
							DataResult<List<ScheduleDto>> resultResponseSchedulesDto = scheduleService
									.saveAllForCreateTeacher(resultUpdateSchedulesDto.getData());
							
							if (resultResponseSchedulesDto.isSuccess()) {
								List<ScheduleDto> schedulesDtoResult = resultResponseSchedulesDto.getData();
								List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();

								// schedulesDto mapping to weeklySchedulesDto
								schedulesDtoResult.forEach(scheduleDto -> {
									WeeklyScheduleDto weeklyScheduleDto = modelMapperService.forResponse()
											.map(scheduleDto,  WeeklyScheduleDto.class);
									weeklyScheduleDto.setStudentId(null);
									weeklyScheduleDto.setDescription(scheduleDto.getDescription());
									weeklySchedulesDto.add(weeklyScheduleDto);
								});

								// weeklySchedulesDto are saving and updating
								DataResult<List<WeeklyScheduleDto>> resultResponseWeeklySchedulesDto
										= weeklyScheduleService.saveAll(weeklySchedulesDto);
								 if(resultResponseWeeklySchedulesDto.isSuccess()){

									 teacherDto.setSchedules(schedulesDtoResult);
									 teacherDto.setWeeklySchedules(resultResponseWeeklySchedulesDto.getData());

									 teacherDto.setId(teacherDb.getId());
									 teacherDto.setCreateDate(teacherDb.getCreateDate());
									 teacherDto.setLastUpdateDate(teacherDb.getLastUpdateDate());
									 return new DataResult<TeacherDto>(teacherDto, true, "Öğretmen veritabanına eklendi.(Gelen response'da"
											 + " schedule ların date lerinde 1 2 saniye yanılma payı vardır "
											 + "sadece oluşturulurken date leri getirmediği için tekrar istek atmak yerine "
											 + "database e kendim anlık tarihi koydum tüm schedule larda ama database de tamamen doğru"
											 + " şekildedir bir dahaki isteklerde yanılma payı yoktur.) null olmasıda tercih edilebilirdi.");
								 }else{
									 teacherDao.deleteById(teacherDb.getId());
									 return new DataResult<TeacherDto>(false, resultResponseWeeklySchedulesDto.getMessage());
								 }

							} else {
								teacherDao.deleteById(teacherDb.getId());
								return new DataResult<TeacherDto>(false, resultResponseSchedulesDto.getMessage());
							}

						} else {
							teacherDao.deleteById(teacherDb.getId());
							return new DataResult<TeacherDto>(false, resultValidationSchedulesDto.getMessage());
						}

					} else {
						teacherDao.deleteById(teacherDb.getId());
						return new DataResult<TeacherDto>(false, resultUpdateSchedulesDto.getMessage());
					}

				} else {
					return new DataResult<TeacherDto>(false,
							"Öğretmen veritabanına kaydedilirken bir sorun ile karşılaşıldı.");
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

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
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findById(long id) {
		// TODO Auto-generated method stub
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
				teacherDto.setSchedules(null);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findByIdWithSchedules(long id) {
		// TODO Auto-generated method stub
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

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);
					scheduleDto.setCreateDate(schedule.getCreateDate());
					scheduleDto.setLastUpdateDate(schedule.getLastUpdateDate());
					scheduleDto.setDayOfWeek(modelMapperService.forResponse().map(schedule.getDayOfWeek(), DayOfWeekDto.class));
					scheduleDto.setHour(modelMapperService.forResponse().map(schedule.getHour(), HourDto.class));

					schedulesDto.add(scheduleDto);
				});

				// SchedulesDto are sorting here
				Collections.sort(schedulesDto, (o1, o2) -> {
					Long s1DayOfWeekId = o1.getDayOfWeek().getId();
					int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());

					if (dayOfWeekCompare == 0) {
						Long s1HourId = o1.getHour().getId();
						int hourCompare = s1HourId.compareTo(o2.getHour().getId());
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
				teacherDto.setSchedules(schedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<TeacherDto> findByIdWithAllSchedules(long id) {
		// TODO Auto-generated method stub
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

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);
					scheduleDto.setCreateDate(schedule.getCreateDate());
					scheduleDto.setLastUpdateDate(schedule.getLastUpdateDate());
					scheduleDto.setDayOfWeek(modelMapperService.forResponse().map(schedule.getDayOfWeek(), DayOfWeekDto.class));
					scheduleDto.setHour(modelMapperService.forResponse().map(schedule.getHour(), HourDto.class));

					schedulesDto.add(scheduleDto);});

				    // SchedulesDto are sorting here
				    Collections.sort(schedulesDto, (o1, o2) -> {
				    	Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				    	int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());

				    	if (dayOfWeekCompare == 0) {
				    		Long s1HourId = o1.getHour().getId();
				    		int hourCompare = s1HourId.compareTo(o2.getHour().getId());
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

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDayOfWeek(modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class));
					weeklyScheduleDto.setHour(modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class));

					weeklySchedulesDto.add(weeklyScheduleDto);});

				    // WeeklySchedulesDto are sorting here
				    weeklySchedulesDto.sort((o1, o2) -> {
				    	Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				    	int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());
				    	if (dayOfWeekCompare == 0) {
				    		Long s1HourId = o1.getHour().getId();
				    		int hourCompare = s1HourId.compareTo(o2.getHour().getId());
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
				teacherDto.setSchedules(schedulesDto);
				teacherDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findByIdWithWeeklySchedules(long id) {
		// TODO Auto-generated method stub
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

					weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDayOfWeek(modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class));
					weeklyScheduleDto.setHour(modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class));

					weeklySchedulesDto.add(weeklyScheduleDto);});

				    // WeeklySchedulesDto are sorting here
				    weeklySchedulesDto.sort((o1, o2) -> {
				    	Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				    	int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());
				    	if (dayOfWeekCompare == 0) {
				    		Long s1HourId = o1.getHour().getId();
				    		int hourCompare = s1HourId.compareTo(o2.getHour().getId());
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
				teacherDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	// schedules değer verilirse eğer öğretmen başına 105 satır değer olucak buda
	// performans sorunu yaratır.
	// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
	@Override
	public DataResult<List<TeacherDto>> getByDepartmentId(long departmentId) {
		// TODO Auto-generated method stub
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
					teacherDto.setSchedules(null);
					teachersDto.add(teacherDto);
				});

				return new DataResult<List<TeacherDto>>(teachersDto, true,
						departmentId + " departman id'li öğretmenler getirildi.");

			} else {

				return new DataResult<List<TeacherDto>>(false,
						departmentId + " departman id'li bir öğretmen bulunamadı.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<TeacherDto>>(false, e.getMessage());
		}
	}

	// schedules değer verilirse eğer öğretmen başına 105 satır değer olucak buda
	// performans sorunu yaratır.
	// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
	@Override
	public DataResult<List<TeacherDto>> findAll() {
		// TODO Auto-generated method stub
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
					teacherDto.setSchedules(null);
					teachersDto.add(teacherDto);
				});

				return new DataResult<List<TeacherDto>>(teachersDto, true, "Öğretmenler getirildi.");

			} else {

				return new DataResult<List<TeacherDto>>(false, "Öğretmen bulunamadı.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<TeacherDto>>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateEmailById(long id, String email) {
		// TODO Auto-generated method stub

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
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateUserNameById(long id, String userName) {
		// TODO Auto-generated method stub
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
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updatePasswordById(long id, String password) {
		// TODO Auto-generated method stub
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
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateTeacherNumberById(long id, String teacherNumber) {
		// TODO Auto-generated method stub
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
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateDepartmentById(long id, long departmentId) {
		// TODO Auto-generated method stub
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
					return new DataResult<TeacherDto>(false,
							id + " id'li öğretmen için verdiğiniz departman id'sini kontrol ediniz.");
				}
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	public DataResult<List<ScheduleDto>> updateSchedulesDtoForTeacher(List<ScheduleDto> schedulesDto, long teacherId) {

		// Eklerken hiç schedules verilmeme olasılığı olduğu için önce onu kontrol ediyorum ve
		// ScheduleValidationService de sadece create edilirken kullanılması için system çalışanı olmayan halini
		// yazdım bu arada. Eğerki eklenmişse Schedule bununda sistem çalışanı kontrol ediliyor var mı yok mu diye.
		// ve de teacher oluşturulurken tüm programların sistem çalışanı aynı olmalı ondan buraya özel kontrol yazdım.
		boolean systemWorkersAreAllTheSame = schedulesDto.stream()
				.filter(schedule -> schedule.getLastUpdateDateSystemWorker() != null)
				.map(schedule -> schedule.getLastUpdateDateSystemWorker()).distinct().count() <= 1;

		if (systemWorkersAreAllTheSame == false) {
			return new DataResult<List<ScheduleDto>>(false,
					"Programlarınızda eklediğiniz sistem çalışanlarının bazıları birbiriyle farklı dikkat ediniz. "
							+ "(Bu istekte tek bir sistem çalışanı gönderilebilir.)");
		}

		SystemWorkerDto systemWorker = schedulesDto.stream()
				.filter(schedule -> schedule.getLastUpdateDateSystemWorker() != null).findAny().get()
				.getLastUpdateDateSystemWorker();

		if (systemWorker != null) {
			if (!(systemWorkerDao.existsById(systemWorker.getId()))) {
				return new DataResult<List<ScheduleDto>>(false,
						"Eklediğiniz programlardaki sistem çalışanı bulunamadı kontrol ediniz.");
			}
		}
		// SystemWorker kontrolü tamamlandı. En başta yapmamın sebebi aşağıdakiler
		// sistemi yorucak işler olabilir.

		// Dao dan toplam sayılarını getiriyor.
		long dayOfWeekCount = dayOfWeekDao.count();
		long hourCount = hourDao.count();

		List<ScheduleDto> willSaveSchedulesDto = new ArrayList<>();

		for (int i = 0; i < dayOfWeekCount; i++) {

			for (int k = 0; k < hourCount; k++) {
				final int dayId = i + 1;
				final int hourId = k + 1;

				List<ScheduleDto> scheduleDto = schedulesDto.stream().filter(
						schedule -> schedule.getDayOfWeek().getId() == dayId && schedule.getHour().getId() == hourId)
						.collect(Collectors.toList());

				if (!(scheduleDto.size() > 1)) {
					if (scheduleDto.size() == 1) {
						scheduleDto.get(0).setTeacherId(teacherId);
						scheduleDto.get(0).setLastUpdateDateSystemWorker(systemWorker);

						willSaveSchedulesDto.add(scheduleDto.get(0));
					} else {
						ScheduleDto emptyscheduleDto = new ScheduleDto();
						
						HourDto hourDto = new HourDto();
						DayOfWeekDto dayOfWeekDto = new DayOfWeekDto();						
						
						hourDto.setId((long) hourId);
						dayOfWeekDto.setId((long) dayId);
						
						emptyscheduleDto.setDayOfWeek(dayOfWeekDto);
						emptyscheduleDto.setHour(hourDto);
						emptyscheduleDto.setTeacherId(teacherId);
						emptyscheduleDto.setLastUpdateDateSystemWorker(systemWorker);
						emptyscheduleDto.setDescription(null);
						emptyscheduleDto.setFull(false);

						willSaveSchedulesDto.add(emptyscheduleDto);
					}
				} else {
					return new DataResult<List<ScheduleDto>>(false,
							"Eklediğiniz programlarda gün ve saati aynı olan değerleriniz var kontrol ediniz.");
				}

			}
		}

		return new DataResult<List<ScheduleDto>>(willSaveSchedulesDto, true, "Öğretmenin programları ayarlandı.");
	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(teacherDao.count(), true, "Öğretmenlerin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}

}

package com.dershaneproject.randevu.business.concretes;

import java.util.*;

import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.*;
import com.dershaneproject.randevu.entities.concretes.*;
import com.dershaneproject.randevu.validations.abstracts.WeeklyScheduleValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;

@Service
public class WeeklyScheduleManager implements WeeklyScheduleService{

	private ModelMapperServiceWithTypeMappingConfigs modelMapperService;
	private WeeklyScheduleDao weeklyScheduleDao;
	private TeacherDao teacherDao;
	private DayOfWeekDao dayOfWeekDao;
	private HourDao hourDao;
	private StudentDao studentDao;
	private SystemWorkerDao systemWorkerDao;
	private WeeklyScheduleValidationService weeklyScheduleValidationService;
	
	@Autowired
	public WeeklyScheduleManager(ModelMapperServiceWithTypeMappingConfigs modelMapperService, WeeklyScheduleDao weeklyScheduleDao,
			TeacherDao teacherDao, DayOfWeekDao dayOfWeekDao, HourDao hourDao, StudentDao studentDao, SystemWorkerDao systemWorkerDao,
			WeeklyScheduleValidationService weeklyScheduleValidationService) {
		this.modelMapperService = modelMapperService;
		this.weeklyScheduleDao = weeklyScheduleDao;
		this.teacherDao = teacherDao;
		this.dayOfWeekDao = dayOfWeekDao;
		this.hourDao = hourDao;
		this.studentDao = studentDao;
		this.systemWorkerDao = systemWorkerDao;
		this.weeklyScheduleValidationService = weeklyScheduleValidationService;
	}

	@Override
	public DataResult<WeeklyScheduleDto> save(WeeklyScheduleDto weeklyScheduleDto) {
		// TODO Auto-generated method stub
		try {
			Result validateResult = weeklyScheduleValidationService.isValidateResult(weeklyScheduleDto);

			if (validateResult.isSuccess()){
				WeeklySchedule weeklySchedule = new WeeklySchedule();

				SystemWorker lastUpdateSystemWorker =
						systemWorkerDao.findById(weeklyScheduleDto.getLastUpdateDateSystemWorker().getId()).get();

				Teacher teacher = new Teacher();
				teacher.setId(weeklyScheduleDto.getTeacherId());

				Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(weeklyScheduleDto.getDayOfWeek().getId());
				Optional<Hour> hour = hourDao.findById(weeklyScheduleDto.getHour().getId());

				HourDto hourDto = modelMapperService.forResponse().map(hour.get(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(dayOfWeek.get(), DayOfWeekDto.class);

				if(weeklyScheduleDto.getStudentId() != null){
                   Result studentValidateResult = weeklyScheduleValidationService.studentExistById(weeklyScheduleDto);
					if(!studentValidateResult.isSuccess()){
						return new DataResult<WeeklyScheduleDto>(false, studentValidateResult.getMessage());
					}
					else {
						Student student = new Student();
						student.setId(weeklyScheduleDto.getStudentId());
						weeklySchedule.setStudent(student);
					}

				}

				weeklySchedule.setLastUpdateDateSystemWorker(lastUpdateSystemWorker);
				weeklySchedule.setFull(weeklyScheduleDto.getFull());
				weeklySchedule.setDescription(weeklyScheduleDto.getDescription());
				weeklySchedule.setTeacher(teacher);
				weeklySchedule.setDayOfWeek(dayOfWeek.get());
				weeklySchedule.setHour(hour.get());
				WeeklySchedule weeklyScheduleDb = weeklyScheduleDao.save(weeklySchedule);

				weeklyScheduleDto.setId(weeklyScheduleDb.getId());
				weeklyScheduleDto.setCreateDate(weeklyScheduleDb.getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklyScheduleDb.getLastUpdateDate());
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);

				if(weeklyScheduleDb.getStudent() != null){
					weeklyScheduleDto.setStudentId(weeklyScheduleDb.getStudent().getId());
				}

				weeklyScheduleDto.setTeacherId(weeklyScheduleDb.getTeacher().getId());

				// if I use the mapper it get the schedules,weeklySchedules (PERFORMANCE PROBLEM)
				SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
				systemWorkerDto.setId(weeklyScheduleDb.getLastUpdateDateSystemWorker().getId());
				systemWorkerDto.setUserName(weeklyScheduleDb.getLastUpdateDateSystemWorker().getUserName());
				systemWorkerDto.setEmail(weeklyScheduleDb.getLastUpdateDateSystemWorker().getEmail());
				systemWorkerDto.setPassword(weeklyScheduleDb.getLastUpdateDateSystemWorker().getPassword());
				systemWorkerDto.setCreateDate(weeklyScheduleDb.getLastUpdateDateSystemWorker().getCreateDate());
				systemWorkerDto.setLastUpdateDate(weeklyScheduleDb.getLastUpdateDateSystemWorker().getLastUpdateDate());
				systemWorkerDto.setAuthority(weeklyScheduleDb.getLastUpdateDateSystemWorker().getAuthority());

				weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, "Haftalık Program veritabanına eklendi.");
			}  else {
				return new DataResult<WeeklyScheduleDto>(false, validateResult.getMessage());
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}

	}
	
	@Override // add student to system after check again
	public DataResult<List<WeeklyScheduleDto>> saveAll(List<WeeklyScheduleDto> weeklySchedulesDto) {
		// TODO Auto-generated method stub

	  try {
		// Firstly, weeklySchedulesDto is validating one by one
		for(WeeklyScheduleDto weeklyScheduleDto: weeklySchedulesDto) {
			Result validateResult = weeklyScheduleValidationService.isValidateResult(weeklyScheduleDto);
			if (!validateResult.isSuccess()) {
				return new DataResult<List<WeeklyScheduleDto>>(false, "Haftalık programlar"
						+ validateResult.getMessage().substring(17));
			}
		}

		List<WeeklySchedule> weeklySchedules = new ArrayList<>();

		String description = "DEFAULT DESCRIPTION";

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

			 // WeeklySchedules will create and add to list
			 for (int i = 0; i < weeklySchedulesDto.size(); i++) {
				 WeeklyScheduleDto weeklyScheduleDto = weeklySchedulesDto.get(i);
				 WeeklySchedule weeklySchedule = new WeeklySchedule();

				 Optional<SystemWorker> systemWorker = Optional.empty();
				 SystemWorkerDto systemWorkerDto = null;

				 // systemWorker of current weeklySchedule is translating to dto for response
				 systemWorker = systemWorkerDao.findById(weeklyScheduleDto.getLastUpdateDateSystemWorker().getId());

				 systemWorkerDto = new SystemWorkerDto();
				 systemWorkerDto.setId(systemWorker.get().getId());
				 systemWorkerDto.setUserName(systemWorker.get().getUserName());
				 systemWorkerDto.setEmail(systemWorker.get().getEmail());
				 systemWorkerDto.setPassword(systemWorker.get().getPassword());
				 systemWorkerDto.setCreateDate(systemWorker.get().getCreateDate());
				 systemWorkerDto.setLastUpdateDate(systemWorker.get().getLastUpdateDate());
				 systemWorkerDto.setAuthority(systemWorker.get().getAuthority());

				 weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				// if weeklySchedule has a student
				if(weeklyScheduleDto.getStudentId() != null && weeklyScheduleDto.getStudentId() != 0){

					// weeklyScheduleDto's student is validating
					Result studentValidateResult = weeklyScheduleValidationService.studentExistById(weeklyScheduleDto);
					if(!studentValidateResult.isSuccess()){
						return new DataResult<List<WeeklyScheduleDto>>(false, "Haftalık programlar"
								+ studentValidateResult.getMessage().substring(17));
					}
					else {
						// student added to weeklySchedule
						Student student = new Student(); // I don't use dao because it is unnecessary
						student.setId(weeklyScheduleDto.getStudentId());
						weeklySchedule.setStudent(student);
					}

				}

				 // if description is null set default description
				 if(weeklyScheduleDto.getDescription() == null) {
					 weeklyScheduleDto.setDescription(description);
				 }

				 // finding the objects of scheduleDto with id from Databases
				 Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(weeklyScheduleDto.getDayOfWeek().getId());
				 Optional<Hour> hour = hourDao.findById(weeklyScheduleDto.getHour().getId());

				 // objects are translating to dto
				 HourDto hourDto = modelMapperService.forResponse().map(hour, HourDto.class);
				 DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(dayOfWeek, DayOfWeekDto.class);

				 weeklyScheduleDto.setHour(hourDto);
				 weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);

				 weeklySchedule.setFull(weeklyScheduleDto.getFull());


				 weeklySchedule.setDescription(weeklyScheduleDto.getDescription());

				 // if I get the teacher it is really expensive and unuseful for system
				 Teacher teacher = new Teacher();
				 teacher.setId(weeklyScheduleDto.getTeacherId());
				 weeklySchedule.setTeacher(teacher);

				 // objects are setting to weeklySchedule
				 weeklySchedule.setLastUpdateDateSystemWorker(systemWorker.get());
				 weeklySchedule.setDayOfWeek(dayOfWeek.get());
				 weeklySchedule.setHour(hour.get());

				 // weeklySchedule adding to list here
				 weeklySchedules.add(weeklySchedule);
			 }
			 // WeeklySchedules created and added to list

			 // WeeklySchedule's id and dates return to list
			 Date fakeDate = new Date();
			 List<WeeklySchedule> schedulesDb = weeklyScheduleDao.saveAll(weeklySchedules);

			 // WeeklySchedulesDb are sorting here
			 Collections.sort(schedulesDb, (o1, o2) -> {
				 Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				 int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());

				 if (dayOfWeekCompare == 0) {
					 Long s1HourId = o1.getHour().getId();
					 int hourCompare = s1HourId.compareTo(o2.getHour().getId());
					 return hourCompare;
				 }
				 return dayOfWeekCompare;
			 });
			 // WeeklySchedulesDb sorted

			 // WeeklySchedule sorted and id and dates are set for weeklySchedulesDto
			 for (int i = 0; i < schedulesDb.size(); i++) {
				 WeeklyScheduleDto weeklyScheduleDto = weeklySchedulesDto.get(i);

				 weeklyScheduleDto.setId(schedulesDb.get(i).getId());
				 weeklyScheduleDto.setCreateDate(fakeDate);
				 weeklyScheduleDto.setLastUpdateDate(fakeDate);
			 }

			 // WeeklySchedules are sending here
			 return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, true, "Haftalık programlar veritabanına eklendi.");

	  }
	  catch(Exception e){
		 return new DataResult<List<WeeklyScheduleDto>>(false, e.getMessage());
	  }

	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		try {
			Boolean weeklyScheduleIsFull = weeklyScheduleDao.existsById(id);
			if (weeklyScheduleIsFull) {
				weeklyScheduleDao.deleteById(id);
				return new Result(true, id + " id'li haftalık program silindi.");
			}

			return new Result(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<WeeklyScheduleDto>> findAll() {
		// TODO Auto-generated method stub
		try {
			List<WeeklySchedule> weeklySchedules = weeklyScheduleDao.findAll();
			if (weeklySchedules.size() != 0) {
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

				return new DataResult<List<WeeklyScheduleDto>>(false, "Haftalık program bulunamadı.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<WeeklyScheduleDto>>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<WeeklyScheduleDto> findById(long id) {
		// TODO Auto-generated method stub
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
			return new DataResult<WeeklyScheduleDto>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<WeeklyScheduleDto> updateFullById(long id, Boolean full) {
		// TODO Auto-generated method stub
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
			return new DataResult<WeeklyScheduleDto>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateTeacherById(long id, Long teacherId) {
		// TODO Auto-generated method stub
		if(teacherId != null){
			if(!teacherDao.existsById(teacherId)){
				return new DataResult<WeeklyScheduleDto>(false, teacherId
						+ " id li öğretmen bulunamadı.");
			}
		}

		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);

			if (!(weeklySchedule.equals(Optional.empty()))) {
				 if(teacherId == null){
					 return new DataResult<WeeklyScheduleDto>(false, "Öğretmen boş olamaz.");
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
			return new DataResult<WeeklyScheduleDto>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateStudentById(long id, Long studentId) {
		// TODO Auto-generated method stub
		if(studentId != null){
			if(!studentDao.existsById(studentId)){
				return new DataResult<WeeklyScheduleDto>(false, studentId
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
			return new DataResult<WeeklyScheduleDto>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateLastUpdateDateSystemWorkerById(long id,
			Long lastUpdateDateSystemWorkerId) {
		// TODO Auto-generated method stub
		if (lastUpdateDateSystemWorkerId == null) {
			return new DataResult<WeeklyScheduleDto>(false, "Sistem çalışanı boş bırakılamaz.");
		}
		if(!systemWorkerDao.existsById(lastUpdateDateSystemWorkerId) ){
			return new DataResult<WeeklyScheduleDto>(false, lastUpdateDateSystemWorkerId
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
			return new DataResult<WeeklyScheduleDto>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) {
		// TODO Auto-generated method stub

		if (dayOfWeekId == null) {
			return new DataResult<>(false, "Gün boş bırakılamaz.");
		}
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);

			if (!(weeklySchedule.equals(Optional.empty()))) {
				if(!dayOfWeekDao.existsById(dayOfWeekId)){
					return new DataResult<WeeklyScheduleDto>(false, "Verdiğiniz gün id'sini kontrol ediniz.");
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
			return new DataResult<WeeklyScheduleDto>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<WeeklyScheduleDto> updateHourById(long id, Long hourId) {
		// TODO Auto-generated method stub

		if (hourId == null) {
			return new DataResult<>(false,"Saat boş bırakılamaz.");
		}
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);

			if (!(weeklySchedule.equals(Optional.empty()))) {
				if(!hourDao.existsById(hourId)){
					return new DataResult<WeeklyScheduleDto>(false, "Verdiğiniz saat id'sini kontrol ediniz.");
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
			return new DataResult<WeeklyScheduleDto>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<WeeklyScheduleDto> updateDescriptionById(long id, String description) {
		// TODO Auto-generated method stub
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
			return new DataResult<WeeklyScheduleDto>(false, id + " id'li haftalık program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {	
		return new DataResult<Long>(weeklyScheduleDao.count(), true,
				"Haftalık programların sayısı getirildi."); 
    }
	 
}

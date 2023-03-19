package com.dershaneproject.randevu.business.concretes;

import java.util.*;

import com.dershaneproject.randevu.dto.*;
import com.dershaneproject.randevu.entities.concretes.*;
import com.dershaneproject.randevu.validations.abstracts.WeeklyScheduleValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.TeacherDao;
import com.dershaneproject.randevu.dataAccess.abstracts.WeeklyScheduleDao;

@Service
public class WeeklyScheduleManager implements WeeklyScheduleService{

	private ModelMapperServiceWithTypeMappingConfigs modelMapperService;
	private WeeklyScheduleDao weeklyScheduleDao;
	private TeacherDao teacherDao;
	private DayOfWeekDao dayOfWeekDao;
	private HourDao hourDao;
	private WeeklyScheduleValidationService weeklyScheduleValidationService;
	
	@Autowired
	public WeeklyScheduleManager(ModelMapperServiceWithTypeMappingConfigs modelMapperService, WeeklyScheduleDao weeklyScheduleDao,
			TeacherDao teacherDao, DayOfWeekDao dayOfWeekDao, HourDao hourDao,
			WeeklyScheduleValidationService weeklyScheduleValidationService) {
		this.modelMapperService = modelMapperService;
		this.weeklyScheduleDao = weeklyScheduleDao;
		this.teacherDao = teacherDao;
		this.dayOfWeekDao = dayOfWeekDao;
		this.hourDao = hourDao;
		this.weeklyScheduleValidationService = weeklyScheduleValidationService;
	}

	@Override
	public DataResult<WeeklyScheduleDto> save(WeeklyScheduleDto weeklyScheduleDto) {
		// TODO Auto-generated method stub
		try {
			Result validateResult = weeklyScheduleValidationService.isValidateResult(weeklyScheduleDto);

			if (validateResult.isSuccess()){
				WeeklySchedule weeklySchedule = new WeeklySchedule();
				Optional<Teacher> teacher = teacherDao.findById(weeklyScheduleDto.getTeacherId());
				Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(weeklyScheduleDto.getDayOfWeek().getId());
				Optional<Hour> hour = hourDao.findById(weeklyScheduleDto.getHour().getId());

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
				else{
					weeklySchedule.setStudent(null);
				}

				weeklySchedule.setFull(false);
				weeklySchedule.setLastUpdateDateSystemWorker(null);
				weeklySchedule.setDescription(weeklyScheduleDto.getDescription());
				weeklySchedule.setTeacher(teacher.get());
				weeklySchedule.setDayOfWeek(dayOfWeek.get());
				weeklySchedule.setHour(hour.get());

				WeeklySchedule weeklyScheduleDb = weeklyScheduleDao.save(weeklySchedule);

				weeklyScheduleDto.setId(weeklyScheduleDb.getId());
				weeklyScheduleDto.setCreateDate(weeklyScheduleDb.getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklyScheduleDb.getLastUpdateDate());

				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, "Haftalık Program veritabanına eklendi.");
			}  else {
				return new DataResult<WeeklyScheduleDto>(false, validateResult.getMessage());
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}

	}
	
	@Override
	public DataResult<List<WeeklyScheduleDto>> saveAll(List<WeeklyScheduleDto> weeklySchedulesDto) {
		// TODO Auto-generated method stub
		List<WeeklySchedule> weeklySchedules = new ArrayList<>();

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

	 try {
		for(WeeklyScheduleDto weeklyScheduleDto: weeklySchedulesDto){
			// Firstly, weeklyScheduleDto is validating
			Result validateResult = weeklyScheduleValidationService.isValidateResult(weeklyScheduleDto);

			if(!validateResult.isSuccess()){
				return new DataResult<List<WeeklyScheduleDto>>(false, "Haftalık programlar"
						+ validateResult.getMessage().substring(17));
			}else {
				WeeklySchedule weeklySchedule;

				// if weeklySchedule has a student
				if(weeklyScheduleDto.getStudentId() != null){
					// weeklyScheduleDto mapping to weeklySchedule
					weeklySchedule =
							modelMapperService.forResponse().map(weeklyScheduleDto, WeeklySchedule.class);

					// weeklyScheduleDto's student is validating
					Result studentValidateResult = weeklyScheduleValidationService.studentExistById(weeklyScheduleDto);
					if(!studentValidateResult.isSuccess()){
						return new DataResult<List<WeeklyScheduleDto>>(false, "Haftalık programlar"
								+ studentValidateResult.getMessage().substring(17));
					}
					else {
						// student added to weeklySchedule
						Student student = new Student();// I don't use dao because it is unnecessary
						student.setId(weeklyScheduleDto.getStudentId());
						weeklySchedule.setStudent(student);
					}

				}
				else{
					// if weeklySchedule doesn't have a student
					weeklySchedule =
							modelMapperService.forResponse().map(weeklyScheduleDto, WeeklySchedule.class);
					weeklySchedule.setStudent(null);
				}
					weeklySchedules.add(weeklySchedule);
				}
		}
		    Date weeklyScheduleFakeDate = new Date(); // saveAll don't return dates
		    List<WeeklySchedule> weeklySchedulesDb = weeklyScheduleDao.saveAll(weeklySchedules);

			// weeklySchedulesDb are sorting here
		    weeklySchedulesDb.sort((o1, o2) -> {
				Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());

				if (dayOfWeekCompare == 0) {
					Long s1HourId = o1.getHour().getId();
					int hourCompare = s1HourId.compareTo(o2.getHour().getId());
					return hourCompare;
				}
				return dayOfWeekCompare;
			});
		    // weeklySchedulesDb sorted

		    // weeklySchedulesDto is updating here for response
			int weeklySchedulesDbLength = weeklySchedulesDb.size();
			for(int i = 0; i<weeklySchedulesDbLength; i++){
				WeeklyScheduleDto weeklyScheduleDto = weeklySchedulesDto.get(i);
				WeeklySchedule weeklyScheduleDb = weeklySchedulesDb.get(i);

				SystemWorkerDto systemWorkerDto =
						modelMapperService.forResponse().map(weeklyScheduleDb.getLastUpdateDateSystemWorker(),
								SystemWorkerDto.class);
				DayOfWeekDto dayOfWeekDto =
						modelMapperService.forResponse().map(weeklyScheduleDb.getDayOfWeek(),
								DayOfWeekDto.class);
				HourDto hourDto =
						modelMapperService.forResponse().map(weeklyScheduleDb.getHour(),
								HourDto.class);

				weeklyScheduleDto.setId(weeklyScheduleDb.getId());
				weeklyScheduleDto.setCreateDate(weeklyScheduleFakeDate);
				weeklyScheduleDto.setLastUpdateDate(weeklyScheduleFakeDate);
				weeklyScheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);
				weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
				weeklyScheduleDto.setHour(hourDto);
			}

			return new DataResult<List<WeeklyScheduleDto>>(weeklySchedulesDto, true, "Haftalık programlar eklendi.");
	 }
	 catch(Exception e){
		 return new DataResult<List<WeeklyScheduleDto>>(false, e.getMessage());
	 }

	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);
			if (!(weeklySchedule.equals(Optional.empty()))) {
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
						SystemWorkerDto systemWorkerDto = 
								modelMapperService.forResponse().map(weeklySchedule.getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(weeklySchedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
					weeklyScheduleDto.setStudentId(weeklySchedule.get().getStudent().getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					weeklyScheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(weeklySchedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
	public DataResult<WeeklyScheduleDto> updateTeacherById(long id, long teacherId) {
		// TODO Auto-generated method stub
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);

			if (!(weeklySchedule.equals(Optional.empty()))) {
				weeklySchedule.get().getTeacher().setId(teacherId);
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(weeklySchedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
	public DataResult<WeeklyScheduleDto> updateStudentById(long id, long studentId) {
		// TODO Auto-generated method stub
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);

			if (!(weeklySchedule.equals(Optional.empty()))) {
				weeklySchedule.get().getStudent().setId(studentId);
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(weeklySchedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
			long lastUpdateDateSystemWorkerId) {
		// TODO Auto-generated method stub
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(weeklySchedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
	public DataResult<WeeklyScheduleDto> updateDayOfWeekById(long id, long dayOfWeekId) {
		// TODO Auto-generated method stub
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);

			if (!(weeklySchedule.equals(Optional.empty()))) {
				weeklySchedule.get().getDayOfWeek().setId(dayOfWeekId);
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(weeklySchedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
	public DataResult<WeeklyScheduleDto> updateHourById(long id, long hourId) {
		// TODO Auto-generated method stub
		try {
			Optional<WeeklySchedule> weeklySchedule = weeklyScheduleDao.findById(id);

			if (!(weeklySchedule.equals(Optional.empty()))) {
				weeklySchedule.get().getHour().setId(hourId);
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(weeklySchedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(weeklySchedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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

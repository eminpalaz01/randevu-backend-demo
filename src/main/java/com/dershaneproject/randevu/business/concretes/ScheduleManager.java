package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.ScheduleDao;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemWorkerDao;
import com.dershaneproject.randevu.dataAccess.abstracts.TeacherDao;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.SystemWorkerDto;
import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.SystemWorker;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;

@Service
public class ScheduleManager implements ScheduleService {

	private ModelMapperServiceWithTypeMappingConfigs modelMapperService;
	private ScheduleValidationService scheduleValidationService;
	private ScheduleDao scheduleDao;
	private TeacherDao teacherDao;
	private DayOfWeekDao dayOfWeekDao;
	private HourDao hourDao;
	private SystemWorkerDao systemWorkerDao;

	@Autowired
	public ScheduleManager(ScheduleDao scheduleDao, TeacherDao teacherDao, DayOfWeekDao dayOfWeekDao, HourDao hourDao,
			SystemWorkerDao systemWorkerDao, ModelMapperServiceWithTypeMappingConfigs modelMapperService,
			ScheduleValidationService scheduleValidationService) {
		this.scheduleDao = scheduleDao;
		this.teacherDao = teacherDao;
		this.dayOfWeekDao = dayOfWeekDao;
		this.hourDao = hourDao;
		this.systemWorkerDao = systemWorkerDao;
		this.modelMapperService = modelMapperService;
		this.scheduleValidationService = scheduleValidationService;
	}

	@Override
	public DataResult<ScheduleDto> save(ScheduleDto scheduleDto) {
		// TODO Auto-generated method stub
		try {
			String description = "DEFAULT DESCRİPTİON";
			Result validateResult = scheduleValidationService.isValidateResult(scheduleDto);

			if (validateResult.isSuccess()) {

				Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(scheduleDto.getDayOfWeek().getId());
				Optional<Hour> hour = hourDao.findById(scheduleDto.getHour().getId());
				Optional<SystemWorker> systemWorker = systemWorkerDao.findById(scheduleDto.getLastUpdateDateSystemWorker().getId());

				HourDto hourDto = modelMapperService.forResponse().map(hour.get(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(dayOfWeek.get(), DayOfWeekDto.class);

				Schedule schedule = new Schedule();
				schedule.setFull(scheduleDto.getFull());

				schedule.setLastUpdateDateSystemWorker(systemWorker.get());
				
				// if description is null set default description
				if(scheduleDto.getDescription() == null) {
					scheduleDto.setDescription(description);
				}
				schedule.setDescription(scheduleDto.getDescription());

				Teacher teacher = new Teacher();
				teacher.setId(scheduleDto.getTeacherId());

				schedule.setTeacher(teacher);
				schedule.setDayOfWeek(dayOfWeek.get());
				schedule.setHour(hour.get());

				Schedule scheduleDb = scheduleDao.save(schedule);

				scheduleDto.setId(scheduleDb.getId());
				scheduleDto.setCreateDate(scheduleDb.getCreateDate());
				scheduleDto.setLastUpdateDate(scheduleDb.getLastUpdateDate());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);

				SystemWorkerDto systemWorkerDto = modelMapperService.forResponse().map(
						systemWorker, SystemWorkerDto.class);
				systemWorkerDto.setSchedules(null);
				systemWorkerDto.setWeeklySchedules(null);

                scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				return new DataResult<ScheduleDto>(scheduleDto, true, "Program veritabanına eklendi.");

			} else {

				return new DataResult<ScheduleDto>(false, validateResult.getMessage());
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<List<ScheduleDto>> saveAll(List<ScheduleDto> schedulesDto) {
		// TODO Auto-generated method stub
		
		// Firstly, schedulesDto is validating one by one
		for (ScheduleDto scheduleDto : schedulesDto) {
			Result resultValidation = scheduleValidationService.isValidateResult(scheduleDto);
			if(!(resultValidation.isSuccess())) {
				return new DataResult<List<ScheduleDto>>(false, resultValidation.getMessage());
			}			
		}

		List<Schedule> schedules = new ArrayList<>();

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

		try {
			 String description = "DEFAULT DESCRİPTİON";
				// Schedules will create and add to list
				for (int i = 0; i < schedulesDto.size(); i++) {
					ScheduleDto scheduleDto = schedulesDto.get(i);
					Optional<SystemWorker> systemWorker = Optional.empty();
					SystemWorkerDto systemWorkerDto = null;

					// if systemWorker of current schedule is not null it is translating to dto for response
					if (scheduleDto.getLastUpdateDateSystemWorker() != null) {
						systemWorker = systemWorkerDao.findById(scheduleDto.getLastUpdateDateSystemWorker().getId());

						systemWorkerDto = new SystemWorkerDto();
						systemWorkerDto.setId(systemWorker.get().getId());
						systemWorkerDto.setUserName(systemWorker.get().getUserName());
						systemWorkerDto.setEmail(systemWorker.get().getEmail());
						systemWorkerDto.setPassword(systemWorker.get().getPassword());
						systemWorkerDto.setCreateDate(systemWorker.get().getCreateDate());
						systemWorkerDto.setLastUpdateDate(systemWorker.get().getLastUpdateDate());
						systemWorkerDto.setAuthority(systemWorker.get().getAuthority());

					}
					
					// if description is null set default description
					if(scheduleDto.getDescription() == null) {
						scheduleDto.setDescription(description);
					}

					// finding the objects of scheduleDto with id from Databases
					Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(scheduleDto.getDayOfWeek().getId());
					Optional<Hour> hour = hourDao.findById(scheduleDto.getHour().getId());

					// objects are translating to dto
					HourDto hourDto = modelMapperService.forResponse().map(hour, HourDto.class);
					DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(dayOfWeek, DayOfWeekDto.class);

					scheduleDto.setHour(hourDto);
					scheduleDto.setDayOfWeek(dayOfWeekDto);
					
					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

					Schedule schedule = new Schedule();
					schedule.setFull(scheduleDto.getFull());


					schedule.setDescription(scheduleDto.getDescription());

					// if I get the teacher it is really expensive and unuseful for system
					Teacher teacher = new Teacher();
					teacher.setId(scheduleDto.getTeacherId());
					schedule.setTeacher(teacher);
					
					// objects are setting to schedule
					schedule.setLastUpdateDateSystemWorker(systemWorker.get());
					schedule.setDayOfWeek(dayOfWeek.get());
					schedule.setHour(hour.get());

					// schedule adding to list here
					schedules.add(schedule);
				}
				// Schedules created and added to list

				// Schedule saved and id and dates return to list
			    Date fakeDate = new Date();
				List<Schedule> schedulesDb = scheduleDao.saveAll(schedules);

				// SchedulesDb are sorting here
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
				// SchedulesDb sorted

				// Schedule's id and dates are set for schedulesDto
				for (int i = 0; i < schedulesDb.size(); i++) {
					ScheduleDto scheduleDto = schedulesDto.get(i);

					scheduleDto.setId(schedulesDb.get(i).getId());
					scheduleDto.setCreateDate(fakeDate);
					scheduleDto.setLastUpdateDate(fakeDate);
				}

				// Schedules are sending here
				return new DataResult<List<ScheduleDto>>(schedulesDto, true, "Programlar veritabanına eklendi.");

			

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<ScheduleDto>>(false, e.getMessage());

		}

	}
	
	@Override
	public DataResult<List<ScheduleDto>> saveAllForCreateTeacher(List<ScheduleDto> schedulesDto){
		// TODO Auto-generated method stub
		List<Schedule> schedules = new ArrayList<>();

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

		try {
				// Schedules will create and add to list
				for (int i = 0; i < schedulesDto.size(); i++) {
					ScheduleDto scheduleDto = schedulesDto.get(i);
					Optional<SystemWorker> systemWorker = Optional.empty();
					SystemWorkerDto systemWorkerDto = null;

					// if systemWorker of current schedule is not null it is translating to dto for response
					if (scheduleDto.getLastUpdateDateSystemWorker() != null) {
						systemWorker = systemWorkerDao.findById(scheduleDto.getLastUpdateDateSystemWorker().getId());
						systemWorkerDto = modelMapperService.forResponse().map(systemWorker.get(),
						SystemWorkerDto.class);
					}

					// finding the objects of scheduleDto with id from Databases
					Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(scheduleDto.getDayOfWeek().getId());
					Optional<Hour> hour = hourDao.findById(scheduleDto.getHour().getId());

					// objects are translating to dto
					HourDto hourDto = modelMapperService.forResponse().map(hour, HourDto.class);
					DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(dayOfWeek, DayOfWeekDto.class);

					scheduleDto.setHour(hourDto);
					scheduleDto.setDayOfWeek(dayOfWeekDto);
					
					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

					Schedule schedule = new Schedule();
					schedule.setFull(scheduleDto.getFull());
					schedule.setDescription(scheduleDto.getDescription());

					// if I get the teacher it is really expensive and unuseful for system 
					Teacher teacher = new Teacher();
					teacher.setId(scheduleDto.getTeacherId());
					schedule.setTeacher(teacher);
					
					// objects are setting to schedule 
					schedule.setLastUpdateDateSystemWorker(systemWorker.get());
					schedule.setDayOfWeek(dayOfWeek.get());
					schedule.setHour(hour.get());

					// schedule adding to list here
					schedules.add(schedule);
				}
				// Schedules created and added to list

				// Schedule's id and dates return to list
				Date scheduleFakeDate = new Date(); // saveAll don't return dates
				List<Schedule> schedulesDb = scheduleDao.saveAll(schedules);

				// Schedules are sorting here
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
				// Schedules sorted

				// Schedule's id and dates are set for schedulesDto
				int schedulesDblength = schedulesDb.size();
				for (int i = 0; i < schedulesDblength; i++) {
					ScheduleDto scheduleDto = schedulesDto.get(i);

					scheduleDto.setId(schedulesDb.get(i).getId());
					scheduleDto.setCreateDate(scheduleFakeDate);
					scheduleDto.setLastUpdateDate(scheduleFakeDate);
				}

				// Schedules are saving here
				return new DataResult<List<ScheduleDto>>(schedulesDto, true, "Programlar veritabanına eklendi.");

			

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<ScheduleDto>>(false, e.getMessage());

		}

	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			if (!(schedule.equals(Optional.empty()))) {
				scheduleDao.deleteById(id);
				return new Result(true, id + " id'li program silindi.");
			}

			return new Result(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<ScheduleDto>> findAll() {
		// TODO Auto-generated method stub
			List<Schedule> schedules = scheduleDao.findAll();

			if (schedules.size() != 0) {

				List<ScheduleDto> schedulesDto = new ArrayList<ScheduleDto>();

				for(Schedule schedule:schedules) {
					SystemWorker lastUpdateDateSystemWorker = schedule.getLastUpdateDateSystemWorker();

					HourDto hourDto = modelMapperService.forResponse().map(schedule.getHour(), HourDto.class);
					DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.getDayOfWeek(),
							DayOfWeekDto.class);

					ScheduleDto scheduleDto = new ScheduleDto();

					scheduleDto.setId(schedule.getId());
					scheduleDto.setFull(schedule.getFull());
					scheduleDto.setDescription(schedule.getDescription());
					scheduleDto.setTeacherId(schedule.getTeacher().getId());
					scheduleDto.setCreateDate(schedule.getCreateDate());
					scheduleDto.setLastUpdateDate(schedule.getLastUpdateDate());
					scheduleDto.setDayOfWeek(dayOfWeekDto);
					scheduleDto.setHour(hourDto);

					if (lastUpdateDateSystemWorker == null) {
						scheduleDto.setLastUpdateDateSystemWorker(null);

					} else {
						// if I use the mapper it get schedules PERFORMANCE PROBLEM
						SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
						systemWorkerDto.setId(schedule.getLastUpdateDateSystemWorker().getId());
						systemWorkerDto.setUserName(schedule.getLastUpdateDateSystemWorker().getUserName());
						systemWorkerDto.setEmail(schedule.getLastUpdateDateSystemWorker().getEmail());
						systemWorkerDto.setPassword(schedule.getLastUpdateDateSystemWorker().getPassword());
						systemWorkerDto.setCreateDate(schedule.getLastUpdateDateSystemWorker().getCreateDate());
						systemWorkerDto.setLastUpdateDate(schedule.getLastUpdateDateSystemWorker().getLastUpdateDate());
						systemWorkerDto.setAuthority(schedule.getLastUpdateDateSystemWorker().getAuthority());

						scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

					}

					schedulesDto.add(scheduleDto);
				}

				return new DataResult<List<ScheduleDto>>(schedulesDto, true, "Programlar getirildi.");

			} else {

				return new DataResult<List<ScheduleDto>>(false, "Program bulunamadı.");
			}
	}

	@Override
	public DataResult<ScheduleDto> findById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);

			if (!(schedule.equals(Optional.empty()))) {
				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(),
						DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());
				scheduleDto.setDescription(schedule.get().getDescription());

				if (lastUpdateDateSystemWorker == null) {
					scheduleDto.setLastUpdateDateSystemWorker(null);

				} else {

					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(schedule.get().getLastUpdateDateSystemWorker().getId());
					systemWorkerDto.setUserName(schedule.get().getLastUpdateDateSystemWorker().getUserName());
					systemWorkerDto.setEmail(schedule.get().getLastUpdateDateSystemWorker().getEmail());
					systemWorkerDto.setPassword(schedule.get().getLastUpdateDateSystemWorker().getPassword());
					systemWorkerDto.setCreateDate(schedule.get().getLastUpdateDateSystemWorker().getCreateDate());
					systemWorkerDto.setLastUpdateDate(schedule.get().getLastUpdateDateSystemWorker().getLastUpdateDate());
					systemWorkerDto.setAuthority(schedule.get().getLastUpdateDateSystemWorker().getAuthority());

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li program getirildi.");
			}
			return new DataResult<ScheduleDto>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateFullById(long id, Boolean full) {
		// TODO Auto-generated method stub
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);

			if (!(schedule.equals(Optional.empty()))) {
				schedule.get().setFull(full);
				scheduleDao.save(schedule.get());

				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(),
						DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();

				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());
				scheduleDto.setDescription(schedule.get().getDescription());

				if (lastUpdateDateSystemWorker == null) {
					scheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(lastUpdateDateSystemWorker.getId());
					systemWorkerDto.setUserName(lastUpdateDateSystemWorker.getUserName());
					systemWorkerDto.setEmail(lastUpdateDateSystemWorker.getEmail());
					systemWorkerDto.setPassword(lastUpdateDateSystemWorker.getPassword());
					systemWorkerDto.setCreateDate(lastUpdateDateSystemWorker.getCreateDate());
					systemWorkerDto.setLastUpdateDate(lastUpdateDateSystemWorker.getLastUpdateDate());
					systemWorkerDto.setAuthority(lastUpdateDateSystemWorker.getAuthority());

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın doluluğu güncellendi.");
			}
			return new DataResult<ScheduleDto>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<ScheduleDto> updateTeacherById(long id, Long teacherId) {
		// TODO Auto-generated method stub
		try {
			if(teacherId == null){
				return new DataResult<ScheduleDto>(false, "Öğretmen Boş olamaz.");
			}
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<Teacher> teacher = teacherDao.findById(teacherId);

			if (!(schedule.equals(Optional.empty())) && !(teacher.equals(Optional.empty()))) {
				schedule.get().setTeacher(teacher.get());
				scheduleDao.save(schedule.get());

				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(),
						DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();

				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());
				scheduleDto.setDescription(schedule.get().getDescription());

				if (lastUpdateDateSystemWorker == null) {
					scheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(lastUpdateDateSystemWorker.getId());
					systemWorkerDto.setUserName(lastUpdateDateSystemWorker.getUserName());
					systemWorkerDto.setEmail(lastUpdateDateSystemWorker.getEmail());
					systemWorkerDto.setPassword(lastUpdateDateSystemWorker.getPassword());
					systemWorkerDto.setCreateDate(lastUpdateDateSystemWorker.getCreateDate());
					systemWorkerDto.setLastUpdateDate(lastUpdateDateSystemWorker.getLastUpdateDate());
					systemWorkerDto.setAuthority(lastUpdateDateSystemWorker.getAuthority());

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın öğretmeni güncellendi.");
			} else {
				if (schedule.equals(Optional.empty())) {
					return new DataResult<ScheduleDto>(false, id + " id'li program bulunamadı.");

				}
			}
			return new DataResult<ScheduleDto>(false,
					id + " id'li program için verdiğiniz öğretmen id'sini kontrol ediniz.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<ScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId) {
		// TODO Auto-generated method stub

		if (lastUpdateDateSystemWorkerId == null){
			return new DataResult<ScheduleDto>(false, "Son güncelleme yapan sistem çalışanı boş bırakılamaz.");
		}
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<SystemWorker> systemWorker = systemWorkerDao.findById(lastUpdateDateSystemWorkerId);

			if (!(schedule.equals(Optional.empty())) && !(systemWorker.equals(Optional.empty()))) {
				schedule.get().setLastUpdateDateSystemWorker(systemWorker.get());
				scheduleDao.save(schedule.get());

				SystemWorker lastUpdateDateSystemWorkerCurrent = schedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(),
						DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());
				scheduleDto.setDescription(schedule.get().getDescription());

				if (lastUpdateDateSystemWorkerCurrent == null) {
					scheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(lastUpdateDateSystemWorkerCurrent.getId());
					systemWorkerDto.setUserName(lastUpdateDateSystemWorkerCurrent.getUserName());
					systemWorkerDto.setEmail(lastUpdateDateSystemWorkerCurrent.getEmail());
					systemWorkerDto.setPassword(lastUpdateDateSystemWorkerCurrent.getPassword());
					systemWorkerDto.setCreateDate(lastUpdateDateSystemWorkerCurrent.getCreateDate());
					systemWorkerDto.setLastUpdateDate(lastUpdateDateSystemWorkerCurrent.getLastUpdateDate());
					systemWorkerDto.setAuthority(lastUpdateDateSystemWorkerCurrent.getAuthority());

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}
				return new DataResult<ScheduleDto>(scheduleDto, true,
						id + " id'li programın üstünde son değişilik yapan sistem çalışanı güncellendi.");
			} else {
				if (schedule.equals(Optional.empty())) {
					return new DataResult<ScheduleDto>(false, id + " id'li program bulunamadı.");

				}
			}

			return new DataResult<ScheduleDto>(false,
					id + " id'li program için verdiğiiz sistem çalışanını kontrol ediniz.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) {
		// TODO Auto-generated method stub

		if (dayOfWeekId == null) {
			return new DataResult<ScheduleDto>(false, "Gün boş bırakılamaz.");
		}
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(dayOfWeekId);

			if (!(schedule.equals(Optional.empty())) && !(dayOfWeek.equals(Optional.empty()))) {
				schedule.get().setDayOfWeek(dayOfWeek.get());
				scheduleDao.save(schedule.get());

				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(),
						DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());
				scheduleDto.setDescription(schedule.get().getDescription());

				if (lastUpdateDateSystemWorker == null) {
					scheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(lastUpdateDateSystemWorker.getId());
					systemWorkerDto.setUserName(lastUpdateDateSystemWorker.getUserName());
					systemWorkerDto.setEmail(lastUpdateDateSystemWorker.getEmail());
					systemWorkerDto.setPassword(lastUpdateDateSystemWorker.getPassword());
					systemWorkerDto.setCreateDate(lastUpdateDateSystemWorker.getCreateDate());
					systemWorkerDto.setLastUpdateDate(lastUpdateDateSystemWorker.getLastUpdateDate());
					systemWorkerDto.setAuthority(lastUpdateDateSystemWorker.getAuthority());

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın günü güncellendi.");
			} else {
				if (schedule.equals(Optional.empty())) {
					return new DataResult<ScheduleDto>(false, id + " id'li program bulunamadı.");

				}
			}
			return new DataResult<ScheduleDto>(false,
					id + " id'li program için verdiğiniz gün id'sini kontrol ediniz.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateHourById(long id, Long hourId) {
		// TODO Auto-generated method stub

		if (hourId == null) {
			return new DataResult<ScheduleDto>(Boolean.FALSE, "Saat boş bırakılamaz.");
		}

		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<Hour> hour = hourDao.findById(hourId);

			if (!(schedule.equals(Optional.empty())) && !(hour.equals(Optional.empty()))) {
				schedule.get().setHour(hour.get());
				scheduleDao.save(schedule.get());

				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(),
						DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());
				scheduleDto.setDescription(schedule.get().getDescription());

				if (lastUpdateDateSystemWorker == null) {
					scheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(lastUpdateDateSystemWorker.getId());
					systemWorkerDto.setUserName(lastUpdateDateSystemWorker.getUserName());
					systemWorkerDto.setEmail(lastUpdateDateSystemWorker.getEmail());
					systemWorkerDto.setPassword(lastUpdateDateSystemWorker.getPassword());
					systemWorkerDto.setCreateDate(lastUpdateDateSystemWorker.getCreateDate());
					systemWorkerDto.setLastUpdateDate(lastUpdateDateSystemWorker.getLastUpdateDate());
					systemWorkerDto.setAuthority(lastUpdateDateSystemWorker.getAuthority());

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın saati güncellendi.");
			} else {
				if (schedule.equals(Optional.empty())) {
					return new DataResult<ScheduleDto>(false, id + " id'li program bulunamadı.");

				}
			}
			return new DataResult<ScheduleDto>(false,
					id + " id'li program için verdiğiniz saat id'sini kontrol ediniz.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateDescriptionById(long id, String description) {
		// TODO Auto-generated method stub
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);

			if (!(schedule.equals(Optional.empty()))) {
				schedule.get().setDescription(description);
				scheduleDao.save(schedule.get());

				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(),
						DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());
				scheduleDto.setDescription(schedule.get().getDescription());

				if (lastUpdateDateSystemWorker == null) {
					scheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					SystemWorkerDto systemWorkerDto = new SystemWorkerDto();
					systemWorkerDto.setId(lastUpdateDateSystemWorker.getId());
					systemWorkerDto.setUserName(lastUpdateDateSystemWorker.getUserName());
					systemWorkerDto.setEmail(lastUpdateDateSystemWorker.getEmail());
					systemWorkerDto.setPassword(lastUpdateDateSystemWorker.getPassword());
					systemWorkerDto.setCreateDate(lastUpdateDateSystemWorker.getCreateDate());
					systemWorkerDto.setLastUpdateDate(lastUpdateDateSystemWorker.getLastUpdateDate());
					systemWorkerDto.setAuthority(lastUpdateDateSystemWorker.getAuthority());

					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın açıklaması güncellendi.");
			}
			return new DataResult<ScheduleDto>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		return new DataResult<Long>(scheduleDao.count(), true, "Programların sayısı getirildi.");
	}

}

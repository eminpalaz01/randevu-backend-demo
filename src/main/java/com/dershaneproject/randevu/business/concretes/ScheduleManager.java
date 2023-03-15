package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.Collections;
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

				Optional<Teacher> teacher = teacherDao.findById(scheduleDto.getTeacherId());
				Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(scheduleDto.getDayOfWeek().getId());
				Optional<Hour> hour = hourDao.findById(scheduleDto.getHour().getId());

				Schedule schedule = new Schedule();
				schedule.setFull(false);
				schedule.setLastUpdateDateSystemWorker(null);
				
				// if description is null set default description
				if(scheduleDto.getDescription() == null) {
					scheduleDto.setDescription(description);
				}
				schedule.setDescription(scheduleDto.getDescription());

				schedule.setTeacher(teacher.get());
				schedule.setDayOfWeek(dayOfWeek.get());
				schedule.setHour(hour.get());

				Schedule scheduleDb = scheduleDao.save(schedule);

				scheduleDto.setId(scheduleDb.getId());
				scheduleDto.setCreateDate(scheduleDb.getCreateDate());
				scheduleDto.setLastUpdateDate(scheduleDb.getLastUpdateDate());

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
		Collections.sort(schedulesDto, (s1, s2) -> {
			Integer s1DayOfWeekId = (int) (s1.getDayOfWeek().getId());
			int dayOfWeekCompare = s1DayOfWeekId.compareTo((int) (s2.getDayOfWeek().getId()));

			if (dayOfWeekCompare == 0) {
				Integer s1HourId = (int) (s1.getHour().getId());
				int hourCompare = s1HourId.compareTo((int) (s2.getHour().getId()));
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
						systemWorkerDto = modelMapperService.forResponse().map(systemWorker.get(),
								SystemWorkerDto.class);
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
					schedule.getTeacher().setId(scheduleDto.getTeacherId());
					
					// objects are setting to schedule 
					schedule.setLastUpdateDateSystemWorker(systemWorker.get());
					schedule.setDayOfWeek(dayOfWeek.get());
					schedule.setHour(hour.get());

					// schedule adding to list here
					schedules.add(schedule);
				}
				// Schedules created and added to list

				// Schedule's id and dates return to list
				List<Schedule> schedulesDb = scheduleDao.saveAll(schedules);

				// Schedules are sorting here
				Collections.sort(schedulesDb, (o1, o2) -> {
					Integer s1DayOfWeekId = (int) (o1.getDayOfWeek().getId());
					int dayOfWeekCompare = s1DayOfWeekId.compareTo((int) (o2.getDayOfWeek().getId()));

					if (dayOfWeekCompare == 0) {
						Integer s1HourId = (int) (o1.getHour().getId());
						int hourCompare = s1HourId.compareTo((int) (o2.getHour().getId()));
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
					scheduleDto.setCreateDate(schedulesDb.get(i).getCreateDate());
					scheduleDto.setLastUpdateDate(schedulesDb.get(i).getLastUpdateDate());
				}

				// Schedules are saving here
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
		Collections.sort(schedulesDto, (s1, s2) -> {
			Integer s1DayOfWeekId = (int) (s1.getDayOfWeek().getId());
			int dayOfWeekCompare = s1DayOfWeekId.compareTo((int) (s2.getDayOfWeek().getId()));

			if (dayOfWeekCompare == 0) {
				Integer s1HourId = (int) (s1.getHour().getId());
				int hourCompare = s1HourId.compareTo((int) (s2.getHour().getId()));
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
				List<Schedule> schedulesDb = scheduleDao.saveAll(schedules);

				// Schedules are sorting here
				Collections.sort(schedulesDb, (o1, o2) -> {
					Integer s1DayOfWeekId = (int) (o1.getDayOfWeek().getId());
					int dayOfWeekCompare = s1DayOfWeekId.compareTo((int) (o2.getDayOfWeek().getId()));

					if (dayOfWeekCompare == 0) {
						Integer s1HourId = (int) (o1.getHour().getId());
						int hourCompare = s1HourId.compareTo((int) (o2.getHour().getId()));
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
					scheduleDto.setCreateDate(schedulesDb.get(i).getCreateDate());
					scheduleDto.setLastUpdateDate(schedulesDb.get(i).getLastUpdateDate());
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
		// TODO Auto-generated method stub
		try {
			List<Schedule> schedules = scheduleDao.findAll();
			if (schedules.size() != 0) {
				List<ScheduleDto> schedulesDto = new ArrayList<ScheduleDto>();

				schedules.forEach(schedule -> {
					SystemWorker lastUpdateDateSystemWorker = schedule.getLastUpdateDateSystemWorker();

					HourDto hourDto = modelMapperService.forResponse().map(schedule.getHour(), HourDto.class);
					DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.getDayOfWeek(),
							DayOfWeekDto.class);

					ScheduleDto scheduleDto = new ScheduleDto();
					scheduleDto.setId(schedule.getId());
					scheduleDto.setTeacherId(schedule.getTeacher().getId());
					scheduleDto.setDayOfWeek(dayOfWeekDto);
					scheduleDto.setHour(hourDto);
					scheduleDto.setFull(schedule.getFull());
					scheduleDto.setCreateDate(schedule.getCreateDate());
					scheduleDto.setLastUpdateDate(schedule.getLastUpdateDate());
					scheduleDto.setDescription(schedule.getDescription());

					if (lastUpdateDateSystemWorker == null) {
						scheduleDto.setLastUpdateDateSystemWorker(null);

					} else {
						SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
								.map(schedule.getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
						scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

					}

					schedulesDto.add(scheduleDto);
				});

				return new DataResult<List<ScheduleDto>>(schedulesDto, true, "Programlar getirildi.");

			} else {

				return new DataResult<List<ScheduleDto>>(false, "Program bulunamadı.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<ScheduleDto>>(false, e.getMessage());
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(schedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(schedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
	public DataResult<ScheduleDto> updateTeacherById(long id, long teacherId) {
		// TODO Auto-generated method stub
		try {
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(schedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
	public DataResult<ScheduleDto> updateLastUpdateDateSystemWorkerById(long id, long lastUpdateDateSystemWorkerId) {
		// TODO Auto-generated method stub
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(lastUpdateDateSystemWorkerCurrent, SystemWorkerDto.class);
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
	public DataResult<ScheduleDto> updateDayOfWeekById(long id, long dayOfWeekId) {
		// TODO Auto-generated method stub
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(schedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
	public DataResult<ScheduleDto> updateHourById(long id, long hourId) {
		// TODO Auto-generated method stub
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(schedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(schedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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

package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.SystemAdministratorService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemAdministratorDao;
import com.dershaneproject.randevu.dto.*;
import com.dershaneproject.randevu.dto.requests.SystemAdministratorSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemAdministratorSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.SystemAdministrator;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemAdministratorManager implements SystemAdministratorService {

	private final SystemAdministratorDao systemAdministratorDao;
	private final ModelMapperServiceWithTypeMappingConfigs modelMapperService;

	@Override
	public DataResult<SystemAdministratorSaveResponse> save(SystemAdministratorSaveRequest systemAdministratorSaveRequest) {
		try {
			SystemAdministrator systemAdministrator = systemAdministratorDao.save(createSystemAdministratorForSave(systemAdministratorSaveRequest));
			SystemAdministratorSaveResponse systemAdministratorSaveResponse = modelMapperService.forResponse()
					.map(systemAdministrator, SystemAdministratorSaveResponse.class);
			return new DataResult<SystemAdministratorSaveResponse>(systemAdministratorSaveResponse, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorSaveResponse>(false, e.getMessage());
		}

	}

	private SystemAdministrator createSystemAdministratorForSave(SystemAdministratorSaveRequest systemAdministratorSaveRequest) {
		SystemAdministrator systemAdministrator = new SystemAdministrator();
		systemAdministrator.setUserName(systemAdministratorSaveRequest.getUserName());
		systemAdministrator.setPassword(systemAdministratorSaveRequest.getPassword());
		systemAdministrator.setEmail(systemAdministratorSaveRequest.getEmail());
		return systemAdministrator;
	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);
			if (!(systemAdministrator.equals(Optional.empty()))) {
				systemAdministratorDao.deleteById(id);
				return new Result(true, id + " id'li sistem yöneticisi silindi.");
			}

			return new Result(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}
	}



	@Override
	public DataResult<SystemAdministratorDto> findById(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

				systemAdministratorSaveRequest.setId(systemAdministrator.get().getId());
				systemAdministratorSaveRequest.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorSaveRequest.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorSaveRequest.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorSaveRequest.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorSaveRequest.setAuthority(systemAdministrator.get().getAuthority());

				return new DataResult<SystemAdministratorDto>(systemAdministratorSaveRequest, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}	
		
	@Override
	public DataResult<SystemAdministratorDto> findByIdWithAllSchedules(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

				List<Schedule> schedules = systemAdministrator.get().getSchedules();
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

					schedulesDto.add(scheduleDto);});

				List<WeeklySchedule> weeklySchedules = systemAdministrator.get().getWeeklySchedules();
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
				weeklySchedules.forEach(weeklySchedule -> {
					WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();

					HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class);
					DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class);

					weeklyScheduleDto.setId(weeklySchedule.getId());
					weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
					weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
					weeklyScheduleDto.setHour(hourDto);
					weeklyScheduleDto.setFull(weeklySchedule.getFull());
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

					if(weeklySchedule.getStudent() == null){
						weeklyScheduleDto.setStudentId(null);
					}else{
						weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());
					}

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
					weeklySchedulesDto.add(weeklyScheduleDto);
				});
				
				systemAdministratorSaveRequest.setId(systemAdministrator.get().getId());
				systemAdministratorSaveRequest.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorSaveRequest.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorSaveRequest.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorSaveRequest.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorSaveRequest.setAuthority(systemAdministrator.get().getAuthority());
				systemAdministratorSaveRequest.setSchedules(schedulesDto);
				systemAdministratorSaveRequest.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorSaveRequest, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> findByIdWithWeeklySchedules(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

				List<WeeklySchedule> weeklySchedules = systemAdministrator.get().getWeeklySchedules();
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
				weeklySchedules.forEach(weeklySchedule -> {
					WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();

					HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class);
					DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class);

					weeklyScheduleDto.setId(weeklySchedule.getId());
					weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
					weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
					weeklyScheduleDto.setHour(hourDto);
					weeklyScheduleDto.setFull(weeklySchedule.getFull());
					weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
					weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
					weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

					if(weeklySchedule.getStudent() == null){
						weeklyScheduleDto.setStudentId(null);
					}else{
						weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());
					}

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
					weeklySchedulesDto.add(weeklyScheduleDto);
				});
				
				systemAdministratorSaveRequest.setId(systemAdministrator.get().getId());
				systemAdministratorSaveRequest.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorSaveRequest.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorSaveRequest.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorSaveRequest.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorSaveRequest.setAuthority(systemAdministrator.get().getAuthority());
				systemAdministratorSaveRequest.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorSaveRequest, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<SystemAdministratorDto> findByIdWithSchedules(long id) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

				List<Schedule> schedules = systemAdministrator.get().getSchedules();
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

					schedulesDto.add(scheduleDto);});
				
				systemAdministratorSaveRequest.setId(systemAdministrator.get().getId());
				systemAdministratorSaveRequest.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorSaveRequest.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorSaveRequest.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorSaveRequest.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorSaveRequest.setAuthority(systemAdministrator.get().getAuthority());
				systemAdministratorSaveRequest.setSchedules(schedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorSaveRequest, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemAdministratorDto>> findAll() {
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (systemAdministrators.size() != 0) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

					systemAdministratorSaveRequest.setId(systemAdministrator.getId());
					systemAdministratorSaveRequest.setUserName(systemAdministrator.getUserName());
					systemAdministratorSaveRequest.setPassword(systemAdministrator.getPassword());
					systemAdministratorSaveRequest.setEmail(systemAdministrator.getEmail());
					systemAdministratorSaveRequest.setAuthority(systemAdministrator.getAuthority());
					systemAdministratorSaveRequest.setCreateDate(systemAdministrator.getCreateDate());
					systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.getLastUpdateDate());

					systemAdministratorsDto.add(systemAdministratorSaveRequest);
				});

				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");

			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithSchedules() {
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (systemAdministrators.size() != 0) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

					List<Schedule> schedules = systemAdministrator.getSchedules();
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

						schedulesDto.add(scheduleDto);});
					
					systemAdministratorSaveRequest.setId(systemAdministrator.getId());
					systemAdministratorSaveRequest.setUserName(systemAdministrator.getUserName());
					systemAdministratorSaveRequest.setPassword(systemAdministrator.getPassword());
					systemAdministratorSaveRequest.setEmail(systemAdministrator.getEmail());
					systemAdministratorSaveRequest.setAuthority(systemAdministrator.getAuthority());
					systemAdministratorSaveRequest.setCreateDate(systemAdministrator.getCreateDate());
					systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.getLastUpdateDate());
					systemAdministratorSaveRequest.setSchedules(schedulesDto);

					systemAdministratorsDto.add(systemAdministratorSaveRequest);
				});

				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");

			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}
		
	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithAllSchedules() {
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (systemAdministrators.size() != 0) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

					List<Schedule> schedules = systemAdministrator.getSchedules();
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

						schedulesDto.add(scheduleDto);});

					List<WeeklySchedule> weeklySchedules = systemAdministrator.getWeeklySchedules();
					List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
					weeklySchedules.forEach(weeklySchedule -> {
						WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();

						HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class);
						DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class);

						weeklyScheduleDto.setId(weeklySchedule.getId());
						weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
						weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
						weeklyScheduleDto.setHour(hourDto);
						weeklyScheduleDto.setFull(weeklySchedule.getFull());
						weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
						weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
						weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

						if(weeklySchedule.getStudent() == null){
							weeklyScheduleDto.setStudentId(null);
						}else{
							weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());
						}

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
						weeklySchedulesDto.add(weeklyScheduleDto);
					});
					
					systemAdministratorSaveRequest.setId(systemAdministrator.getId());
					systemAdministratorSaveRequest.setUserName(systemAdministrator.getUserName());
					systemAdministratorSaveRequest.setPassword(systemAdministrator.getPassword());
					systemAdministratorSaveRequest.setEmail(systemAdministrator.getEmail());
					systemAdministratorSaveRequest.setAuthority(systemAdministrator.getAuthority());
					systemAdministratorSaveRequest.setCreateDate(systemAdministrator.getCreateDate());
					systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.getLastUpdateDate());
					systemAdministratorSaveRequest.setSchedules(schedulesDto);
					systemAdministratorSaveRequest.setWeeklySchedules(weeklySchedulesDto);

					systemAdministratorsDto.add(systemAdministratorSaveRequest);
				});

				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");

			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithWeeklySchedules() {
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (systemAdministrators.size() != 0) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

					List<WeeklySchedule> weeklySchedules = systemAdministrator.getWeeklySchedules();
					List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
					weeklySchedules.forEach(weeklySchedule -> {
						WeeklyScheduleDto weeklyScheduleDto = new WeeklyScheduleDto();

						HourDto hourDto = modelMapperService.forResponse().map(weeklySchedule.getHour(), HourDto.class);
						DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(weeklySchedule.getDayOfWeek(), DayOfWeekDto.class);

						weeklyScheduleDto.setId(weeklySchedule.getId());
						weeklyScheduleDto.setTeacherId(weeklySchedule.getTeacher().getId());
						weeklyScheduleDto.setDayOfWeek(dayOfWeekDto);
						weeklyScheduleDto.setHour(hourDto);
						weeklyScheduleDto.setFull(weeklySchedule.getFull());
						weeklyScheduleDto.setCreateDate(weeklySchedule.getCreateDate());
						weeklyScheduleDto.setLastUpdateDate(weeklySchedule.getLastUpdateDate());
						weeklyScheduleDto.setDescription(weeklySchedule.getDescription());

						if(weeklySchedule.getStudent() == null){
							weeklyScheduleDto.setStudentId(null);
						}else{
							weeklyScheduleDto.setStudentId(weeklySchedule.getStudent().getId());
						}

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
						weeklySchedulesDto.add(weeklyScheduleDto);
					});
					
					systemAdministratorSaveRequest.setId(systemAdministrator.getId());
					systemAdministratorSaveRequest.setUserName(systemAdministrator.getUserName());
					systemAdministratorSaveRequest.setPassword(systemAdministrator.getPassword());
					systemAdministratorSaveRequest.setEmail(systemAdministrator.getEmail());
					systemAdministratorSaveRequest.setAuthority(systemAdministrator.getAuthority());
					systemAdministratorSaveRequest.setCreateDate(systemAdministrator.getCreateDate());
					systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.getLastUpdateDate());
					systemAdministratorSaveRequest.setWeeklySchedules(weeklySchedulesDto);

					systemAdministratorsDto.add(systemAdministratorSaveRequest);
				});

				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");

			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updateUserNameById(long id, String userName) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

				systemAdministrator.get().setUserName(userName);

				systemAdministratorDao.save(systemAdministrator.get());

				systemAdministratorSaveRequest.setId(systemAdministrator.get().getId());
				systemAdministratorSaveRequest.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorSaveRequest.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorSaveRequest.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorSaveRequest.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorSaveRequest.setAuthority(systemAdministrator.get().getAuthority());

				return new DataResult<SystemAdministratorDto>(systemAdministratorSaveRequest, true,
						id + " id'li sistem yöneticisinin kullanıcı adı güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updatePasswordById(long id, String password) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

				systemAdministrator.get().setPassword(password);

				systemAdministratorDao.save(systemAdministrator.get());

				systemAdministratorSaveRequest.setId(systemAdministrator.get().getId());
				systemAdministratorSaveRequest.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorSaveRequest.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorSaveRequest.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorSaveRequest.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorSaveRequest.setAuthority(systemAdministrator.get().getAuthority());

				return new DataResult<SystemAdministratorDto>(systemAdministratorSaveRequest, true,
						id + " id'li sistem yöneticisinin şifresi güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updateEmailById(long id, String email) {
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorSaveRequest = new SystemAdministratorDto();

				systemAdministrator.get().setEmail(email);

				systemAdministratorDao.save(systemAdministrator.get());

				systemAdministratorSaveRequest.setId(systemAdministrator.get().getId());
				systemAdministratorSaveRequest.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorSaveRequest.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorSaveRequest.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorSaveRequest.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorSaveRequest.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorSaveRequest.setAuthority(systemAdministrator.get().getAuthority());

				return new DataResult<SystemAdministratorDto>(systemAdministratorSaveRequest, true,
						id + " id'li sistem yöneticisinin maili güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		try {
			return new DataResult<Long>(systemAdministratorDao.count(), true, "Sistem yöneticilerinin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}	
	}


}

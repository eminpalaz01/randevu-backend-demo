package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.SystemStaffService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemStaffDao;
import com.dershaneproject.randevu.dto.*;
import com.dershaneproject.randevu.dto.requests.SystemStaffSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemStaffSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.SystemStaff;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemStaffManager implements SystemStaffService {

	private final  SystemStaffDao systemStaffDao;
	private final  ModelMapperServiceWithTypeMappingConfigs modelMapperService;

	@Override
	public DataResult<SystemStaffSaveResponse> save(SystemStaffSaveRequest systemStaffSaveRequest) {
		try {
			SystemStaff systemStaff = systemStaffDao.save(createSystemStaffForSave(systemStaffSaveRequest));
			SystemStaffSaveResponse systemStaffSaveResponse = modelMapperService.forResponse().map(systemStaff, SystemStaffSaveResponse.class);

			return new DataResult<SystemStaffSaveResponse>(systemStaffSaveResponse, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			return new DataResult<SystemStaffSaveResponse>(false, e.getMessage());
		}
	}

	private SystemStaff createSystemStaffForSave(SystemStaffSaveRequest systemStaffDto) {
		SystemStaff systemStaff = new SystemStaff();
		systemStaff.setUserName(systemStaffDto.getUserName());
		systemStaff.setPassword(systemStaffDto.getPassword());
		systemStaff.setEmail(systemStaffDto.getEmail());
		return systemStaff;
	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);
			if (!(systemStaff.equals(Optional.empty()))) {
				systemStaffDao.deleteById(id);
				return new Result(true, id + " id'li sistem çalışanı silindi.");
			}

			return new Result(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> findById(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithAllSchedules(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();
				
				List<Schedule> schedules = systemStaff.get().getSchedules();
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

				List<WeeklySchedule> weeklySchedules = systemStaff.get().getWeeklySchedules();
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

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());
				systemStaffDto.setSchedules(schedulesDto);
				systemStaffDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithWeeklySchedules(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				List<WeeklySchedule> weeklySchedules = systemStaff.get().getWeeklySchedules();
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

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());
				systemStaffDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> findByIdWithSchedules(long id) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();
				
				List<Schedule> schedules = systemStaff.get().getSchedules();
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

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());
				systemStaffDto.setSchedules(schedulesDto);

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAll() {
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (systemStaffs.size() != 0) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = new SystemStaffDto();

					systemStaffDto.setId(systemStaff.getId());
					systemStaffDto.setUserName(systemStaff.getUserName());
					systemStaffDto.setPassword(systemStaff.getPassword());
					systemStaffDto.setEmail(systemStaff.getEmail());
					systemStaffDto.setAuthority(systemStaff.getAuthority());
					systemStaffDto.setCreateDate(systemStaff.getCreateDate());
					systemStaffDto.setLastUpdateDate(systemStaff.getLastUpdateDate());

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithAllSchedules() {
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (systemStaffs.size() != 0) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = new SystemStaffDto();

					List<Schedule> schedules = systemStaff.getSchedules();
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

					List<WeeklySchedule> weeklySchedules = systemStaff.getWeeklySchedules();
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

					systemStaffDto.setId(systemStaff.getId());
					systemStaffDto.setUserName(systemStaff.getUserName());
					systemStaffDto.setPassword(systemStaff.getPassword());
					systemStaffDto.setEmail(systemStaff.getEmail());
					systemStaffDto.setAuthority(systemStaff.getAuthority());
					systemStaffDto.setCreateDate(systemStaff.getCreateDate());
					systemStaffDto.setLastUpdateDate(systemStaff.getLastUpdateDate());
					systemStaffDto.setSchedules(schedulesDto);
					systemStaffDto.setWeeklySchedules(weeklySchedulesDto);

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithSchedules() {
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (systemStaffs.size() != 0) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = new SystemStaffDto();

					List<Schedule> schedules = systemStaff.getSchedules();
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

					systemStaffDto.setId(systemStaff.getId());
					systemStaffDto.setUserName(systemStaff.getUserName());
					systemStaffDto.setPassword(systemStaff.getPassword());
					systemStaffDto.setEmail(systemStaff.getEmail());
					systemStaffDto.setAuthority(systemStaff.getAuthority());
					systemStaffDto.setCreateDate(systemStaff.getCreateDate());
					systemStaffDto.setLastUpdateDate(systemStaff.getLastUpdateDate());
					systemStaffDto.setSchedules(schedulesDto);

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAllWithWeeklySchedules() {
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (systemStaffs.size() != 0) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = new SystemStaffDto();

					List<WeeklySchedule> weeklySchedules = systemStaff.getWeeklySchedules();
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

					systemStaffDto.setId(systemStaff.getId());
					systemStaffDto.setUserName(systemStaff.getUserName());
					systemStaffDto.setPassword(systemStaff.getPassword());
					systemStaffDto.setEmail(systemStaff.getEmail());
					systemStaffDto.setAuthority(systemStaff.getAuthority());
					systemStaffDto.setCreateDate(systemStaff.getCreateDate());
					systemStaffDto.setLastUpdateDate(systemStaff.getLastUpdateDate());
					systemStaffDto.setWeeklySchedules(weeklySchedulesDto);

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updateUserNameById(long id, String userName) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaff.get().setUserName(userName);

				systemStaffDao.save(systemStaff.get());

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());

				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının kullanıcı adı güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updatePasswordById(long id, String password) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaff.get().setPassword(password);

				systemStaffDao.save(systemStaff.get());

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());

				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının şifresi güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updateEmailById(long id, String email) {
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaff.get().setEmail(email);

				systemStaffDao.save(systemStaff.get());

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());

				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının maili güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		try {
			return new DataResult<Long>(systemStaffDao.count(), true, "Sistem çalışanlarının sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}

}

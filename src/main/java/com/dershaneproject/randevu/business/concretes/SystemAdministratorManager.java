package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.SystemAdministratorService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemAdministratorDao;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.SystemAdministrator;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;

@Service
public class SystemAdministratorManager implements SystemAdministratorService {

	private SystemAdministratorDao systemAdministratorDao;
	private ModelMapperServiceWithTypeMappingConfigs modelMapperService;

	@Autowired
	public SystemAdministratorManager(SystemAdministratorDao systemAdministratorDao, ModelMapperServiceWithTypeMappingConfigs modelMapperService) {
		// TODO Auto-generated constructor stub
		this.systemAdministratorDao = systemAdministratorDao;
		this.modelMapperService = modelMapperService;
		
	}

	@Override
	public DataResult<SystemAdministratorDto> save(SystemAdministratorDto systemAdministratorDto) {
		// TODO Auto-generated method stub
		try {
			SystemAdministrator systemAdministrator = new SystemAdministrator();

			systemAdministrator.setUserName(systemAdministratorDto.getUserName());
			systemAdministrator.setPassword(systemAdministratorDto.getPassword());
			systemAdministrator.setEmail(systemAdministratorDto.getEmail());

			SystemAdministrator systemAdministratorDb = systemAdministratorDao.save(systemAdministrator);

			systemAdministratorDto.setId(systemAdministratorDb.getId());
			systemAdministratorDto.setCreateDate(systemAdministratorDb.getCreateDate());
			systemAdministratorDto.setLastUpdateDate(systemAdministratorDb.getLastUpdateDate());

			return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}

	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);
			if (!(systemAdministrator.equals(Optional.empty()))) {
				systemAdministratorDao.deleteById(id);
				return new Result(true, id + " id'li sistem yöneticisi silindi.");
			}

			return new Result(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}
	}



	@Override
	public DataResult<SystemAdministratorDto> findById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

				systemAdministratorDto.setId(systemAdministrator.get().getId());
				systemAdministratorDto.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorDto.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorDto.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorDto.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorDto.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorDto.setAuthority(systemAdministrator.get().getAuthority());

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}	
		
	@Override
	public DataResult<SystemAdministratorDto> findByIdWithAllSchedules(long id) {
		// TODO Auto-generated method stub
		try {
			 modelMapperService.forResponse().createTypeMap(Schedule.class, ScheduleDto.class)
			.addMapping(src -> src.getLastUpdateDateSystemWorker(), ScheduleDto::setLastUpdateDateSystemWorker)
			.addMapping(src -> src.getDayOfWeek(), ScheduleDto::setDayOfWeek)
			.addMapping(src -> src.getHour(), ScheduleDto::setHour);
			
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

				List<Schedule> schedules = systemAdministrator.get().getSchedules();
				List<ScheduleDto> schedulesDto = new ArrayList<>();
				schedules.forEach(schedule -> {
					ScheduleDto scheduleDto = modelMapperService.forResponse().map(schedule, ScheduleDto.class);
					schedulesDto.add(scheduleDto);
				});

				List<WeeklySchedule> weeklySchedules = systemAdministrator.get().getWeeklySchedules();
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
				weeklySchedules.forEach(weeklySchedule -> {
					WeeklyScheduleDto weeklyScheduleDto = modelMapperService.forResponse().map(weeklySchedule,
							WeeklyScheduleDto.class);
					weeklySchedulesDto.add(weeklyScheduleDto);
				});
				
				systemAdministratorDto.setId(systemAdministrator.get().getId());
				systemAdministratorDto.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorDto.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorDto.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorDto.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorDto.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorDto.setAuthority(systemAdministrator.get().getAuthority());
				systemAdministratorDto.setSchedules(schedulesDto);
				systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> findByIdWithWeeklySchedules(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

				List<WeeklySchedule> weeklySchedules = systemAdministrator.get().getWeeklySchedules();
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
				weeklySchedules.forEach(weeklySchedule -> {
					WeeklyScheduleDto weeklyScheduleDto = modelMapperService.forResponse().map(weeklySchedule,
							WeeklyScheduleDto.class);
					weeklySchedulesDto.add(weeklyScheduleDto);
				});
				
				systemAdministratorDto.setId(systemAdministrator.get().getId());
				systemAdministratorDto.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorDto.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorDto.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorDto.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorDto.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorDto.setAuthority(systemAdministrator.get().getAuthority());
				systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<SystemAdministratorDto> findByIdWithSchedules(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

				List<Schedule> schedules = systemAdministrator.get().getSchedules();
				List<ScheduleDto> schedulesDto = new ArrayList<>();
				schedules.forEach(schedule -> {
					ScheduleDto scheduleDto = modelMapperService.forResponse().map(schedule, ScheduleDto.class);
					schedulesDto.add(scheduleDto);
				});
				
				systemAdministratorDto.setId(systemAdministrator.get().getId());
				systemAdministratorDto.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorDto.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorDto.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorDto.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorDto.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorDto.setAuthority(systemAdministrator.get().getAuthority());
				systemAdministratorDto.setSchedules(schedulesDto);

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisi getirildi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemAdministratorDto>> findAll() {
		// TODO Auto-generated method stub
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (systemAdministrators.size() != 0) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

					systemAdministratorDto.setId(systemAdministrator.getId());
					systemAdministratorDto.setUserName(systemAdministrator.getUserName());
					systemAdministratorDto.setPassword(systemAdministrator.getPassword());
					systemAdministratorDto.setEmail(systemAdministrator.getEmail());
					systemAdministratorDto.setAuthority(systemAdministrator.getAuthority());
					systemAdministratorDto.setCreateDate(systemAdministrator.getCreateDate());
					systemAdministratorDto.setLastUpdateDate(systemAdministrator.getLastUpdateDate());

					systemAdministratorsDto.add(systemAdministratorDto);
				});

				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");

			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithSchedules() {
		// TODO Auto-generated method stub
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (systemAdministrators.size() != 0) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

					List<Schedule> schedules = systemAdministrator.getSchedules();
					List<ScheduleDto> schedulesDto = new ArrayList<>();
					schedules.forEach(schedule -> {
						ScheduleDto scheduleDto = modelMapperService.forResponse().map(schedule, ScheduleDto.class);
						schedulesDto.add(scheduleDto);
					});
					
					systemAdministratorDto.setId(systemAdministrator.getId());
					systemAdministratorDto.setUserName(systemAdministrator.getUserName());
					systemAdministratorDto.setPassword(systemAdministrator.getPassword());
					systemAdministratorDto.setEmail(systemAdministrator.getEmail());
					systemAdministratorDto.setAuthority(systemAdministrator.getAuthority());
					systemAdministratorDto.setCreateDate(systemAdministrator.getCreateDate());
					systemAdministratorDto.setLastUpdateDate(systemAdministrator.getLastUpdateDate());
					systemAdministratorDto.setSchedules(schedulesDto);

					systemAdministratorsDto.add(systemAdministratorDto);
				});

				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");

			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}
		
	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithAllSchedules() {
		// TODO Auto-generated method stub
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (systemAdministrators.size() != 0) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

					List<Schedule> schedules = systemAdministrator.getSchedules();
					List<ScheduleDto> schedulesDto = new ArrayList<>();
					schedules.forEach(schedule -> {
						ScheduleDto scheduleDto = modelMapperService.forResponse().map(schedule, ScheduleDto.class);
						schedulesDto.add(scheduleDto);
					});

					List<WeeklySchedule> weeklySchedules = systemAdministrator.getWeeklySchedules();
					List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
					weeklySchedules.forEach(weeklySchedule -> {
						WeeklyScheduleDto weeklyScheduleDto = modelMapperService.forResponse().map(weeklySchedule,
								WeeklyScheduleDto.class);
						weeklySchedulesDto.add(weeklyScheduleDto);
					});
					
					systemAdministratorDto.setId(systemAdministrator.getId());
					systemAdministratorDto.setUserName(systemAdministrator.getUserName());
					systemAdministratorDto.setPassword(systemAdministrator.getPassword());
					systemAdministratorDto.setEmail(systemAdministrator.getEmail());
					systemAdministratorDto.setAuthority(systemAdministrator.getAuthority());
					systemAdministratorDto.setCreateDate(systemAdministrator.getCreateDate());
					systemAdministratorDto.setLastUpdateDate(systemAdministrator.getLastUpdateDate());
					systemAdministratorDto.setSchedules(schedulesDto);
					systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

					systemAdministratorsDto.add(systemAdministratorDto);
				});

				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");

			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemAdministratorDto>> findAllWithWeeklySchedules() {
		// TODO Auto-generated method stub
		try {
			List<SystemAdministrator> systemAdministrators = systemAdministratorDao.findAll();
			if (systemAdministrators.size() != 0) {
				List<SystemAdministratorDto> systemAdministratorsDto = new ArrayList<SystemAdministratorDto>();

				systemAdministrators.forEach(systemAdministrator -> {
					SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

					List<WeeklySchedule> weeklySchedules = systemAdministrator.getWeeklySchedules();
					List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
					weeklySchedules.forEach(weeklySchedule -> {
						WeeklyScheduleDto weeklyScheduleDto = modelMapperService.forResponse().map(weeklySchedule,
								WeeklyScheduleDto.class);
						weeklySchedulesDto.add(weeklyScheduleDto);
					});
					
					systemAdministratorDto.setId(systemAdministrator.getId());
					systemAdministratorDto.setUserName(systemAdministrator.getUserName());
					systemAdministratorDto.setPassword(systemAdministrator.getPassword());
					systemAdministratorDto.setEmail(systemAdministrator.getEmail());
					systemAdministratorDto.setAuthority(systemAdministrator.getAuthority());
					systemAdministratorDto.setCreateDate(systemAdministrator.getCreateDate());
					systemAdministratorDto.setLastUpdateDate(systemAdministrator.getLastUpdateDate());
					systemAdministratorDto.setWeeklySchedules(weeklySchedulesDto);

					systemAdministratorsDto.add(systemAdministratorDto);
				});

				return new DataResult<List<SystemAdministratorDto>>(systemAdministratorsDto, true,
						"Sistem yöneticileri getirildi.");

			} else {

				return new DataResult<List<SystemAdministratorDto>>(false, "Sistem yöneticisi bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<SystemAdministratorDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updateUserNameById(long id, String userName) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

				systemAdministrator.get().setUserName(userName);

				systemAdministratorDao.save(systemAdministrator.get());

				systemAdministratorDto.setId(systemAdministrator.get().getId());
				systemAdministratorDto.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorDto.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorDto.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorDto.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorDto.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorDto.setAuthority(systemAdministrator.get().getAuthority());

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisinin kullanıcı adı güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updatePasswordById(long id, String password) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

				systemAdministrator.get().setPassword(password);

				systemAdministratorDao.save(systemAdministrator.get());

				systemAdministratorDto.setId(systemAdministrator.get().getId());
				systemAdministratorDto.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorDto.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorDto.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorDto.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorDto.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorDto.setAuthority(systemAdministrator.get().getAuthority());

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisinin şifresi güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemAdministratorDto> updateEmailById(long id, String email) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemAdministrator> systemAdministrator = systemAdministratorDao.findById(id);

			if (!(systemAdministrator.equals(Optional.empty()))) {
				SystemAdministratorDto systemAdministratorDto = new SystemAdministratorDto();

				systemAdministrator.get().setEmail(email);

				systemAdministratorDao.save(systemAdministrator.get());

				systemAdministratorDto.setId(systemAdministrator.get().getId());
				systemAdministratorDto.setUserName(systemAdministrator.get().getUserName());
				systemAdministratorDto.setEmail(systemAdministrator.get().getEmail());
				systemAdministratorDto.setPassword(systemAdministrator.get().getPassword());
				systemAdministratorDto.setCreateDate(systemAdministrator.get().getCreateDate());
				systemAdministratorDto.setLastUpdateDate(systemAdministrator.get().getLastUpdateDate());
				systemAdministratorDto.setAuthority(systemAdministrator.get().getAuthority());

				return new DataResult<SystemAdministratorDto>(systemAdministratorDto, true,
						id + " id'li sistem yöneticisinin maili güncellendi.");
			}
			return new DataResult<SystemAdministratorDto>(false, id + " id'li sistem yöneticisi bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemAdministratorDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(systemAdministratorDao.count(), true, "Sistem yöneticilerinin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}	
	}



}

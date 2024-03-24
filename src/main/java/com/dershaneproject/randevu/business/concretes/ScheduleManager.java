package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.SystemWorkerDto;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.ScheduleSaveResponse;
import com.dershaneproject.randevu.entities.concretes.*;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleManager implements ScheduleService {

	private final ModelMapperServiceWithTypeMappingConfigs modelMapperService;
	private final ScheduleValidationService scheduleValidationService;
	private final ScheduleDao scheduleDao;
	private final TeacherDao teacherDao;
	private final DayOfWeekDao dayOfWeekDao;
	private final HourDao hourDao;
	private final SystemWorkerDao systemWorkerDao;

	@Override
	public DataResult<ScheduleSaveResponse> save(ScheduleSaveRequest scheduleSaveRequest) {
		try {
			Result validateResult = scheduleValidationService.isValidateResult(scheduleSaveRequest);

			if (validateResult.isSuccess()) {
				Schedule schedule = scheduleDao.save(createScheduleForSave(scheduleSaveRequest));
				ScheduleSaveResponse scheduleSaveResponse = modelMapperService.forResponse().map(
						schedule, ScheduleSaveResponse.class);
				// convert edemezse acilacak
//				scheduleSaveResponse.setTeacher(schedule.getTeacher());
//				scheduleSaveResponse.setLastUpdateDateSystemWorker(schedule.getLastUpdateDateSystemWorker());
//				scheduleSaveResponse.setDayOfWeek(schedule.getDayOfWeek());
//				scheduleSaveResponse.setHour(schedule.getHour());

				return new DataResult<ScheduleSaveResponse>(scheduleSaveResponse, true, "Program veritabanına eklendi.");

			} else {
				return new DataResult<>(false, validateResult.getMessage());
			}

		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<List<ScheduleSaveResponse>> saveAll(List<ScheduleSaveRequest> scheduleSaveRequestList) {
		// Firstly, scheduleSaveRequestList is validating one by one
		for (ScheduleSaveRequest scheduleSaveRequest : scheduleSaveRequestList) {
			Result resultValidation = scheduleValidationService.isValidateResult(scheduleSaveRequest);
			if(!(resultValidation.isSuccess())) {
				return new DataResult<>(false, resultValidation.getMessage());
			}
		}
		List<Schedule> schedules = new ArrayList<Schedule>();
		try {
			// Schedules will create and add to list
            for (ScheduleSaveRequest scheduleSaveRequest : scheduleSaveRequestList) {
                schedules.add(createScheduleForSave(scheduleSaveRequest));
            }
			// Schedules created and added to list

			// Schedule saved and id and dates return to list
			schedules = scheduleDao.saveAll(schedules);
			schedules = scheduleDao.findAllByIdSorted(schedules.stream()
					.map(Schedule::getId)
					.collect(Collectors.toList()));

			List<ScheduleSaveResponse> scheduleSaveResponseList = new LinkedList<ScheduleSaveResponse>();
			// Schedule's id and dates are set for scheduleSaveRequestList
            for (Schedule schedule : schedules) {
                ScheduleSaveResponse scheduleSaveResponse = modelMapperService.forResponse().map(
                        schedule, ScheduleSaveResponse.class);
                scheduleSaveResponseList.add(scheduleSaveResponse);
            }

			// Schedules are sending here
			return new DataResult<List<ScheduleSaveResponse>>(scheduleSaveResponseList, true, "Programlar veritabanına eklendi.");

		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	private Schedule createScheduleForSave(ScheduleSaveRequest scheduleSaveRequest) {
		DayOfWeek dayOfWeek = new DayOfWeek();
		dayOfWeek.setId(scheduleSaveRequest.getDayOfWeekId());

		Hour hour = new Hour();
		hour.setId(scheduleSaveRequest.getHourId());

		SystemWorker systemWorker = new SystemWorker();
		systemWorker.setId(scheduleSaveRequest.getLastUpdateDateSystemWorkerId());

		Teacher teacher = new Teacher();
		teacher.setId(scheduleSaveRequest.getTeacherId());

		Schedule schedule = new Schedule();
		schedule.setFull(scheduleSaveRequest.getFull());
		// if description is null set default description
		if(scheduleSaveRequest.getDescription() != null && !scheduleSaveRequest.getDescription().isEmpty()) {
			schedule.setDescription(scheduleSaveRequest.getDescription());
		}
		schedule.setTeacher(teacher);
		schedule.setLastUpdateDateSystemWorker(systemWorker);
		schedule.setDayOfWeek(dayOfWeek);
		schedule.setHour(hour);
		return schedule;
	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			if (!(schedule.equals(Optional.empty()))) {
				scheduleDao.deleteById(id);
				return new Result(true, id + " id'li program silindi.");
			}

			return new Result(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<ScheduleDto>> findAll() {
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

				return new DataResult<>(false, "Program bulunamadı.");
			}
	}

	@Override
	public DataResult<ScheduleDto> findById(long id) {
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
			return new DataResult<>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateFullById(long id, Boolean full) {
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
			return new DataResult<>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<ScheduleDto> updateTeacherById(long id, Long teacherId) {
		try {
			if(teacherId == null){
				return new DataResult<>(false, "Öğretmen Boş olamaz.");
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
					return new DataResult<>(false, id + " id'li program bulunamadı.");

				}
			}
			return new DataResult<>(false,
					id + " id'li program için verdiğiniz öğretmen id'sini kontrol ediniz.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<ScheduleDto> updateLastUpdateDateSystemWorkerById(long id, Long lastUpdateDateSystemWorkerId) {
		if (lastUpdateDateSystemWorkerId == null){
			return new DataResult<>(false, "Son güncelleme yapan sistem çalışanı boş bırakılamaz.");
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
					return new DataResult<>(false, id + " id'li program bulunamadı.");

				}
			}

			return new DataResult<>(false,
					id + " id'li program için verdiğiiz sistem çalışanını kontrol ediniz.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateDayOfWeekById(long id, Long dayOfWeekId) {

		if (dayOfWeekId == null) {
			return new DataResult<>(false, "Gün boş bırakılamaz.");
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
					return new DataResult<>(false, id + " id'li program bulunamadı.");

				}
			}
			return new DataResult<>(false,
					id + " id'li program için verdiğiniz gün id'sini kontrol ediniz.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateHourById(long id, Long hourId) {

		if (hourId == null) {
			return new DataResult<>(Boolean.FALSE, "Saat boş bırakılamaz.");
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
					return new DataResult<>(false, id + " id'li program bulunamadı.");

				}
			}
			return new DataResult<>(false,
					id + " id'li program için verdiğiniz saat id'sini kontrol ediniz.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<ScheduleDto> updateDescriptionById(long id, String description) {
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
			return new DataResult<>(false, id + " id'li program bulunamadı.");
		} catch (Exception e) {
			return new DataResult<>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(scheduleDao.count(), true, "Programların sayısı getirildi.");
	}

	@Override
	public DataResult<List<ScheduleDto>> findAllByTeacherId(long teacherId) {
		List<Schedule> schedules = scheduleDao.findAllByTeacherIdSorted(teacherId);

		if (!schedules.isEmpty()) {

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

			return new DataResult<>(false, "Program bulunamadı.");
		}
	}

}

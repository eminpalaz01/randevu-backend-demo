package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.SystemWorkerDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.Student;
import com.dershaneproject.randevu.entities.concretes.SystemWorker;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;

@Service
public class WeeklyScheduleManager implements WeeklyScheduleService{

	private ModelMapperServiceWithTypeMappingConfigs modelMapperService;
	private WeeklyScheduleDao weeklyScheduleDao;
	private TeacherDao teacherDao;
	private DayOfWeekDao dayOfWeekDao;
	private HourDao hourDao;
	
	@Autowired
	public WeeklyScheduleManager(ModelMapperServiceWithTypeMappingConfigs modelMapperService, WeeklyScheduleDao weeklyScheduleDao,
			TeacherDao teacherDao, DayOfWeekDao dayOfWeekDao, HourDao hourDao) {
		this.modelMapperService = modelMapperService;
		this.weeklyScheduleDao = weeklyScheduleDao;
		this.teacherDao = teacherDao;
		this.dayOfWeekDao = dayOfWeekDao;
		this.hourDao = hourDao;
	}

	@Override
	public DataResult<WeeklyScheduleDto> save(WeeklyScheduleDto weeklyScheduleDto) {
		// TODO Auto-generated method stub
		try {
			String messagePart1 = "Veritabanına program kaydı başarısız girdiğiniz";
			String messagePart2 = "değerleri sistemde bulunamadı kontrol ediniz.";

			Optional<Teacher> teacher = teacherDao.findById(weeklyScheduleDto.getTeacherId());
			Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(weeklyScheduleDto.getDayOfWeek().getId());
			Optional<Hour> hour = hourDao.findById(weeklyScheduleDto.getHour().getId());

			if (teacher.equals(Optional.empty()) || dayOfWeek.equals(Optional.empty())
					|| hour.equals(Optional.empty())) {

				if (teacher.equals(Optional.empty())) {
					messagePart1 += " öğretmen ";
				}
				if (dayOfWeek.equals(Optional.empty())) {
					messagePart1 += " gün ";
				}
				if (hour.equals(Optional.empty())) {
					messagePart1 += " saat ";
				}

				return new DataResult<WeeklyScheduleDto>(false, messagePart1 + messagePart2);

			} else {

				WeeklySchedule weeklySchedule = new WeeklySchedule();
				
				weeklySchedule.setFull(false);
				weeklySchedule.setLastUpdateDateSystemWorker(null);
				weeklySchedule.setStudent(null);
				weeklySchedule.setDescription(weeklyScheduleDto.getDescription());
				weeklySchedule.setTeacher(teacher.get());
				weeklySchedule.setDayOfWeek(dayOfWeek.get());
				weeklySchedule.setHour(hour.get());

				WeeklySchedule weeklyScheduleDb = weeklyScheduleDao.save(weeklySchedule);

				weeklyScheduleDto.setId(weeklyScheduleDb.getId());
				weeklyScheduleDto.setCreateDate(weeklyScheduleDb.getCreateDate());
				weeklyScheduleDto.setLastUpdateDate(weeklyScheduleDb.getLastUpdateDate());

				return new DataResult<WeeklyScheduleDto>(weeklyScheduleDto, true, "Haftalık Program veritabanına eklendi.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<WeeklyScheduleDto>(false, e.getMessage());
		}

	}
	
	@Override
	public DataResult<List<WeeklyScheduleDto>> saveAll(List<WeeklyScheduleDto> weeklySchedulesDto) {
		// TODO Auto-generated method stub
		return null;
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
						weeklyScheduleDto.setStudentId(0);

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
					weeklyScheduleDto.setStudentId(0);

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
					weeklyScheduleDto.setStudentId(0);

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
					weeklyScheduleDto.setStudentId(0);

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
					weeklyScheduleDto.setStudentId(0);

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
					weeklyScheduleDto.setStudentId(0);

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
					weeklyScheduleDto.setStudentId(0);

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
					weeklyScheduleDto.setStudentId(0);

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
					weeklyScheduleDto.setStudentId(0);

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

package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.ScheduleDao;
import com.dershaneproject.randevu.dataAccess.abstracts.StudentDao;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemWorkerDao;
import com.dershaneproject.randevu.dataAccess.abstracts.TeacherDao;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.StudentDto;
import com.dershaneproject.randevu.dto.SystemWorkerDto;
import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.Student;
import com.dershaneproject.randevu.entities.concretes.SystemWorker;
import com.dershaneproject.randevu.entities.concretes.Teacher;

@Service
public class ScheduleManager implements ScheduleService {

	private ModelMapperService modelMapperService;
	private ScheduleDao scheduleDao;
	private TeacherDao teacherDao;
	private StudentDao studentDao;
	private DayOfWeekDao dayOfWeekDao;
	private HourDao hourDao;
	private SystemWorkerDao systemWorkerDao;

	@Autowired
	public ScheduleManager(ScheduleDao scheduleDao, TeacherDao teacherDao, StudentDao studentDao,
			DayOfWeekDao dayOfWeekDao, HourDao hourDao, SystemWorkerDao systemWorkerDao, ModelMapperService modelMapperService) {
		this.scheduleDao = scheduleDao;
		this.teacherDao = teacherDao;
		this.studentDao = studentDao;
		this.dayOfWeekDao = dayOfWeekDao;
		this.hourDao = hourDao;
		this.systemWorkerDao = systemWorkerDao;
		this.modelMapperService = modelMapperService;
	}

	@Override
	public DataResult<ScheduleDto> save(ScheduleDto scheduleDto) {
		// TODO Auto-generated method stub
		try {
			String messagePart1 = "Veritabanına program kaydı başarısız girdiğiniz";
			String messagePart2 = "değerleri sistemde bulunamadı kontrol ediniz.";

			Optional<Teacher> teacher = teacherDao.findById(scheduleDto.getTeacherId());
			Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(scheduleDto.getDayOfWeek().getId());
			Optional<Hour> hour = hourDao.findById(scheduleDto.getHour().getId());

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

				return new DataResult<ScheduleDto>(false, messagePart1 + messagePart2);

			} else {

				Schedule schedule = new Schedule();
				schedule.setFull(false);
				schedule.setLastUpdateDateSystemWorker(null);
				schedule.setStudent(null);

				schedule.setTeacher(teacher.get());
				schedule.setDayOfWeek(dayOfWeek.get());
				schedule.setHour(hour.get());

				Schedule scheduleDb = scheduleDao.save(schedule);

				scheduleDto.setId(scheduleDb.getId());
				scheduleDto.setCreateDate(scheduleDb.getCreateDate());
				scheduleDto.setLastUpdateDate(scheduleDb.getLastUpdateDate());

				return new DataResult<ScheduleDto>(scheduleDto, true, "Program veritabanına eklendi.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<ScheduleDto>(false, e.getMessage());
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
					Student student = schedule.getStudent();
					SystemWorker lastUpdateDateSystemWorker = schedule.getLastUpdateDateSystemWorker();
                    
                    HourDto hourDto = modelMapperService.forResponse().map(schedule.getHour(), HourDto.class);
                    DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.getDayOfWeek(), DayOfWeekDto.class);
                    
					ScheduleDto scheduleDto = new ScheduleDto();
					scheduleDto.setId(schedule.getId());
					scheduleDto.setTeacherId(schedule.getTeacher().getId());
					scheduleDto.setDayOfWeek(dayOfWeekDto);
					scheduleDto.setHour(hourDto);
					scheduleDto.setFull(schedule.getFull());
					scheduleDto.setCreateDate(schedule.getCreateDate());
					scheduleDto.setLastUpdateDate(schedule.getLastUpdateDate());

					if (student == null) {
						scheduleDto.setStudentId(0);

					} else {
						scheduleDto.setStudentId(schedule.getStudent().getId());

					}

					if (lastUpdateDateSystemWorker == null) {
						scheduleDto.setLastUpdateDateSystemWorker(null);

					} else {
						SystemWorkerDto systemWorkerDto = 
								modelMapperService.forResponse().map(schedule.getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
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
				Student student = schedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();
				
                HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
                DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(), DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());

				if (student == null) {
					scheduleDto.setStudentId(0);

				} else {
					scheduleDto.setStudentId(schedule.get().getStudent().getId());

				}

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

				Student student = schedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();

				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(), DayOfWeekDto.class);
				
				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());

				if (student == null) {
					scheduleDto.setStudentId(0);

				} else {
					scheduleDto.setStudentId(schedule.get().getStudent().getId());

				}

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

				Student student = schedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();
				
				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(), DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());

				if (student == null) {
					scheduleDto.setStudentId(0);

				} else {
					scheduleDto.setStudentId(schedule.get().getStudent().getId());

				}

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
	public DataResult<ScheduleDto> updateStudentById(long id, long studentId) {
		// TODO Auto-generated method stub
		try {
			Optional<Schedule> schedule = scheduleDao.findById(id);
			Optional<Student> student = studentDao.findById(studentId);

			if (!(schedule.equals(Optional.empty())) && !(student.equals(Optional.empty()))) {
				schedule.get().setStudent(student.get());
				scheduleDao.save(schedule.get());

				Student studentCurrent = schedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();
				
				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(), DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());

				if (studentCurrent == null) {
					scheduleDto.setStudentId(0);

				} else {
					scheduleDto.setStudentId(studentCurrent.getId());

				}

				if (lastUpdateDateSystemWorker == null) {
					scheduleDto.setLastUpdateDateSystemWorker(null);

				} else {
					SystemWorkerDto systemWorkerDto = modelMapperService.forResponse()
							.map(schedule.get().getLastUpdateDateSystemWorker(), SystemWorkerDto.class);
					scheduleDto.setLastUpdateDateSystemWorker(systemWorkerDto);

				}

				return new DataResult<ScheduleDto>(scheduleDto, true, id + " id'li programın öğrencisi güncellendi.");
			} else {
				if (schedule.equals(Optional.empty())) {

					return new DataResult<ScheduleDto>(false, id + " id'li program bulunamadı.");
				}
			}
			return new DataResult<ScheduleDto>(false,
					id + " id'li program için verdiğiniz öğrenci id'sini kontrol ediniz.");

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

				Student student = schedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorkerCurrent = schedule.get().getLastUpdateDateSystemWorker();
				
				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(), DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());

				if (student == null) {
					scheduleDto.setStudentId(0);

				} else {
					scheduleDto.setStudentId(schedule.get().getStudent().getId());

				}

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

				Student student = schedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();
				
				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(), DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());

				if (student == null) {
					scheduleDto.setStudentId(0);

				} else {
					scheduleDto.setStudentId(schedule.get().getStudent().getId());

				}

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

				Student student = schedule.get().getStudent();
				SystemWorker lastUpdateDateSystemWorker = schedule.get().getLastUpdateDateSystemWorker();
				
				HourDto hourDto = modelMapperService.forResponse().map(schedule.get().getHour(), HourDto.class);
	            DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(schedule.get().getDayOfWeek(), DayOfWeekDto.class);

				ScheduleDto scheduleDto = new ScheduleDto();
				scheduleDto.setId(schedule.get().getId());
				scheduleDto.setTeacherId(schedule.get().getTeacher().getId());
				scheduleDto.setDayOfWeek(dayOfWeekDto);
				scheduleDto.setHour(hourDto);
				scheduleDto.setFull(schedule.get().getFull());
				scheduleDto.setCreateDate(schedule.get().getCreateDate());
				scheduleDto.setLastUpdateDate(schedule.get().getLastUpdateDate());

				if (student == null) {
					scheduleDto.setStudentId(0);

				} else {
					scheduleDto.setStudentId(schedule.get().getStudent().getId());

				}

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
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(scheduleDao.count(), true, "Programların sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}

}

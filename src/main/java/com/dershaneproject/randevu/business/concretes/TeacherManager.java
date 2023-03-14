package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.business.abstracts.TeacherService;
import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.core.utilities.concretes.ScheduleDtoComparator;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.DepartmentDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemWorkerDao;
import com.dershaneproject.randevu.dataAccess.abstracts.TeacherDao;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.SystemWorkerDto;
import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Department;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;

@Service
public class TeacherManager implements TeacherService {

	private TeacherDao teacherDao;
	private DepartmentDao departmentDao;
	private HourDao hourDao;
	private DayOfWeekDao dayOfWeekDao;
	private ScheduleService scheduleService;
	private WeeklyScheduleService weeklyScheduleService;
	private SystemWorkerDao systemWorkerDao;
	private ModelMapperServiceWithTypeMappingConfigs modelMapperService;

	@Autowired
	public TeacherManager(TeacherDao teacherDao, DepartmentDao departmentDao, HourDao hourDao,
			DayOfWeekDao dayOfWeekDao, ScheduleService scheduleService, WeeklyScheduleService weeklyScheduleService,
			ModelMapperServiceWithTypeMappingConfigs modelMapperService, SystemWorkerDao systemWorkerDao) {
		this.teacherDao = teacherDao;
		this.departmentDao = departmentDao;
		this.hourDao = hourDao;
		this.dayOfWeekDao = dayOfWeekDao;
		this.scheduleService = scheduleService;
		this.weeklyScheduleService = weeklyScheduleService;
		this.modelMapperService = modelMapperService;
		this.systemWorkerDao = systemWorkerDao;
	}

	@Transactional
	@Override
	public DataResult<TeacherDto> save(TeacherDto teacherDto) {
		try {
			Optional<Department> department = departmentDao.findById(teacherDto.getDepartmentId());
			List<ScheduleDto> schedulesDto = teacherDto.getSchedules();

			if (schedulesDto == null) {
				schedulesDto = new ArrayList<ScheduleDto>();
			}

			if (department.equals(Optional.empty())) {
				return new DataResult<TeacherDto>(false,
						"Veritabanına öğretmen kaydı başarısız departman id'sini kontrol ediniz.");
			} else {
				Teacher teacher = new Teacher();
				teacher.setUserName(teacherDto.getUserName());
				teacher.setPassword(teacherDto.getPassword());
				teacher.setEmail(teacherDto.getEmail());

				teacher.setDepartment(department.get());
				teacher.setTeacherNumber(teacherDto.getTeacherNumber());

				Teacher teacherDb = teacherDao.save(teacher);
				if(teacherDb != null) {
				 teacherDto.setId(teacherDb.getId());
				 teacherDto.setCreateDate(teacherDb.getCreateDate());
				 teacherDto.setLastUpdateDate(teacherDb.getLastUpdateDate());

	             DataResult<List<ScheduleDto>> resultUpdateSchedules = 
	            		updateSchedulesDtoForTeacher(schedulesDto, teacherDb.getId());
	            
	            if(resultUpdateSchedules.isSuccess()) {	
	              DataResult<List<ScheduleDto>> resultSchedules = scheduleService.saveAll(resultUpdateSchedules.getData());	
	              
	              	 if(resultSchedules.isSuccess()) {
					   teacherDto.setSchedules(resultSchedules.getData());
					   return new DataResult<TeacherDto>(teacherDto, true, "Öğretmen veritabanına eklendi.");
					  
				  }  else {
					   return new DataResult<TeacherDto>(false, resultSchedules.getMessage());
				  }
	             
	            } else {
	            	return new DataResult<TeacherDto>(false, resultUpdateSchedules.getMessage());
	            }
	            
			} else {
				return new DataResult<TeacherDto>(false, "Öğretmen veritabanına kaydedilirken bir sorun ile karşılaşıldı.");
			}
				
          }

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public Result deleteById(long id) {

		try {
			Optional<Teacher> teacher = teacherDao.findById(id);
			if (!(teacher.equals(Optional.empty()))) {
				teacherDao.deleteById(id);
				return new Result(true, id + " id'li öğretmen silindi.");
			}
			return new Result(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setSchedules(null);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findByIdWithSchedules(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				TeacherDto teacherDto = new TeacherDto();

				List<Schedule> schedules = teacher.get().getSchedules();
				List<ScheduleDto> schedulesDto = new ArrayList<>();
				schedules.forEach(schedule -> {
					ScheduleDto scheduleDto = modelMapperService.forResponse().map(schedule, ScheduleDto.class);
					schedulesDto.add(scheduleDto);
				});

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setSchedules(schedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<TeacherDto> findByIdWithAllSchedules(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				TeacherDto teacherDto = new TeacherDto();

				List<Schedule> schedules = teacher.get().getSchedules();
				List<ScheduleDto> schedulesDto = new ArrayList<>();
				schedules.forEach(schedule -> {
					ScheduleDto scheduleDto = modelMapperService.forResponse().map(schedule, ScheduleDto.class);
					schedulesDto.add(scheduleDto);
				});

				List<WeeklySchedule> weeklySchedules = teacher.get().getWeeklySchedules();
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
				weeklySchedules.forEach(weeklySchedule -> {
					WeeklyScheduleDto weeklyScheduleDto = modelMapperService.forResponse().map(weeklySchedule,
							WeeklyScheduleDto.class);
					weeklySchedulesDto.add(weeklyScheduleDto);
				});

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setSchedules(schedulesDto);
				teacherDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> findByIdWithWeeklySchedules(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				TeacherDto teacherDto = new TeacherDto();

				List<WeeklySchedule> weeklySchedules = teacher.get().getWeeklySchedules();
				List<WeeklyScheduleDto> weeklySchedulesDto = new ArrayList<>();
				weeklySchedules.forEach(weeklySchedule -> {
					WeeklyScheduleDto weeklyScheduleDto = modelMapperService.forResponse().map(weeklySchedule,
							WeeklyScheduleDto.class);
					weeklySchedulesDto.add(weeklyScheduleDto);
				});

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	// schedules değer verilirse eğer öğretmen başına 105 satır değer olucak buda
	// performans sorunu yaratır.
	// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
	@Override
	public DataResult<List<TeacherDto>> getByDepartmentId(long departmentId) {
		// TODO Auto-generated method stub
		try {
			List<Teacher> teachers = teacherDao.getByDepartmentId(departmentId);
			if (teachers.size() != 0) {
				List<TeacherDto> teachersDto = new ArrayList<TeacherDto>();

				teachers.forEach(teacher -> {

					TeacherDto teacherDto = new TeacherDto();
					teacherDto.setId(teacher.getId());
					teacherDto.setUserName(teacher.getUserName());
					teacherDto.setEmail(teacher.getEmail());
					teacherDto.setPassword(teacher.getPassword());
					teacherDto.setCreateDate(teacher.getCreateDate());
					teacherDto.setLastUpdateDate(teacher.getLastUpdateDate());
					teacherDto.setDepartmentId(teacher.getDepartment().getId());
					teacherDto.setTeacherNumber(teacher.getTeacherNumber());

					// Burası verilirse eğer öğretmen başına 105 satır değer olucak buda performans
					// sorunu yaratır.
					// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
					teacherDto.setSchedules(null);
					teachersDto.add(teacherDto);
				});

				return new DataResult<List<TeacherDto>>(teachersDto, true,
						departmentId + " departman id'li öğretmenler getirildi.");

			} else {

				return new DataResult<List<TeacherDto>>(false,
						departmentId + " departman id'li bir öğretmen bulunamadı.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<TeacherDto>>(false, e.getMessage());
		}
	}

	// schedules değer verilirse eğer öğretmen başına 105 satır değer olucak buda
	// performans sorunu yaratır.
	// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
	@Override
	public DataResult<List<TeacherDto>> findAll() {
		// TODO Auto-generated method stub
		try {
			List<Teacher> teachers = teacherDao.findAll();
			if (teachers.size() != 0) {
				List<TeacherDto> teachersDto = new ArrayList<TeacherDto>();

				teachers.forEach(teacher -> {

					TeacherDto teacherDto = new TeacherDto();
					teacherDto.setId(teacher.getId());
					teacherDto.setUserName(teacher.getUserName());
					teacherDto.setEmail(teacher.getEmail());
					teacherDto.setPassword(teacher.getPassword());
					teacherDto.setCreateDate(teacher.getCreateDate());
					teacherDto.setLastUpdateDate(teacher.getLastUpdateDate());
					teacherDto.setDepartmentId(teacher.getDepartment().getId());
					teacherDto.setTeacherNumber(teacher.getTeacherNumber());

					// Burası verilirse eğer öğretmen başına 105 satır değer olucak buda performans
					// sorunu yaratır.
					// Bu yüzden öğretmeni bireysel olarak çektiğimizde gönderdim sadece
					teacherDto.setSchedules(null);
					teachersDto.add(teacherDto);
				});

				return new DataResult<List<TeacherDto>>(teachersDto, true, "Öğretmenler getirildi.");

			} else {

				return new DataResult<List<TeacherDto>>(false, "Öğretmen bulunamadı.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<TeacherDto>>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateEmailById(long id, String email) {
		// TODO Auto-generated method stub

		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				teacher.get().setEmail(email);

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmenin maili güncellendi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateUserNameById(long id, String userName) {
		// TODO Auto-generated method stub
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				teacher.get().setUserName(userName);

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true,
						id + " id'li öğretmenin kullanıcı adı güncellendi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updatePasswordById(long id, String password) {
		// TODO Auto-generated method stub
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				teacher.get().setPassword(password);

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmenin şifresi güncellendi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateTeacherNumberById(long id, String teacherNumber) {
		// TODO Auto-generated method stub
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);

			if (!(teacher.equals(Optional.empty()))) {
				teacher.get().setTeacherNumber(teacherNumber);

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmenin numarası güncellendi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<TeacherDto> updateDepartmentById(long id, long departmentId) {
		// TODO Auto-generated method stub
		try {
			Optional<Teacher> teacher = teacherDao.findById(id);
			Optional<Department> department = departmentDao.findById(departmentId);

			if (!(teacher.equals(Optional.empty())) && !(department.equals(Optional.empty()))) {
				teacher.get().setDepartment(department.get());

				teacherDao.save(teacher.get());

				TeacherDto teacherDto = new TeacherDto();

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li öğretmenin departmanı güncellendi.");
			} else {
				if (department.equals(Optional.empty())) {
					return new DataResult<TeacherDto>(false,
							id + " id'li öğretmen için verdiğiniz departman id'sini kontrol ediniz.");
				}
			}
			return new DataResult<TeacherDto>(false, id + " id'li öğretmen bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	private DataResult<List<ScheduleDto>> updateSchedulesDtoForTeacher(List<ScheduleDto> schedulesDto, long teacherId) {
		
		// Eklerken hiç schedules verilmeme olasılığı olduğu için önce onu kontrol ediyorum ve
		// ScheduleValidationService de sadece create edilirken kullanılması için system çalışanı olmayan halini
		// yazdım bu arada. Eğerki eklenmişse Schedule bununda sistem çalışanı kontrol ediliyor var mı yok mu diye. 
		// ve de teacher oluşturulurken tüm programların sistem çalışanı aynı olmalı ondan buraya özel 
		// kontrol yazdım. ( private )
		boolean systemWorkersAreAllTheSame = schedulesDto.stream()
		        .filter(schedule -> schedule.getLastUpdateDateSystemWorker() != null)
				.map(schedule -> schedule.getLastUpdateDateSystemWorker())
		        .distinct()
		        .count() <= 1;
		
		if (systemWorkersAreAllTheSame == false) {
			return new DataResult<List<ScheduleDto>>(false,
			"Programlarınızda eklediğiniz sistem çalışanlarının bazıları birbiriyle farklı dikkat ediniz. "
			+ "(Bu istekte tek bir sistem çalışanı gönderilebilir.)");
		}
		
		SystemWorkerDto systemWorker = schedulesDto.stream()
				.filter(schedule -> schedule.getLastUpdateDateSystemWorker() != null)
				.findAny().get().getLastUpdateDateSystemWorker();
		
		if(systemWorker != null){
			if(!(systemWorkerDao.existsById(systemWorker.getId()))) {
				return new DataResult<List<ScheduleDto>>(false,
					"Eklediğiniz programlardaki sistem çalışanı bulunamadı kontrol ediniz.");	
			}		
		}
		// SystemWorker kontrolü tamamlandı. En başta yapmamın sebebi aşağıdakiler sistemi yorucak işler olabilir.

		long dayOfWeekCount = dayOfWeekDao.count();
		long hourCount = hourDao.count();
		
		List<ScheduleDto> willSaveSchedulesDto = new ArrayList<>();
		


		for (int i = 0; i < dayOfWeekCount; i++) {

			for (int k = 0; k < hourCount; k++) {
				final int dayId = i + 1;
				final int hourId = k + 1;

				Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById((long) dayId);
				Optional<Hour> hour = hourDao.findById((long) hourId);

				HourDto hourDto = modelMapperService.forResponse().map(hour, HourDto.class);
				DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(dayOfWeek, DayOfWeekDto.class);

				List<ScheduleDto> scheduleDto = schedulesDto.stream().filter(
						schedule -> schedule.getDayOfWeek().getId() == dayId && schedule.getHour().getId() == hourId)
						.collect(Collectors.toList());

				if (!(scheduleDto.size() > 1)) {
					if (scheduleDto.size() == 1) {
						scheduleDto.get(0).setTeacherId(teacherId);
						scheduleDto.get(0).setLastUpdateDateSystemWorker(systemWorker);

						willSaveSchedulesDto.add(scheduleDto.get(0));
					} else {
						ScheduleDto emptyscheduleDto = new ScheduleDto();
						emptyscheduleDto.setDayOfWeek(dayOfWeekDto);
						emptyscheduleDto.setHour(hourDto);
						emptyscheduleDto.setTeacherId(teacherId);
						emptyscheduleDto.setLastUpdateDateSystemWorker(systemWorker);
						emptyscheduleDto.setDescription(
								"Oluşturan: " + systemWorker.getUserName() + " ** Sistem tarafından oluşturulmuştur. **");
						emptyscheduleDto.setFull(false);

						willSaveSchedulesDto.add(emptyscheduleDto);
					}
				} else {
					return new DataResult<List<ScheduleDto>>(false,
							"Eklediğiniz programlarda gün ve saati aynı olan değerleriniz var kontrol ediniz.");
				}

			}
		}
			
		return new DataResult<List<ScheduleDto>>(willSaveSchedulesDto, true, "Öğretmenin programları ayarlandı.");
	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(teacherDao.count(), true, "Öğretmenlerin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}

}

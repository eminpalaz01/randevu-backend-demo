package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.business.abstracts.TeacherService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.DepartmentDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.TeacherDao;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Department;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.Teacher;

@Service
public class TeacherManager implements TeacherService {

	private TeacherDao teacherDao;
	private DepartmentDao departmentDao;
	private HourDao hourDao;
	private DayOfWeekDao dayOfWeekDao;
	private ScheduleService scheduleService;
	private ModelMapperService modelMapperService;

	@Autowired
	public TeacherManager(TeacherDao teacherDao, DepartmentDao departmentDao, HourDao hourDao,
			DayOfWeekDao dayOfWeekDao, ScheduleService scheduleService, ModelMapperService modelMapperService) {
		this.teacherDao = teacherDao;
		this.departmentDao = departmentDao;
		this.hourDao = hourDao;
		this.dayOfWeekDao = dayOfWeekDao;
		this.scheduleService = scheduleService;
		this.modelMapperService = modelMapperService;
	}

	@Override
	public DataResult<TeacherDto> save(TeacherDto teacherDto) {
		try {
			Optional<Department> department = departmentDao.findById(teacherDto.getDepartmentId());
			if (department.equals(Optional.empty())) {
				return new DataResult<TeacherDto>(false,
						"Veritaban??na ????retmen kayd?? ba??ar??s??z departman id'sini kontrol ediniz.");
			} else {
				Teacher teacher = new Teacher();
				teacher.setUserName(teacherDto.getUserName());
				teacher.setPassword(teacherDto.getPassword());
				teacher.setEmail(teacherDto.getEmail());
				
				teacher.setDepartment(department.get());
				teacher.setTeacherNumber(teacherDto.getTeacherNumber());

				Teacher teacherDb = teacherDao.save(teacher);

				teacherDto.setId(teacherDb.getId());
				teacherDto.setCreateDate(teacherDb.getCreateDate());
				teacherDto.setLastUpdateDate(teacherDb.getLastUpdateDate());

				for (int i = 0; i < dayOfWeekDao.count(); i++) {

					for (int k = 0; k < hourDao.count(); k++) {
						
						Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById((long) (i+1));
                        Optional<Hour> hour = hourDao.findById((long) (k+1));
                        
                        HourDto hourDto = modelMapperService.forResponse().map(hour, HourDto.class);
                        DayOfWeekDto dayOfWeekDto = modelMapperService.forResponse().map(dayOfWeek, DayOfWeekDto.class);
                        
						ScheduleDto scheduleDto = new ScheduleDto();
						scheduleDto.setDayOfWeek(dayOfWeekDto);
						scheduleDto.setHour(hourDto);
						scheduleDto.setTeacherId(teacherDb.getId());

						scheduleService.save(scheduleDto);
					}
				}

				return new DataResult<TeacherDto>(teacherDto, true, "????retmen veritaban??na eklendi.");
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
				return new Result(true, id + " id'li ????retmen silindi.");
			}
			return new Result(false, id + " id'li ????retmen bulunamad??.");
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

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li ????retmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li ????retmen bulunamad??.");
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

				teacherDto.setId(teacher.get().getId());
				teacherDto.setUserName(teacher.get().getUserName());
				teacherDto.setEmail(teacher.get().getEmail());
				teacherDto.setPassword(teacher.get().getPassword());
				teacherDto.setCreateDate(teacher.get().getCreateDate());
				teacherDto.setLastUpdateDate(teacher.get().getLastUpdateDate());
				teacherDto.setDepartmentId(teacher.get().getDepartment().getId());
				teacherDto.setTeacherNumber(teacher.get().getTeacherNumber());
				teacherDto.setSchedules(teacher.get().getSchedules());

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li ????retmen getirildi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li ????retmen bulunamad??.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}
	}

	// schedules de??er verilirse e??er ????retmen ba????na 105 sat??r de??er olucak buda
	// performans sorunu yarat??r.
	// Bu y??zden ????retmeni bireysel olarak ??ekti??imizde g??nderdim sadece
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

					// Buras?? verilirse e??er ????retmen ba????na 105 sat??r de??er olucak buda performans
					// sorunu yarat??r.
					// Bu y??zden ????retmeni bireysel olarak ??ekti??imizde g??nderdim sadece
					teacherDto.setSchedules(null);
					teachersDto.add(teacherDto);
				});

				return new DataResult<List<TeacherDto>>(teachersDto, true, departmentId + " departman id'li ????retmenler getirildi.");

			} else {

				return new DataResult<List<TeacherDto>>(false, departmentId + " departman id'li bir ????retmen bulunamad??.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<TeacherDto>>(false, e.getMessage());
		}
	}

	// schedules de??er verilirse e??er ????retmen ba????na 105 sat??r de??er olucak buda
	// performans sorunu yarat??r.
	// Bu y??zden ????retmeni bireysel olarak ??ekti??imizde g??nderdim sadece
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

					// Buras?? verilirse e??er ????retmen ba????na 105 sat??r de??er olucak buda performans
					// sorunu yarat??r.
					// Bu y??zden ????retmeni bireysel olarak ??ekti??imizde g??nderdim sadece
					teacherDto.setSchedules(null);
					teachersDto.add(teacherDto);
				});

				return new DataResult<List<TeacherDto>>(teachersDto, true, "????retmenler getirildi.");

			} else {

				return new DataResult<List<TeacherDto>>(false, "????retmen bulunamad??.");
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

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li ????retmenin maili g??ncellendi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li ????retmen bulunamad??.");
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
						id + " id'li ????retmenin kullan??c?? ad?? g??ncellendi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li ????retmen bulunamad??.");
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

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li ????retmenin ??ifresi g??ncellendi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li ????retmen bulunamad??.");
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

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li ????retmenin numaras?? g??ncellendi.");
			}
			return new DataResult<TeacherDto>(false, id + " id'li ????retmen bulunamad??.");
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

				return new DataResult<TeacherDto>(teacherDto, true, id + " id'li ????retmenin departman?? g??ncellendi.");
			} else {
				if (department.equals(Optional.empty())) {
					return new DataResult<TeacherDto>(false,
							id + " id'li ????retmen i??in verdi??iniz departman id'sini kontrol ediniz.");
				}
			}
			return new DataResult<TeacherDto>(false, id + " id'li ????retmen bulunamad??.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<TeacherDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(teacherDao.count(), true, "????retmenlerin say??s?? getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}

}

package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dershaneproject.randevu.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.StudentService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.StudentDao;
import com.dershaneproject.randevu.entities.concretes.Student;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;

@Service
@RequiredArgsConstructor
public class StudentManager implements StudentService {

	private final StudentDao studentDao;
	private final ModelMapperServiceWithTypeMappingConfigs modelMapperService;

	@Override
	public DataResult<StudentDto> save(StudentDto studentDto) {
		// TODO Auto-generated method stub
		try {
			
			Student student = new Student();

			student.setUserName(studentDto.getUserName());
			student.setPassword(studentDto.getPassword());
			student.setEmail(studentDto.getEmail());
			student.setStudentNumber(studentDto.getStudentNumber());

			Student studentDb = studentDao.save(student);

			studentDto.setId(studentDb.getId());
			studentDto.setCreateDate(studentDb.getCreateDate());
			studentDto.setLastUpdateDate(studentDb.getLastUpdateDate());

			return new DataResult<StudentDto>(studentDto, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<StudentDto>(false, e.getMessage());
		}

	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Student> student = studentDao.findById(id);
			if (!(student.equals(Optional.empty()))) {
				studentDao.deleteById(id);
				return new Result(true, id + " id'li öğrenci silindi.");
			}

			return new Result(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}

	}

	@Override
	public DataResult<List<StudentDto>> findAll() {
		// TODO Auto-generated method stub
		try {
			List<Student> students = studentDao.findAll();
			if (students.size() != 0) {
				List<StudentDto> studentsDto = new ArrayList<StudentDto>();

				students.forEach(student -> {
					StudentDto studentDto = new StudentDto();

					studentDto.setId(student.getId());
					studentDto.setUserName(student.getUserName());
					studentDto.setPassword(student.getPassword());
					studentDto.setEmail(student.getEmail());
					studentDto.setCreateDate(student.getCreateDate());
					studentDto.setLastUpdateDate(student.getLastUpdateDate());
					studentDto.setStudentNumber(student.getStudentNumber());

					studentsDto.add(studentDto);
				});

				return new DataResult<List<StudentDto>>(studentsDto, true, "Öğrenciler getirildi.");

			} else {

				return new DataResult<List<StudentDto>>(false, "Öğrenci bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<StudentDto>>(false, e.getMessage());
		}

	}
	
	@Override
	public DataResult<List<StudentDto>> findAllWithWeeklySchedules() {
		// TODO Auto-generated method stub
		try {
			List<Student> students = studentDao.findAll();
			if (students.size() != 0) {
				List<StudentDto> studentsDto = new ArrayList<StudentDto>();

				students.forEach(student -> {
					StudentDto studentDto = new StudentDto();
					
					List<WeeklySchedule> weeklySchedules = student.getWeeklySchedules();
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

						if (student == null) {
							weeklyScheduleDto.setStudentId(null);

						} else {
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

					// WeeklySchedulesDto are sorting here
					weeklySchedulesDto.sort((o1, o2) -> {
						Long s1DayOfWeekId = o1.getDayOfWeek().getId();
						int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());
						if (dayOfWeekCompare == 0) {
							Long s1HourId = o1.getHour().getId();
							int hourCompare = s1HourId.compareTo(o2.getHour().getId());
							return hourCompare;
						}
						return dayOfWeekCompare;
					});
					// WeeklySchedulesDto sorted

					studentDto.setId(student.getId());
					studentDto.setUserName(student.getUserName());
					studentDto.setPassword(student.getPassword());
					studentDto.setEmail(student.getEmail());
					studentDto.setCreateDate(student.getCreateDate());
					studentDto.setLastUpdateDate(student.getLastUpdateDate());
					studentDto.setStudentNumber(student.getStudentNumber());
					studentDto.setWeeklySchedules(weeklySchedulesDto);

					studentsDto.add(studentDto);
				});

				return new DataResult<List<StudentDto>>(studentsDto, true, "Öğrenciler getirildi.");

			} else {

				return new DataResult<List<StudentDto>>(false, "Öğrenci bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<StudentDto>>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<StudentDto> findById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Student> student = studentDao.findById(id);

			if (!(student.equals(Optional.empty()))) {
				StudentDto studentDto = new StudentDto();

				studentDto.setId(student.get().getId());
				studentDto.setUserName(student.get().getUserName());
				studentDto.setEmail(student.get().getEmail());
				studentDto.setPassword(student.get().getPassword());
				studentDto.setCreateDate(student.get().getCreateDate());
				studentDto.setLastUpdateDate(student.get().getLastUpdateDate());
				studentDto.setStudentNumber(student.get().getStudentNumber());
				studentDto.setWeeklySchedules(null);

				return new DataResult<StudentDto>(studentDto, true, id + " id'li öğrenci getirildi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<StudentDto>(false, e.getMessage());
		}

	}
	
	@Override
	public DataResult<StudentDto> findByIdWithWeeklySchedules(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Student> student = studentDao.findById(id);

			if (!(student.equals(Optional.empty()))) {
				StudentDto studentDto = new StudentDto();
				
				List<WeeklySchedule> weeklySchedules = student.get().getWeeklySchedules();
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

					if (student == null) {
						weeklyScheduleDto.setStudentId(null);

					} else {
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

				// WeeklySchedulesDto are sorting here
				weeklySchedulesDto.sort((o1, o2) -> {
					Long s1DayOfWeekId = o1.getDayOfWeek().getId();
					int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());
					if (dayOfWeekCompare == 0) {
						Long s1HourId = o1.getHour().getId();
						int hourCompare = s1HourId.compareTo(o2.getHour().getId());
						return hourCompare;
					}
					return dayOfWeekCompare;
				});
				// WeeklySchedulesDto sorted

				studentDto.setId(student.get().getId());
				studentDto.setUserName(student.get().getUserName());
				studentDto.setEmail(student.get().getEmail());
				studentDto.setPassword(student.get().getPassword());
				studentDto.setCreateDate(student.get().getCreateDate());
				studentDto.setLastUpdateDate(student.get().getLastUpdateDate());
				studentDto.setStudentNumber(student.get().getStudentNumber());
				studentDto.setWeeklySchedules(weeklySchedulesDto);

				return new DataResult<StudentDto>(studentDto, true, id + " id'li öğrenci getirildi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<StudentDto>(false, e.getMessage());
		}
	}
	@Override
	public DataResult<StudentDto> updateStudentNumberById(long id, String studentNumber) {
		// TODO Auto-generated method stub
		try {
			Optional<Student> student = studentDao.findById(id);

			if (!(student.equals(Optional.empty()))) {
				StudentDto studentDto = new StudentDto();

				student.get().setStudentNumber(studentNumber);

				studentDao.save(student.get());

				studentDto.setId(student.get().getId());
				studentDto.setUserName(student.get().getUserName());
				studentDto.setEmail(student.get().getEmail());
				studentDto.setPassword(student.get().getPassword());
				studentDto.setCreateDate(student.get().getCreateDate());
				studentDto.setLastUpdateDate(student.get().getLastUpdateDate());
				studentDto.setStudentNumber(student.get().getStudentNumber());

				return new DataResult<StudentDto>(studentDto, true, id + " id'li öğrencinin numarası güncellendi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<StudentDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<StudentDto> updateUserNameById(long id, String userName) {
		// TODO Auto-generated method stub
		try {
			Optional<Student> student = studentDao.findById(id);

			if (!(student.equals(Optional.empty()))) {
				StudentDto studentDto = new StudentDto();

				student.get().setUserName(userName);

				studentDao.save(student.get());

				studentDto.setId(student.get().getId());
				studentDto.setUserName(student.get().getUserName());
				studentDto.setEmail(student.get().getEmail());
				studentDto.setPassword(student.get().getPassword());
				studentDto.setCreateDate(student.get().getCreateDate());
				studentDto.setLastUpdateDate(student.get().getLastUpdateDate());
				studentDto.setStudentNumber(student.get().getStudentNumber());

				return new DataResult<StudentDto>(studentDto, true,
						id + " id'li öğrencinin kullanıcı adı güncellendi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<StudentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<StudentDto> updatePasswordById(long id, String password) {
		// TODO Auto-generated method stub
		try {
			Optional<Student> student = studentDao.findById(id);

			if (!(student.equals(Optional.empty()))) {
				StudentDto studentDto = new StudentDto();

				student.get().setPassword(password);

				studentDao.save(student.get());

				studentDto.setId(student.get().getId());
				studentDto.setUserName(student.get().getUserName());
				studentDto.setEmail(student.get().getEmail());
				studentDto.setPassword(student.get().getPassword());
				studentDto.setCreateDate(student.get().getCreateDate());
				studentDto.setLastUpdateDate(student.get().getLastUpdateDate());
				studentDto.setStudentNumber(student.get().getStudentNumber());

				return new DataResult<StudentDto>(studentDto, true, id + " id'li öğrencinin şifresi güncellendi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<StudentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<StudentDto> updateEmailById(long id, String email) {
		// TODO Auto-generated method stub
		try {
			Optional<Student> student = studentDao.findById(id);

			if (!(student.equals(Optional.empty()))) {
				StudentDto studentDto = new StudentDto();

				student.get().setEmail(email);

				studentDao.save(student.get());

				studentDto.setId(student.get().getId());
				studentDto.setUserName(student.get().getUserName());
				studentDto.setEmail(student.get().getEmail());
				studentDto.setPassword(student.get().getPassword());
				studentDto.setCreateDate(student.get().getCreateDate());
				studentDto.setLastUpdateDate(student.get().getLastUpdateDate());
				studentDto.setStudentNumber(student.get().getStudentNumber());

				return new DataResult<StudentDto>(studentDto, true, id + " id'li öğrencinin emaili güncellendi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<StudentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(studentDao.count(), true, "Öğrencilerin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}

}

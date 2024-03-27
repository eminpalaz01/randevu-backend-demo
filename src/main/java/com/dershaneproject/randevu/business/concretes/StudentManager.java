package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.StudentService;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.StudentDao;
import com.dershaneproject.randevu.dto.*;
import com.dershaneproject.randevu.dto.requests.StudentSaveRequest;
import com.dershaneproject.randevu.dto.responses.StudentSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Student;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentManager implements StudentService {

	private final StudentDao studentDao;
	private final ModelMapperServiceWithTypeMappingConfigs modelMapperService;

	@Override
	public DataResult<StudentSaveResponse> save(StudentSaveRequest studentSaveRequest) {
		try {
			Student student = studentDao.save(createStudentForSave(studentSaveRequest));
			StudentSaveResponse studentSaveResponse = modelMapperService.forResponse().map(student, StudentSaveResponse.class);

			return new DataResult<StudentSaveResponse>(studentSaveResponse, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			return new DataResult<StudentSaveResponse>(false, e.getMessage());
		}

	}

	private Student createStudentForSave(StudentSaveRequest studentSaveRequest) {
		Student student = new Student();
		student.setUserName(studentSaveRequest.getUserName());
		student.setPassword(studentSaveRequest.getPassword());
		student.setEmail(studentSaveRequest.getEmail());
		student.setStudentNumber(studentSaveRequest.getStudentNumber());
		return student;
	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<Student> student = studentDao.findById(id);
			if (!(student.equals(Optional.empty()))) {
				studentDao.deleteById(id);
				return new Result(true, id + " id'li öğrenci silindi.");
			}

			return new Result(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}

	}

	@Override
	public DataResult<List<StudentDto>> findAll() {
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
			return new DataResult<List<StudentDto>>(false, e.getMessage());
		}

	}
	
	@Override
	public DataResult<List<StudentDto>> findAllWithWeeklySchedules() {
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
						weeklyScheduleDto.setDayOfWeekDto(dayOfWeekDto);
						weeklyScheduleDto.setHourDto(hourDto);
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

						weeklyScheduleDto.setLastUpdateDateSystemWorkerDto(systemWorkerDto);
						weeklySchedulesDto.add(weeklyScheduleDto);
					});

					// WeeklySchedulesDto are sorting here
					weeklySchedulesDto.sort((o1, o2) -> {
						Long s1DayOfWeekId = o1.getDayOfWeekDto().getId();
						int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeekDto().getId());
						if (dayOfWeekCompare == 0) {
							Long s1HourId = o1.getHourDto().getId();
							int hourCompare = s1HourId.compareTo(o2.getHourDto().getId());
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
					studentDto.setWeeklySchedulesDto(weeklySchedulesDto);

					studentsDto.add(studentDto);
				});

				return new DataResult<List<StudentDto>>(studentsDto, true, "Öğrenciler getirildi.");

			} else {

				return new DataResult<List<StudentDto>>(false, "Öğrenci bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<StudentDto>>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<StudentDto> findById(long id) {
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
				studentDto.setWeeklySchedulesDto(null);

				return new DataResult<StudentDto>(studentDto, true, id + " id'li öğrenci getirildi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			return new DataResult<StudentDto>(false, e.getMessage());
		}

	}
	
	@Override
	public DataResult<StudentDto> findByIdWithWeeklySchedules(long id) {
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
					weeklyScheduleDto.setDayOfWeekDto(dayOfWeekDto);
					weeklyScheduleDto.setHourDto(hourDto);
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

					weeklyScheduleDto.setLastUpdateDateSystemWorkerDto(systemWorkerDto);
					weeklySchedulesDto.add(weeklyScheduleDto);
				});

				// WeeklySchedulesDto are sorting here
				weeklySchedulesDto.sort((o1, o2) -> {
					Long s1DayOfWeekId = o1.getDayOfWeekDto().getId();
					int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeekDto().getId());
					if (dayOfWeekCompare == 0) {
						Long s1HourId = o1.getHourDto().getId();
						int hourCompare = s1HourId.compareTo(o2.getHourDto().getId());
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
				studentDto.setWeeklySchedulesDto(weeklySchedulesDto);

				return new DataResult<StudentDto>(studentDto, true, id + " id'li öğrenci getirildi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			return new DataResult<StudentDto>(false, e.getMessage());
		}
	}
	@Override
	public DataResult<StudentDto> updateStudentNumberById(long id, String studentNumber) {
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
			return new DataResult<StudentDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<StudentDto> updateUserNameById(long id, String userName) {
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
			return new DataResult<StudentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<StudentDto> updatePasswordById(long id, String password) {
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
			return new DataResult<StudentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<StudentDto> updateEmailById(long id, String email) {
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
			return new DataResult<StudentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		try {
			return new DataResult<Long>(studentDao.count(), true, "Öğrencilerin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}

}

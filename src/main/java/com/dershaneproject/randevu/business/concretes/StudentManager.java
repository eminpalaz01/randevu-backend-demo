package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.StudentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.StudentDao;
import com.dershaneproject.randevu.dto.StudentDto;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.dto.requests.StudentSaveRequest;
import com.dershaneproject.randevu.dto.responses.StudentSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Student;
import com.dershaneproject.randevu.mappers.StudentMapper;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentManager implements StudentService {

	private final StudentDao studentDao;

	private final StudentMapper studentMapper;
	private final WeeklyScheduleMapper weeklyScheduleMapper;

	@Override
	public DataResult<StudentSaveResponse> save(StudentSaveRequest studentSaveRequest) {
		try {
			Student student = studentDao.save(createStudentForSave(studentSaveRequest));
			StudentSaveResponse studentSaveResponse = studentMapper.toSaveResponse(student);
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
			if (student.isPresent()) {
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
			if (!students.isEmpty()) {
				List<StudentDto> studentsDto = studentMapper.toDtoList(students);
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
			if (!students.isEmpty()) {
				List<StudentDto> studentsDto = new ArrayList<StudentDto>();
				students.forEach(student -> {
					StudentDto studentDto = studentMapper.toDto(student);
					List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(student.getWeeklySchedules());
					// WeeklySchedulesDto are sorting
					weeklySchedulesDto.sort((o1, o2) -> {
						Long s1DayOfWeekId = o1.getDayOfWeekDto().getId();
						int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeekDto().getId());
						if (dayOfWeekCompare == 0) {
							Long s1HourId = o1.getHourDto().getId();
                            return s1HourId.compareTo(o2.getHourDto().getId());
						}
						return dayOfWeekCompare;
					});
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
			if (student.isPresent()) {
				StudentDto studentDto = studentMapper.toDto(student.get());
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
			if (student.isPresent()) {
				StudentDto studentDto = studentMapper.toDto(student.get());
				List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(student.get().getWeeklySchedules());
				// WeeklySchedulesDto are sorting here
				weeklySchedulesDto.sort((o1, o2) -> {
					Long s1DayOfWeekId = o1.getDayOfWeekDto().getId();
					int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeekDto().getId());
					if (dayOfWeekCompare == 0) {
						Long s1HourId = o1.getHourDto().getId();
                        return s1HourId.compareTo(o2.getHourDto().getId());
					}
					return dayOfWeekCompare;
				});
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
			if (student.isPresent()) {
				student.get().setStudentNumber(studentNumber);
				StudentDto studentDto = studentMapper.toDto(studentDao.save(student.get()));
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
			if (student.isPresent()) {
				student.get().setUserName(userName);
				StudentDto studentDto = studentMapper.toDto(studentDao.save(student.get()));
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
			if (student.isPresent()) {
				student.get().setPassword(password);
				StudentDto studentDto = studentMapper.toDto(studentDao.save(student.get()));
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
			if (student.isPresent()) {
				student.get().setEmail(email);
				StudentDto studentDto = studentMapper.toDto(studentDao.save(student.get()));

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

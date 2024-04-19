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
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.mappers.StudentMapper;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
		Student student = studentDao.save(createStudentForSave(studentSaveRequest));
		StudentSaveResponse studentSaveResponse = studentMapper.toSaveResponse(student);
		return new DataResult<StudentSaveResponse>(studentSaveResponse, "Veritabanına kaydedildi.");
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
	public Result deleteById(long id) throws BusinessException {
		Optional<Student> student = studentDao.findById(id);
		if (student.isPresent()) {
			studentDao.deleteById(id);
			return new Result(id + " id'li öğrenci silindi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğrenci bulunamadı."));
	}

	@Override
	public DataResult<List<StudentDto>> findAll() throws BusinessException {
		List<Student> students = studentDao.findAll();
		if (students.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Öğrenci bulunamadı."));

		List<StudentDto> studentsDto = studentMapper.toDtoList(students);
		return new DataResult<List<StudentDto>>(studentsDto, "Öğrenciler getirildi.");

	}
	
	@Override
	public DataResult<List<StudentDto>> findAllWithWeeklySchedules() throws BusinessException {
		List<Student> students = studentDao.findAll();
		if (students.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Öğrenci bulunamadı."));

		List<StudentDto> studentsDto = new ArrayList<StudentDto>();
		students.forEach(student -> {
			StudentDto studentDto = studentMapper.toDto(student);
			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(student.getWeeklySchedules());
			// WeeklySchedulesDto are sorting
			weeklySchedulesDto.sort((o1, o2) -> {
				Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());
				if (dayOfWeekCompare == 0) {
					Long s1HourId = o1.getHour().getId();
					return s1HourId.compareTo(o2.getHour().getId());
				}
				return dayOfWeekCompare;
			});
			studentDto.setWeeklySchedules(weeklySchedulesDto);

			studentsDto.add(studentDto);
		});
		return new DataResult<List<StudentDto>>(studentsDto, "Öğrenciler getirildi.");
	}

	@Override
	public DataResult<StudentDto> findById(long id) throws BusinessException {
		Optional<Student> student = studentDao.findById(id);
		if (student.isPresent()) {
			StudentDto studentDto = studentMapper.toDto(student.get());
			return new DataResult<StudentDto>(studentDto, id + " id'li öğrenci getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğrenci bulunamadı."));
	}
	
	@Override
	public DataResult<StudentDto> findByIdWithWeeklySchedules(long id) throws BusinessException {
		Optional<Student> student = studentDao.findById(id);
		if (student.isPresent()) {
			StudentDto studentDto = studentMapper.toDto(student.get());
			List<WeeklyScheduleDto> weeklySchedulesDto = weeklyScheduleMapper.toDtoList(student.get().getWeeklySchedules());
			// WeeklySchedulesDto are sorting here
			weeklySchedulesDto.sort((o1, o2) -> {
				Long s1DayOfWeekId = o1.getDayOfWeek().getId();
				int dayOfWeekCompare = s1DayOfWeekId.compareTo(o2.getDayOfWeek().getId());
				if (dayOfWeekCompare == 0) {
					Long s1HourId = o1.getHour().getId();
					return s1HourId.compareTo(o2.getHour().getId());
				}
				return dayOfWeekCompare;
			});
			studentDto.setWeeklySchedules(weeklySchedulesDto);
			return new DataResult<StudentDto>(studentDto, id + " id'li öğrenci getirildi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğrenci bulunamadı."));

	}
	@Override
	public DataResult<StudentDto> updateStudentNumberById(long id, String studentNumber) throws BusinessException {
		Optional<Student> student = studentDao.findById(id);
		if (student.isPresent()) {
			student.get().setStudentNumber(studentNumber);
			StudentDto studentDto = studentMapper.toDto(studentDao.save(student.get()));
			return new DataResult<StudentDto>(studentDto, id + " id'li öğrencinin numarası güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğrenci bulunamadı."));
	}

	@Override
	public DataResult<StudentDto> updateUserNameById(long id, String userName) throws BusinessException {
		Optional<Student> student = studentDao.findById(id);
		if (student.isPresent()) {
			student.get().setUserName(userName);
			StudentDto studentDto = studentMapper.toDto(studentDao.save(student.get()));
			return new DataResult<StudentDto>(studentDto, id + " id'li öğrencinin kullanıcı adı güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğrenci bulunamadı."));
	}

	@Override
	public DataResult<StudentDto> updatePasswordById(long id, String password) throws BusinessException {
		Optional<Student> student = studentDao.findById(id);
		if (student.isPresent()) {
			student.get().setPassword(password);
			StudentDto studentDto = studentMapper.toDto(studentDao.save(student.get()));
			return new DataResult<StudentDto>(studentDto, id + " id'li öğrencinin şifresi güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğrenci bulunamadı."));
	}

	@Override
	public DataResult<StudentDto> updateEmailById(long id, String email) throws BusinessException {
		Optional<Student> student = studentDao.findById(id);
		if (student.isPresent()) {
			student.get().setEmail(email);
			StudentDto studentDto = studentMapper.toDto(studentDao.save(student.get()));

			return new DataResult<StudentDto>(studentDto, id + " id'li öğrencinin emaili güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li öğrenci bulunamadı."));
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(studentDao.count(), "Öğrencilerin sayısı getirildi.");
	}
}

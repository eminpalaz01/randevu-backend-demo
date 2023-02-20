package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.StudentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.StudentDao;
import com.dershaneproject.randevu.dto.StudentDto;
import com.dershaneproject.randevu.entities.concretes.Student;

@Service
public class StudentManager implements StudentService {

	private StudentDao studentDao;

	@Autowired
	public StudentManager(StudentDao studentDao) {
		this.studentDao = studentDao;
	}

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
				studentDto.setSchedules(null);

				return new DataResult<StudentDto>(studentDto, true, id + " id'li öğrenci getirildi.");
			}
			return new DataResult<StudentDto>(false, id + " id'li öğrenci bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<StudentDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<StudentDto> findByIdWithSchedules(long id) {
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
				studentDto.setSchedules(student.get().getSchedules());

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

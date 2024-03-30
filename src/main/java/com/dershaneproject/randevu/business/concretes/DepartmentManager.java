package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.DepartmentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DepartmentDao;
import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.dto.requests.DepartmentSaveRequest;
import com.dershaneproject.randevu.dto.responses.DepartmentSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Department;
import com.dershaneproject.randevu.mappers.DepartmentMapper;
import com.dershaneproject.randevu.mappers.TeacherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentManager implements DepartmentService {

	private final DepartmentDao departmentDao;

	private final DepartmentMapper departmentMapper;
	private final TeacherMapper teacherMapper;

	@Override
	public DataResult<DepartmentSaveResponse> save(DepartmentSaveRequest departmentSaveRequest) {
		try {
			Department department =  departmentMapper.toEntity(departmentSaveRequest);

			DepartmentSaveResponse departmentSaveResponse = departmentMapper.toSaveResponse(department);
			departmentSaveResponse.setId(departmentDao.save(department).getId());

			return new DataResult<DepartmentSaveResponse>(departmentSaveResponse, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			return new DataResult<DepartmentSaveResponse>(false, e.getMessage());
		}

	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<Department> department = departmentDao.findById(id);
			if (!(department.equals(Optional.empty()))) {
				departmentDao.deleteById(id);
				return new Result(true, id + " id'li silme işlemi başarılı.");
			}

			return new Result(false, id + " id'li departman bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}

	}

	@Override
	public DataResult<DepartmentDto> findWithTeachersById(long id) {
		try {
			Optional<Department> department = departmentDao.findById(id);
			if (department.isPresent()) {
				DepartmentDto departmentDto = departmentMapper.toDto(department.get());
				List<TeacherDto> teachersDto = teacherMapper.toDtoList(department.get().getTeachers());
				departmentDto.setTeachersDto(teachersDto);
				return new DataResult<DepartmentDto>(departmentDto, true, id + " id'li departman bulundu.");
			}
			return new DataResult<DepartmentDto>(false, id + " id'li departman bulunamadı.");
		} catch (Exception e) {
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<DepartmentDto> findById(long id) {
		try {
			Optional<Department> department = departmentDao.findById(id);
			if (department.isPresent()) {
				DepartmentDto departmentDto = departmentMapper.toDto(department.get());
				return new DataResult<DepartmentDto>(departmentDto, true, id + " id'li departman bulundu.");
			}
			return new DataResult<DepartmentDto>(false, id + " id'li departman bulunamadı.");
		} catch (Exception e) {
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<DepartmentDto>> findAllWithTeachers() {
		try {
			List<Department> departments = departmentDao.findAll();
			if (!departments.isEmpty()) {
				List<DepartmentDto> departmentsDto = new ArrayList<DepartmentDto>();
				for (Department department : departments) {
					DepartmentDto departmentDto = departmentMapper.toDto(department);
					List<TeacherDto> teachersDto = teacherMapper.toDtoList(department.getTeachers());
					departmentsDto.add(departmentDto);
				}
				return new DataResult<List<DepartmentDto>>(departmentsDto, true, "Tüm departmanlar getirildi.");
			} else {
				return new DataResult<List<DepartmentDto>>(false, "Departman bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<DepartmentDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<DepartmentDto>> findAll() {
		try {
			List<Department> departments = departmentDao.findAll();
			if (!departments.isEmpty()) {
				List<DepartmentDto> departmentsDto = departmentMapper.toDtoList(departments);
				return new DataResult<List<DepartmentDto>>(departmentsDto, true, "Tüm departmanlar getirildi.");
			} else {
				return new DataResult<List<DepartmentDto>>(false, "Departman bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<DepartmentDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<DepartmentDto> updateNameById(long id, String name) {
		try {
			Optional<Department> department = departmentDao.findById(id);
			if (department.isPresent()) {
				department.get().setName(name);
				departmentDao.save(department.get());
				DepartmentDto departmentDto = departmentMapper.toDto(department.get());
				return new DataResult<DepartmentDto>(departmentDto, true, id + " id'li departmanın adı güncellendi.");
			}
			return new DataResult<DepartmentDto>(false, id + " id'li departman bulunamadı.");

		} catch (Exception e) {
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<DepartmentDto> updateCompressingById(long id, String compressing) {
		try {
			Optional<Department> department = departmentDao.findById(id);
			if (department.isPresent()) {
				department.get().setCompressing(compressing);
				departmentDao.save(department.get());
				DepartmentDto departmentDto = departmentMapper.toDto(department.get());
				return new DataResult<DepartmentDto>(departmentDto, true, id + " id'li departmanın kısaltması güncellendi.");
			}
			return new DataResult<DepartmentDto>(false, id + " id'li departman bulunamadı.");
		} catch (Exception e) {
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		try {
			return new DataResult<Long>(departmentDao.count(), true, "Departmanların sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}
}

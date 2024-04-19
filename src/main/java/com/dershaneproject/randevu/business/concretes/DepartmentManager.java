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
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.mappers.DepartmentMapper;
import com.dershaneproject.randevu.mappers.TeacherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
		Department department =  departmentMapper.toEntity(departmentSaveRequest);

		DepartmentSaveResponse departmentSaveResponse = departmentMapper.toSaveResponse(department);
		departmentSaveResponse.setId(departmentDao.save(department).getId());

		return new DataResult<DepartmentSaveResponse>(departmentSaveResponse, "Veritabanına kaydedildi.");
	}

	@Override
	public Result deleteById(long id) throws BusinessException {
		Optional<Department> department = departmentDao.findById(id);
		if (department.isPresent()) {
			departmentDao.deleteById(id);
			return new Result(id + " id'li silme işlemi başarılı.");
		}

		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li departman bulunamadı."));
	}

	@Override
	public DataResult<DepartmentDto> findWithTeachersById(long id) throws BusinessException {
		Optional<Department> department = departmentDao.findById(id);
		if (department.isPresent()) {
			DepartmentDto departmentDto = departmentMapper.toDto(department.get());
			List<TeacherDto> teachersDto = teacherMapper.toDtoList(department.get().getTeachers());
			departmentDto.setTeachers(teachersDto);
			return new DataResult<DepartmentDto>(departmentDto, id + " id'li departman bulundu.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li departman bulunamadı."));
	}

	@Override
	public DataResult<DepartmentDto> findById(long id) throws BusinessException {
		Optional<Department> department = departmentDao.findById(id);
		if (department.isPresent()) {
			DepartmentDto departmentDto = departmentMapper.toDto(department.get());
			return new DataResult<DepartmentDto>(departmentDto, id + " id'li departman bulundu.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li departman bulunamadı."));
	}

	@Override
	public DataResult<List<DepartmentDto>> findAllWithTeachers() throws BusinessException {
		List<Department> departments = departmentDao.findAll();
		if (!departments.isEmpty()) {
			List<DepartmentDto> departmentsDto = new ArrayList<DepartmentDto>();
			for (Department department : departments) {
				DepartmentDto departmentDto = departmentMapper.toDto(department);
				List<TeacherDto> teachersDto = teacherMapper.toDtoList(department.getTeachers());
				departmentDto.setTeachers(teachersDto);
				departmentsDto.add(departmentDto);
			}
			return new DataResult<List<DepartmentDto>>(departmentsDto, "Tüm departmanlar getirildi.");
		} else {
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Departman bulunamadı."));
		}
	}

	@Override
	public DataResult<List<DepartmentDto>> findAll() throws BusinessException {
		List<Department> departments = departmentDao.findAll();
		if (!departments.isEmpty()) {
			List<DepartmentDto> departmentsDto = departmentMapper.toDtoList(departments);
			return new DataResult<List<DepartmentDto>>(departmentsDto, "Tüm departmanlar getirildi.");
		} else {
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Departman bulunamadı."));
		}
	}

	@Override
	public DataResult<DepartmentDto> updateNameById(long id, String name) throws BusinessException {
		Optional<Department> department = departmentDao.findById(id);
		if (department.isPresent()) {
			department.get().setName(name);
			departmentDao.save(department.get());
			DepartmentDto departmentDto = departmentMapper.toDto(department.get());
			return new DataResult<DepartmentDto>(departmentDto, id + " id'li departmanın adı güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li departman bulunamadı."));
	}

	@Override
	public DataResult<DepartmentDto> updateCompressingById(long id, String compressing) throws BusinessException {
		Optional<Department> department = departmentDao.findById(id);
		if (department.isPresent()) {
			department.get().setCompressing(compressing);
			departmentDao.save(department.get());
			DepartmentDto departmentDto = departmentMapper.toDto(department.get());
			return new DataResult<DepartmentDto>(departmentDto, id + " id'li departmanın kısaltması güncellendi.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li departman bulunamadı."));
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(departmentDao.count(), "Departmanların sayısı getirildi.");
	}
}

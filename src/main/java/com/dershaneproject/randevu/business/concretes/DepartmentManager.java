package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.DepartmentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DepartmentDao;
import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.entities.concretes.Department;

@Service
public class DepartmentManager implements DepartmentService {

	private DepartmentDao departmentDao;

	@Autowired
	public DepartmentManager(DepartmentDao departmentDao) {
		this.departmentDao = departmentDao;
	}

	@Override
	public DataResult<DepartmentDto> save(DepartmentDto departmentDto) {
		// TODO Auto-generated method stub
		try {
			Department department = new Department();

			department.setName(departmentDto.getName());
			department.setCompressing(departmentDto.getCompressing());

			Department departmentDb = departmentDao.save(department);

			departmentDto.setId(departmentDb.getId());

			return new DataResult<DepartmentDto>(departmentDto, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}

	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Department> department = departmentDao.findById(id);
			if (!(department.equals(Optional.empty()))) {
				departmentDao.deleteById(id);
				return new Result(true, id + " id'li silme işlemi başarılı.");
			}

			return new Result(false, id + " id'li departman bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}

	}

	@Override
	public DataResult<DepartmentDto> findWithTeachersById(long id) {
		// TODO Auto-generated method stub

		try {
			Optional<Department> department = departmentDao.findById(id);
			if (!(department.equals(Optional.empty()))) {
				DepartmentDto departmentDto = new DepartmentDto();
				departmentDto.setId(department.get().getId());
				departmentDto.setName(department.get().getName());
				departmentDto.setCompressing(department.get().getCompressing());
				departmentDto.setTeachers(department.get().getTeachers());

				return new DataResult<DepartmentDto>(departmentDto, true, id + " id'li departman bulundu.");
			}

			return new DataResult<DepartmentDto>(false, id + " id'li departman bulunamadı.");

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<DepartmentDto> findById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<Department> department = departmentDao.findById(id);
			if (!(department.equals(Optional.empty()))) {
				DepartmentDto departmentDto = new DepartmentDto();
				departmentDto.setId(department.get().getId());
				departmentDto.setName(department.get().getName());
				departmentDto.setCompressing(department.get().getCompressing());

				return new DataResult<DepartmentDto>(departmentDto, true, id + " id'li departman bulundu.");
			}

			return new DataResult<DepartmentDto>(false, id + " id'li departman bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<List<DepartmentDto>> findAllWithTeachers() {
		// TODO Auto-generated method stub
		try {
			List<Department> departments = departmentDao.findAll();
			if (departments.size() != 0) {
				List<DepartmentDto> departmentsDto = new ArrayList<DepartmentDto>();

				departments.forEach(department -> {
					DepartmentDto departmentDto = new DepartmentDto();
					departmentDto.setId(department.getId());
					departmentDto.setName(department.getName());
					departmentDto.setCompressing(department.getCompressing());
					departmentDto.setTeachers(department.getTeachers());

					departmentsDto.add(departmentDto);
				});
				return new DataResult<List<DepartmentDto>>(departmentsDto, true, "Tüm departmanlar getirildi.");
			} else {
				return new DataResult<List<DepartmentDto>>(false, "Departman bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<DepartmentDto>>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<List<DepartmentDto>> findAll() {
		// TODO Auto-generated method stub
		try {
			List<Department> departments = departmentDao.findAll();
			if (departments.size() != 0) {
				List<DepartmentDto> departmentsDto = new ArrayList<DepartmentDto>();

				departments.forEach(department -> {
					DepartmentDto departmentDto = new DepartmentDto();
					departmentDto.setId(department.getId());
					departmentDto.setName(department.getName());
					departmentDto.setCompressing(department.getCompressing());

					departmentsDto.add(departmentDto);
				});
				return new DataResult<List<DepartmentDto>>(departmentsDto, true, "Tüm departmanlar getirildi.");
			} else {
				return new DataResult<List<DepartmentDto>>(false, "Departman bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<DepartmentDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<DepartmentDto> updateNameById(long id, String name) {
		// TODO Auto-generated method stub

		try {
			Optional<Department> department = departmentDao.findById(id);
			if (!(department.equals(Optional.empty()))) {
				department.get().setName(name);

				departmentDao.save(department.get());

				DepartmentDto departmentDto = new DepartmentDto();
				departmentDto.setId(department.get().getId());
				departmentDto.setName(department.get().getName());
				departmentDto.setCompressing(department.get().getCompressing());

				return new DataResult<DepartmentDto>(departmentDto, true, id + " id'li departmanın adı güncellendi.");
			}

			return new DataResult<DepartmentDto>(false, id + " id'li departman bulunamadı.");

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<DepartmentDto> updateCompressingById(long id, String compressing) {
		// TODO Auto-generated method stub
		try {
			Optional<Department> department = departmentDao.findById(id);
			if (!(department.equals(Optional.empty()))) {
				department.get().setCompressing(compressing);

				departmentDao.save(department.get());

				DepartmentDto departmentDto = new DepartmentDto();
				departmentDto.setId(department.get().getId());
				departmentDto.setName(department.get().getName());
				departmentDto.setCompressing(department.get().getCompressing());

				return new DataResult<DepartmentDto>(departmentDto, true, id + " id'li departmanın kısaltması güncellendi.");
			}

			return new DataResult<DepartmentDto>(false, id + " id'li departman bulunamadı.");

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<DepartmentDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(departmentDao.count(), true, "Günlerin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}
}

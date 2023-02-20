package com.dershaneproject.randevu.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.business.abstracts.SystemStaffService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemStaffDao;
import com.dershaneproject.randevu.dto.SystemStaffDto;
import com.dershaneproject.randevu.entities.concretes.SystemStaff;

@Service
public class SystemStaffManager implements SystemStaffService {

	private SystemStaffDao systemStaffDao;

	@Autowired
	public SystemStaffManager(SystemStaffDao systemStaffDao) {
		// TODO Auto-generated constructor stub
		this.systemStaffDao = systemStaffDao;
	}

	@Override
	public DataResult<SystemStaffDto> save(SystemStaffDto systemStaffDto) {
		// TODO Auto-generated method stub
		try {
			SystemStaff systemStaff = new SystemStaff();

			systemStaff.setUserName(systemStaffDto.getUserName());
			systemStaff.setPassword(systemStaffDto.getPassword());
			systemStaff.setEmail(systemStaffDto.getEmail());

			SystemStaff systemStaffDb = systemStaffDao.save(systemStaff);

			systemStaffDto.setId(systemStaffDb.getId());
			systemStaffDto.setCreateDate(systemStaffDb.getCreateDate());
			systemStaffDto.setLastUpdateDate(systemStaffDb.getLastUpdateDate());

			return new DataResult<SystemStaffDto>(systemStaffDto, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);
			if (!(systemStaff.equals(Optional.empty()))) {
				systemStaffDao.deleteById(id);
				return new Result(true, id + " id'li sistem çalışanı silindi.");
			}

			return new Result(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<SystemStaffDto>> findAll() {
		// TODO Auto-generated method stub
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (systemStaffs.size() != 0) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = new SystemStaffDto();

					systemStaffDto.setId(systemStaff.getId());
					systemStaffDto.setUserName(systemStaff.getUserName());
					systemStaffDto.setPassword(systemStaff.getPassword());
					systemStaffDto.setEmail(systemStaff.getEmail());
					systemStaffDto.setAuthority(systemStaff.getAuthority());
					systemStaffDto.setCreateDate(systemStaff.getCreateDate());
					systemStaffDto.setLastUpdateDate(systemStaff.getLastUpdateDate());

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<List<SystemStaffDto>> findAllWithSchedules() {
		// TODO Auto-generated method stub
		try {
			List<SystemStaff> systemStaffs = systemStaffDao.findAll();
			if (systemStaffs.size() != 0) {
				List<SystemStaffDto> systemStaffsDto = new ArrayList<SystemStaffDto>();

				systemStaffs.forEach(systemStaff -> {
					SystemStaffDto systemStaffDto = new SystemStaffDto();

					systemStaffDto.setId(systemStaff.getId());
					systemStaffDto.setUserName(systemStaff.getUserName());
					systemStaffDto.setPassword(systemStaff.getPassword());
					systemStaffDto.setEmail(systemStaff.getEmail());
					systemStaffDto.setAuthority(systemStaff.getAuthority());
					systemStaffDto.setCreateDate(systemStaff.getCreateDate());
					systemStaffDto.setLastUpdateDate(systemStaff.getLastUpdateDate());
					systemStaffDto.setSchedules(systemStaff.getSchedules());

					systemStaffsDto.add(systemStaffDto);
				});

				return new DataResult<List<SystemStaffDto>>(systemStaffsDto, true, "Sistem çalışanları getirildi.");

			} else {

				return new DataResult<List<SystemStaffDto>>(false, "Sistem çalışanı bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<SystemStaffDto>>(false, e.getMessage());
		}
	}



	@Override
	public DataResult<SystemStaffDto> findById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}
	
	@Override
	public DataResult<SystemStaffDto> findByIdWithSchedules(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());
				systemStaffDto.setSchedules(systemStaff.get().getSchedules());

				return new DataResult<SystemStaffDto>(systemStaffDto, true, id + " id'li sistem çalışanı getirildi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updateUserNameById(long id, String userName) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaff.get().setUserName(userName);

				systemStaffDao.save(systemStaff.get());

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());

				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının kullanıcı adı güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updatePasswordById(long id, String password) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaff.get().setPassword(password);

				systemStaffDao.save(systemStaff.get());

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());

				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının şifresi güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<SystemStaffDto> updateEmailById(long id, String email) {
		// TODO Auto-generated method stub
		try {
			Optional<SystemStaff> systemStaff = systemStaffDao.findById(id);

			if (!(systemStaff.equals(Optional.empty()))) {
				SystemStaffDto systemStaffDto = new SystemStaffDto();

				systemStaff.get().setEmail(email);

				systemStaffDao.save(systemStaff.get());

				systemStaffDto.setId(systemStaff.get().getId());
				systemStaffDto.setUserName(systemStaff.get().getUserName());
				systemStaffDto.setEmail(systemStaff.get().getEmail());
				systemStaffDto.setPassword(systemStaff.get().getPassword());
				systemStaffDto.setCreateDate(systemStaff.get().getCreateDate());
				systemStaffDto.setLastUpdateDate(systemStaff.get().getLastUpdateDate());
				systemStaffDto.setAuthority(systemStaff.get().getAuthority());

				return new DataResult<SystemStaffDto>(systemStaffDto, true,
						id + " id'li sistem çalışanının maili güncellendi.");
			}
			return new DataResult<SystemStaffDto>(false, id + " id'li sistem çalışanı bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<SystemStaffDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(systemStaffDao.count(), true, "Sistem çalışanlarının sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}	
	}

}

package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.DayOfWeekService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.requests.DayOfWeekSaveRequest;
import com.dershaneproject.randevu.dto.responses.DayOfWeekSaveResponse;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DayOfWeekManager implements DayOfWeekService {

	private final DayOfWeekDao dayOfWeekDao;

	@Override
	public DataResult<DayOfWeekSaveResponse> save(DayOfWeekSaveRequest dayOfWeekSaveRequest) {
		try {
			DayOfWeek dayOfWeek = new DayOfWeek();
			dayOfWeek.setName(dayOfWeekSaveRequest.getName());

			DayOfWeekSaveResponse dayOfWeekSaveResponse = new DayOfWeekSaveResponse();
			dayOfWeekSaveResponse.setId(dayOfWeekDao.save(dayOfWeek).getId());

			return new DataResult<DayOfWeekSaveResponse>(dayOfWeekSaveResponse, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<DayOfWeekSaveResponse>(false, e.getMessage());
		}
	}

	@Override
	public Result deleteById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(id);
			if (!(dayOfWeek.equals(Optional.empty()))) {
				dayOfWeekDao.deleteById(id);
				return new Result(true, id + " id'li silme işlemi başarılı.");
			}

			return new Result(false, id + " id'li gün bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<DayOfWeekDto>> findAll() {
		// TODO Auto-generated method stub
		try {
			List<DayOfWeek> daysOfWeek = dayOfWeekDao.findAll();
			if (daysOfWeek.size() != 0) {
				List<DayOfWeekDto> daysOfWeekDto = new ArrayList<DayOfWeekDto>();

				daysOfWeek.forEach(dayOfWeek -> {
					DayOfWeekDto dayOfWeekDto = new DayOfWeekDto();
					dayOfWeekDto.setId(dayOfWeek.getId());
					dayOfWeekDto.setName(dayOfWeek.getName());

					daysOfWeekDto.add(dayOfWeekDto);
				});
				return new DataResult<List<DayOfWeekDto>>(daysOfWeekDto, true, "Tüm günler getirildi.");
			} else {
				return new DataResult<List<DayOfWeekDto>>(false, "Kayıtlı Gün bulunamadı.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<List<DayOfWeekDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<DayOfWeekDto> findById(long id) {
		// TODO Auto-generated method stub
		try {
			Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(id);
			if (!(dayOfWeek.equals(Optional.empty()))) {
				DayOfWeekDto dayOfWeekDto = new DayOfWeekDto();
				dayOfWeekDto.setId(dayOfWeek.get().getId());
				dayOfWeekDto.setName(dayOfWeek.get().getName());

				return new DataResult<DayOfWeekDto>(dayOfWeekDto, true, id + " id'li gün bulundu.");
			}

			return new DataResult<DayOfWeekDto>(false, id + " id'li gün bulunamadı.");
		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<DayOfWeekDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<DayOfWeekDto> updateNameById(long id, String name) {
		// TODO Auto-generated method stub
		try {
			Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(id);
			if (!(dayOfWeek.equals(Optional.empty()))) {
				dayOfWeek.get().setName(name);

				dayOfWeekDao.save(dayOfWeek.get());

				DayOfWeekDto dayOfWeekDto = new DayOfWeekDto();
				dayOfWeekDto.setId(dayOfWeek.get().getId());
				dayOfWeekDto.setName(dayOfWeek.get().getName());

				return new DataResult<DayOfWeekDto>(dayOfWeekDto, true, id + " id'li günün adı güncellendi.");
			}

			return new DataResult<DayOfWeekDto>(false, id + " id'li gün bulunamadı.");

		} catch (Exception e) {
			// TODO: handle exception
			return new DataResult<DayOfWeekDto>(false, e.getMessage());
		}

	}

	@Override
	public DataResult<Long> getCount() {
		// TODO Auto-generated method stub
		try {
			return new DataResult<Long>(dayOfWeekDao.count(), true, "Günlerin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}
	}

}

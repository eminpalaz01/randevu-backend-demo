package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.DayOfWeekService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.requests.DayOfWeekSaveRequest;
import com.dershaneproject.randevu.dto.responses.DayOfWeekSaveResponse;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DayOfWeekManager implements DayOfWeekService {

	private final DayOfWeekDao dayOfWeekDao;

	@Override
	public DataResult<DayOfWeekSaveResponse> save(DayOfWeekSaveRequest dayOfWeekSaveRequest) throws BusinessException {
		DayOfWeek dayOfWeek = new DayOfWeek();
		dayOfWeek.setName(dayOfWeekSaveRequest.getName());

		DayOfWeekSaveResponse dayOfWeekSaveResponse = new DayOfWeekSaveResponse();
		dayOfWeekSaveResponse.setId(dayOfWeekDao.save(dayOfWeek).getId());
		return new DataResult<DayOfWeekSaveResponse>(dayOfWeekSaveResponse, "Veritabanına kaydedildi.");
	}

	@Override
	public Result deleteById(long id) throws BusinessException {
		Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(id);
		if (dayOfWeek.isPresent()) {
			dayOfWeekDao.deleteById(id);
			return new Result(id + " id'li silme işlemi başarılı.");
		}

		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + "id'li silme işlemi başarısız"));
	}

	@Override
	public DataResult<List<DayOfWeekDto>> findAll() throws BusinessException {
		List<DayOfWeek> daysOfWeek = dayOfWeekDao.findAll();
		if (daysOfWeek.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Kayıtlı Gün bulunamadı."));

		List<DayOfWeekDto> daysOfWeekDto = new ArrayList<>();
		daysOfWeek.forEach(dayOfWeek -> {
				DayOfWeekDto dayOfWeekDto = new DayOfWeekDto();
				dayOfWeekDto.setId(dayOfWeek.getId());
				dayOfWeekDto.setName(dayOfWeek.getName());

				daysOfWeekDto.add(dayOfWeekDto);
			});
		return new DataResult<List<DayOfWeekDto>>(daysOfWeekDto, "Tüm günler getirildi.");
	}

	@Override
	public DataResult<DayOfWeekDto> findById(long id) throws BusinessException {
		Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(id);
		if (dayOfWeek.isPresent()) {
			DayOfWeekDto dayOfWeekDto = new DayOfWeekDto();
			dayOfWeekDto.setId(dayOfWeek.get().getId());
			dayOfWeekDto.setName(dayOfWeek.get().getName());
			return new DataResult<DayOfWeekDto>(dayOfWeekDto, id + " id'li gün bulundu.");
		}
		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li gün bulunamadı."));
	}

	@Override
	public DataResult<DayOfWeekDto> updateNameById(long id, String name) throws BusinessException {
		Optional<DayOfWeek> dayOfWeek = dayOfWeekDao.findById(id);
		if (dayOfWeek.isPresent()) {
			dayOfWeek.get().setName(name);

			dayOfWeekDao.save(dayOfWeek.get());

			DayOfWeekDto dayOfWeekDto = new DayOfWeekDto();
			dayOfWeekDto.setId(dayOfWeek.get().getId());
			dayOfWeekDto.setName(dayOfWeek.get().getName());

			return new DataResult<DayOfWeekDto>(dayOfWeekDto, id + " id'li günün adı güncellendi.");
		}

		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(" id'li gün bulunamadı."));
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(dayOfWeekDao.count(), "Günlerin sayısı getirildi.");
	}

}

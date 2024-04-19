package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.HourService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.requests.HourSaveRequest;
import com.dershaneproject.randevu.dto.responses.HourSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HourManager implements HourService {

	private final HourDao hourDao;

	@Override
	public DataResult<HourSaveResponse> save(HourSaveRequest hourSaveRequest) {
		Hour hour = new Hour();
		hour.setTime(hourSaveRequest.getTime());
		Long hourId = hourDao.save(hour).getId();

		HourSaveResponse HourSaveResponse = new HourSaveResponse();
		HourSaveResponse.setTime(hourSaveRequest.getTime());
		HourSaveResponse.setId(hourId);

		return new DataResult<HourSaveResponse>(HourSaveResponse, "Veritabanına kaydedildi.");
	}

	@Override
	public Result deleteById(long id) throws BusinessException {
		Optional<Hour> hour = hourDao.findById(id);
		if (hour.isPresent()) {
			hourDao.deleteById(id);
			return new Result(id + " id'li silme işlemi başarılı.");
		}

		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li saat bulunamadı."));
	}

	@Override
	public DataResult<List<HourDto>> findAll() throws BusinessException {
		List<Hour> hours = hourDao.findAll();
		if (hours.isEmpty())
			throw new BusinessException(HttpStatus.NOT_FOUND, List.of("Kayıtlı saat bulunamadı."));

		List<HourDto> hoursDto = new ArrayList<HourDto>();
		hours.forEach(hour -> {
			HourDto hourDto = new HourDto();
			hourDto.setId(hour.getId());
			hourDto.setTime(hour.getTime());

			hoursDto.add(hourDto);
		});
		return new DataResult<List<HourDto>>(hoursDto, "Tüm saatler getirildi.");
	}

	@Override
	public DataResult<HourDto> findById(long id) throws BusinessException {
		Optional<Hour> hour = hourDao.findById(id);
		if (hour.isPresent()) {
			HourDto hourDto = new HourDto();
			hourDto.setId(hour.get().getId());
			hourDto.setTime(hour.get().getTime());

			return new DataResult<HourDto>(hourDto, id + " id'li saat bulundu.");
		}

		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li saat bulunamadı."));
	}

	@Override
	public DataResult<HourDto> updateTimeById(long id, LocalTime time) throws BusinessException {
		Optional<Hour> hour = hourDao.findById(id);
		if (hour.isPresent()) {
			hour.get().setTime(time);

			hourDao.save(hour.get());

			HourDto hourDto = new HourDto();
			hourDto.setId(hour.get().getId());
			hourDto.setTime(hour.get().getTime());

			return new DataResult<HourDto>(hourDto, id + " id'li saat güncellendi.");
		}

		throw new BusinessException(HttpStatus.NOT_FOUND, List.of(id + " id'li saat bulunamadı."));
	}

	@Override
	public DataResult<Long> getCount() {
		return new DataResult<Long>(hourDao.count(), "Saatlerin sayısı getirildi.");
	}

}

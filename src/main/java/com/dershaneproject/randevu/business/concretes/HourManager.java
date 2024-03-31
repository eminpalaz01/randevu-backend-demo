package com.dershaneproject.randevu.business.concretes;

import com.dershaneproject.randevu.business.abstracts.HourService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.requests.HourSaveRequest;
import com.dershaneproject.randevu.dto.responses.HourSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Hour;
import lombok.RequiredArgsConstructor;
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
		try {
			Hour hour = new Hour();
			hour.setTime(hourSaveRequest.getTime());
			Long hourId = hourDao.save(hour).getId();

			HourSaveResponse HourSaveResponse = new HourSaveResponse();
			HourSaveResponse.setTime(hourSaveRequest.getTime());
			HourSaveResponse.setId(hourId);

			return new DataResult<HourSaveResponse>(HourSaveResponse, true, "Veritabanına kaydedildi.");
		} catch (Exception e) {
			return new DataResult<HourSaveResponse>(false, e.getMessage());
		}
	}

	@Override
	public Result deleteById(long id) {
		try {
			Optional<Hour> hour = hourDao.findById(id);
			if (!(hour.equals(Optional.empty()))) {
				hourDao.deleteById(id);
				return new Result(true, id + " id'li silme işlemi başarılı.");
			}

			return new Result(false, id + " id'li saat bulunamadı.");
		} catch (Exception e) {
			return new Result(false, e.getMessage());
		}
	}

	@Override
	public DataResult<List<HourDto>> findAll() {
		try {
			List<Hour> hours = hourDao.findAll();
			if (hours.size() != 0) {
				List<HourDto> hoursDto = new ArrayList<HourDto>();

				hours.forEach(hour -> {
					HourDto hourDto = new HourDto();
					hourDto.setId(hour.getId());
					hourDto.setTime(hour.getTime());

					hoursDto.add(hourDto);
				});
				return new DataResult<List<HourDto>>(hoursDto, true, "Tüm saatler getirildi.");
			} else {
				return new DataResult<List<HourDto>>(false, "Kayıtlı saat bulunamadı.");
			}
		} catch (Exception e) {
			return new DataResult<List<HourDto>>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<HourDto> findById(long id) {
		try {
			Optional<Hour> hour = hourDao.findById(id);
			if (!(hour.equals(Optional.empty()))) {
				HourDto hourDto = new HourDto();
				hourDto.setId(hour.get().getId());
				hourDto.setTime(hour.get().getTime());

				return new DataResult<HourDto>(hourDto, true, id + " id'li saat bulundu.");
			}

			return new DataResult<HourDto>(false, id + " id'li saat bulunamadı.");
		} catch (Exception e) {
			return new DataResult<HourDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<HourDto> updateTimeById(long id, LocalTime time) {
		try {
			Optional<Hour> hour = hourDao.findById(id);
			if (!(hour.equals(Optional.empty()))) {
				hour.get().setTime(time);

				hourDao.save(hour.get());

				HourDto hourDto = new HourDto();
				hourDto.setId(hour.get().getId());
				hourDto.setTime(hour.get().getTime());

				return new DataResult<HourDto>(hourDto, true, id + " id'li saat güncellendi.");
			}

			return new DataResult<HourDto>(false, id + " id'li saat bulunamadı.");

		} catch (Exception e) {
			return new DataResult<HourDto>(false, e.getMessage());
		}
	}

	@Override
	public DataResult<Long> getCount() {
		try {
			return new DataResult<Long>(hourDao.count(), true, "Saatlerin sayısı getirildi.");
		} catch (Exception e) {
			return new DataResult<Long>(false, e.getMessage());
		}	}

}

package com.dershaneproject.randevu.validations.concretes;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemWorkerDao;
import com.dershaneproject.randevu.dataAccess.abstracts.TeacherDao;
import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;

@RequiredArgsConstructor
@Component
public class ScheduleValidationServiceImpl implements ScheduleValidationService {

	private final TeacherDao teacherDao;
	private final DayOfWeekDao dayOfWeekDao;
	private final HourDao hourDao;
	private final SystemWorkerDao systemWorkerDao;

	@Override
	public Result isValidateResult(ScheduleDto scheduleDto) {
		// TODO Auto-generated method stub
		StringBuffer fieldErrorMessage = new StringBuffer("Program oluşturulamaz girdiğiniz");
		String messageSuccess = "Program ın oluşturulmasında bir sorun yok.";

		Boolean isFullEmpty = scheduleDto.getFull();
		boolean isExistsTeacher = teacherDao.existsById(scheduleDto.getTeacherId());
		boolean isExistsDayOfWeek = dayOfWeekDao.existsById(scheduleDto.getDayOfWeek().getId());
		boolean isExistsHour = hourDao.existsById(scheduleDto.getHour().getId());
		boolean isExistsSystemWorker = systemWorkerDao.existsById(scheduleDto.getLastUpdateDateSystemWorker().getId());

		if (!isExistsTeacher || !isExistsDayOfWeek || !isExistsHour || !isExistsSystemWorker || isFullEmpty == null) {
			List<String> errorFields = new ArrayList<>(5);

			if (!isExistsTeacher) {
				errorFields.add("Öğretmen");
			}
			if (!isExistsDayOfWeek) {
				errorFields.add("Gün");
			}
			if (!isExistsHour) {
				errorFields.add("Saat");
			}
			if (!isExistsSystemWorker) {
				errorFields.add("Sistem Çalışanı");
			}
			if (isFullEmpty == null) {
				errorFields.add("Doluluk değeri");
			}

			// This algorithm writes in a readable form
			for (int i = 0; i < errorFields.size(); i++) {
				if (errorFields.size() - 1 == i) {
					fieldErrorMessage.append(" " + errorFields.get(i));
					break;
				}
				fieldErrorMessage.append(" " + errorFields.get(i) + ",");
			}

			fieldErrorMessage.append(" değerleri sistemde bulunamadı kontrol ediniz.");

			return new Result(false, fieldErrorMessage.toString());
		}

		return new Result(true, messageSuccess);

	}

	@Override
	public Result areValidateForCreateTeacherResult(List<ScheduleDto> schedulesDto) {
		// TODO Auto-generated method stub
		StringBuffer fieldErrorMessage = new StringBuffer("Programların biri veya bazıları oluşturulamaz girdiğiniz bazı");
		String messageSuccess = "Programlar'ın oluşturulmasında bir sorun yok.";

		for(ScheduleDto scheduleDto:schedulesDto) {
			
		boolean isExistsTeacher = teacherDao.existsById(scheduleDto.getTeacherId());
		boolean isExistsDayOfWeek = dayOfWeekDao.existsById(scheduleDto.getDayOfWeek().getId());
		boolean isExistsHour = hourDao.existsById(scheduleDto.getHour().getId());

		if (!isExistsTeacher || !isExistsDayOfWeek || !isExistsHour) {
			List<String> errorFields = new ArrayList<>(3);

			if (!isExistsTeacher) {
				errorFields.add("Öğretmen");
			}
			if (!isExistsDayOfWeek) {
				errorFields.add("Gün");
			}
			if (!isExistsHour) {
				errorFields.add("Saat");
			}

			// This algorithm writes in a readable form
			for (int i = 0; i < errorFields.size(); i++) {
				if (errorFields.size() - 1 == i) {
					fieldErrorMessage.append(" " + errorFields.get(i));
				}
				fieldErrorMessage.append(" " + errorFields.get(i) + ",");
			}

			fieldErrorMessage.append(" değerleri sistemde bulunamadı kontrol ediniz.");

			return new Result(false, fieldErrorMessage.toString());
		}
		
	  }

		return new Result(true, messageSuccess);

	}

}

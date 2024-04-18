package com.dershaneproject.randevu.validations.concretes;

import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemWorkerDao;
import com.dershaneproject.randevu.dataAccess.abstracts.TeacherDao;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.validations.abstracts.ScheduleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ScheduleValidationServiceImpl implements ScheduleValidationService {

	private final TeacherDao teacherDao;
	private final DayOfWeekDao dayOfWeekDao;
	private final HourDao hourDao;
	private final SystemWorkerDao systemWorkerDao;

	@Override
	public Result isValidateResult(ScheduleSaveRequest scheduleSaveRequest) throws BusinessException {
		return isValidateResult(scheduleSaveRequest.getFull(), scheduleSaveRequest.getTeacherId(), scheduleSaveRequest.getLastUpdateDateSystemWorkerId(), scheduleSaveRequest.getDayOfWeekId(), scheduleSaveRequest.getHourId());
	}

	private Result isValidateResult(Boolean isFull, Long teacherId, Long lastUpdateSystemWorkerId, Long dayOfWeekId, Long hourId) throws BusinessException {
		StringBuilder fieldErrorMessage = new StringBuilder("Program oluşturulamaz girdiğiniz");
		String messageSuccess = "Program ın oluşturulmasında bir sorun yok.";

		boolean isExistsTeacher = teacherDao.existsById(teacherId);
		boolean isExistsDayOfWeek = dayOfWeekDao.existsById(dayOfWeekId);
		boolean isExistsHour = hourDao.existsById(hourId);
		boolean isExistsSystemWorker = systemWorkerDao.existsById(lastUpdateSystemWorkerId);

		if (!isExistsTeacher || !isExistsDayOfWeek || !isExistsHour || !isExistsSystemWorker || isFull == null) {
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
			if (isFull == null) {
				errorFields.add("Doluluk değeri");
			}

			// This algorithm writes in a readable form
			for (int i = 0; i < errorFields.size(); i++) {
				if (errorFields.size() - 1 == i) {
					fieldErrorMessage.append(" ").append(errorFields.get(i));
					break;
				}
				fieldErrorMessage.append(" ").append(errorFields.get(i)).append(",");
			}
			fieldErrorMessage.append(" değerleri sistemde bulunamadı kontrol ediniz.");
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(fieldErrorMessage.toString()));
		}
		return new Result(messageSuccess);
	}

	@Override
	public Result areValidateResult(List<ScheduleSaveRequest> scheduleSaveRequestList) throws BusinessException {
		String successMessage = "Programlar'ın oluşturulmasında bir sorun yok.";
		String errorMessageFirstPart = "Programların biri veya bazıları oluşturulamaz girdiğiniz bazı";
		String errorMessageLastPart = " değerleri sistemde bulunamadı kontrol ediniz.";
		StringBuilder fieldErrorMessage = new StringBuilder(errorMessageFirstPart);

		for(ScheduleSaveRequest scheduleDto: scheduleSaveRequestList) {
			
		boolean isExistsTeacher = teacherDao.existsById(scheduleDto.getTeacherId());
		boolean isExistsDayOfWeek = dayOfWeekDao.existsById(scheduleDto.getDayOfWeekId());
		boolean isExistsHour = hourDao.existsById(scheduleDto.getHourId());

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
					fieldErrorMessage.append(" ").append(errorFields.get(i));
				}
				fieldErrorMessage.append(" ").append(errorFields.get(i)).append(",");
			}
			fieldErrorMessage.append(errorMessageLastPart);
			throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(fieldErrorMessage.toString()));
		}
	  }
		return new Result(successMessage);
	}
}

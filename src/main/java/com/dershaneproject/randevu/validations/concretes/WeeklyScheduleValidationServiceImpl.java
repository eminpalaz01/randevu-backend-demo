package com.dershaneproject.randevu.validations.concretes;

import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;
import com.dershaneproject.randevu.exceptions.BusinessException;
import com.dershaneproject.randevu.validations.abstracts.WeeklyScheduleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class WeeklyScheduleValidationServiceImpl implements WeeklyScheduleValidationService {

    private final TeacherDao teacherDao;
    private final DayOfWeekDao dayOfWeekDao;
    private final HourDao hourDao;
    private final SystemWorkerDao systemWorkerDao;
    private final StudentDao studentDao;

    @Override
    public Result isValidateResult(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) throws BusinessException {
        StringBuilder fieldErrorMessage = new StringBuilder("Haftalık Program oluşturulamaz girdiğiniz bazı");
        String messageSuccess = "Haftalık Program'ın oluşturulmasında bir sorun yok.";

        Boolean isFullEmpty = weeklyScheduleSaveRequest.getFull();
        boolean isExistsTeacher = teacherDao.existsById(weeklyScheduleSaveRequest.getTeacherId());
        boolean isExistsDayOfWeek = dayOfWeekDao.existsById(weeklyScheduleSaveRequest.getDayOfWeekId());
        boolean isExistsHour = hourDao.existsById(weeklyScheduleSaveRequest.getHourId());
        boolean isExistsSystemWorker = systemWorkerDao.existsById(weeklyScheduleSaveRequest.getLastUpdateDateSystemWorkerId());

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

    public Result studentExistById(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) throws BusinessException {
        StringBuilder fieldErrorMessage = new StringBuilder("Haftalık Program oluşturulamaz girdiğiniz bazı");
        String messageSuccess = "Haftalık Program'ın oluşturulmasında bir sorun yok.";

        boolean isExistsStudent = studentDao.existsById(weeklyScheduleSaveRequest.getStudentId());
        if (!isExistsStudent){
            fieldErrorMessage.append(" öğrenci değerleri sistemde bulunamadı kontrol ediniz.");
            throw new BusinessException(HttpStatus.BAD_REQUEST, List.of(fieldErrorMessage.toString()));
        }
        return new Result(messageSuccess);
    }
}

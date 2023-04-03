package com.dershaneproject.randevu.validations.concretes;

import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dataAccess.abstracts.*;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.validations.abstracts.WeeklyScheduleValidationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class WeeklyScheduleValidationServiceImpl implements WeeklyScheduleValidationService {

    private TeacherDao teacherDao;
    private DayOfWeekDao dayOfWeekDao;
    private HourDao hourDao;
    private SystemWorkerDao systemWorkerDao;
    private StudentDao studentDao;

    @Override
    public Result isValidateResult(WeeklyScheduleDto weeklyScheduleDto) {
        // TODO Auto-generated method stub
        StringBuffer fieldErrorMessage = new StringBuffer("Haftalık Program oluşturulamaz girdiğiniz bazı");
        String messageSuccess = "Haftalık Program'ın oluşturulmasında bir sorun yok.";

        Boolean isFullEmpty = weeklyScheduleDto.getFull();
        boolean isExistsTeacher = teacherDao.existsById(weeklyScheduleDto.getTeacherId());
        boolean isExistsDayOfWeek = dayOfWeekDao.existsById(weeklyScheduleDto.getDayOfWeek().getId());
        boolean isExistsHour = hourDao.existsById(weeklyScheduleDto.getHour().getId());
        boolean isExistsSystemWorker = systemWorkerDao.existsById(weeklyScheduleDto.getLastUpdateDateSystemWorker().getId());

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

        return new Result(true, messageSuccess.toString());

    }

    public Result studentExistById(WeeklyScheduleDto weeklyScheduleDto){
        // TODO Auto-generated method stub
        StringBuffer fieldErrorMessage = new StringBuffer("Haftalık Program oluşturulamaz girdiğiniz bazı");
        String messageSuccess = "Haftalık Program'ın oluşturulmasında bir sorun yok.";

        boolean isExistsStudent = studentDao.existsById(weeklyScheduleDto.getStudentId());

        if (!isExistsStudent){

            fieldErrorMessage.append(" öğrenci değerleri sistemde bulunamadı kontrol ediniz.");

            return new Result(false, fieldErrorMessage.toString());
        }

        return new Result(true, messageSuccess.toString());

    }
}

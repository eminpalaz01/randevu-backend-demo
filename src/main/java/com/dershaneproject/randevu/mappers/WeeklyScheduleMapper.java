package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.dto.requests.WeeklyScheduleSaveRequest;
import com.dershaneproject.randevu.dto.responses.WeeklyScheduleSaveResponse;
import com.dershaneproject.randevu.entities.concretes.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {SystemWorkerMapper.class, DayOfWeekMapper.class , HourMapper.class}
//        ,imports = {Teacher.class}
)
public interface WeeklyScheduleMapper {

    @Mapping(target = "hour", source = "hour")
    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "lastUpdateDateSystemWorker", source = "lastUpdateDateSystemWorker")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "studentId", expression = "java(weeklySchedule.getStudent() != null ? weeklySchedule.getStudent().getId() : null)")
    WeeklyScheduleDto toDto(WeeklySchedule weeklySchedule);

    @Mapping(target = "hour", source = "hour")
    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "lastUpdateDateSystemWorker", source = "lastUpdateDateSystemWorker")
    @Mapping(target = "teacher", expression = "java(createEmptyTeacherWithId(weeklyScheduleDto))")
    @Mapping(target = "student", expression = "java(createEmptyStudentWithId(weeklyScheduleDto))")
    WeeklySchedule toEntity(WeeklyScheduleDto weeklyScheduleDto);

    @Mapping(target = "teacher", expression = "java(createEmptyTeacherWithId(weeklyScheduleSaveRequest))")
    @Mapping(target = "lastUpdateDateSystemWorker", expression = "java(createEmptyLastUpdateDateSystemWorkerWithId(weeklyScheduleSaveRequest))")
    @Mapping(target = "dayOfWeek", expression = "java(createEmptyDayOfWeekWithId(weeklyScheduleSaveRequest))")
    @Mapping(target = "hour", expression = "java(createEmptyHourWithId(weeklyScheduleSaveRequest))")
    @Mapping(target = "student", expression = "java(null)")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createDate", expression = "java(null)")
    @Mapping(target = "lastUpdateDate", expression = "java(null)")
    WeeklySchedule toEntity(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest);

    @Mapping(target = "hour", source = "hour")
    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "lastUpdateDateSystemWorker", source = "lastUpdateDateSystemWorker")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "studentId", expression = "java(weeklySchedule.getStudent() != null ? weeklySchedule.getStudent().getId() : null)")
    WeeklyScheduleSaveResponse toSaveResponse(WeeklySchedule weeklySchedule);

    List<WeeklyScheduleDto> toDtoList(List<WeeklySchedule> weeklyScheduleList);

    List<WeeklySchedule> toEntityList(List<WeeklyScheduleDto> weeklyScheduleDtoList);

    List<WeeklyScheduleSaveResponse> toSaveResponseList(List<WeeklySchedule> weeklyScheduleList);

    List<WeeklySchedule> toEntityListFromSaveRequestList(List<WeeklyScheduleSaveRequest> weeklyScheduleSaveRequestList);


    default Teacher createEmptyTeacherWithId(WeeklyScheduleDto weeklyScheduleDto) {
        return Teacher.createEmptyWithId(weeklyScheduleDto.getTeacherId());
    }
    default Student createEmptyStudentWithId(WeeklyScheduleDto weeklyScheduleDto) {
        return Student.createEmptyWithId(weeklyScheduleDto.getStudentId());
    }
    default Teacher createEmptyTeacherWithId(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) {
        return Teacher.createEmptyWithId(weeklyScheduleSaveRequest.getTeacherId());
    }
    default SystemWorker createEmptyLastUpdateDateSystemWorkerWithId(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) {
        return SystemWorker.createEmptyWithId(weeklyScheduleSaveRequest.getLastUpdateDateSystemWorkerId());
    }
    default DayOfWeek createEmptyDayOfWeekWithId(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) {
        return DayOfWeek.createEmptyWithId(weeklyScheduleSaveRequest.getDayOfWeekId());
    }
    default Hour createEmptyHourWithId(WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) {
        return Hour.createEmptyWithId(weeklyScheduleSaveRequest.getHourId());
    }
}

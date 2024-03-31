package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequest;
import com.dershaneproject.randevu.dto.requests.ScheduleSaveRequestForTeacher;
import com.dershaneproject.randevu.dto.responses.ScheduleSaveResponse;
import com.dershaneproject.randevu.entities.concretes.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {TeacherMapper.class, SystemWorkerMapper.class, DayOfWeekMapper.class ,HourMapper.class}
//        ,imports = {Teacher.class}
)
public interface ScheduleMapper {

    @Mapping(target = "hourDto", source = "hour")
    @Mapping(target = "dayOfWeekDto", source = "dayOfWeek")
    @Mapping(target = "lastUpdateDateSystemWorkerDto", source = "lastUpdateDateSystemWorker")
    @Mapping(target = "teacherId", source = "teacher.id")
    ScheduleDto toDto(Schedule schedule);

    @Mapping(target = "hour", source = "hourDto")
    @Mapping(target = "dayOfWeek", source = "dayOfWeekDto")
    @Mapping(target = "lastUpdateDateSystemWorker", source = "lastUpdateDateSystemWorkerDto")
    @Mapping(target = "teacher", expression = "java(createEmptyTeacherWithId(scheduleDto))")
    Schedule toEntity(ScheduleDto scheduleDto);

    @Mapping(target = "teacher", expression = "java(createEmptyTeacherWithId(scheduleSaveRequest))")
    @Mapping(target = "lastUpdateDateSystemWorker", expression = "java(createEmptyLastUpdateDateSystemWorkerWithId(scheduleSaveRequest))")
    @Mapping(target = "dayOfWeek", expression = "java(createEmptyDayOfWeekWithId(scheduleSaveRequest))")
    @Mapping(target = "hour", expression = "java(createEmptyHourWithId(scheduleSaveRequest))")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "lastUpdateDate", expression = "java(null)")
    @Mapping(target = "createDate", expression = "java(null)")
    Schedule toEntity(ScheduleSaveRequest scheduleSaveRequest);

    @Mapping(target = "hour", source = "hour")
    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "lastUpdateDateSystemWorker", source = "lastUpdateDateSystemWorker")
    @Mapping(target = "teacherId", source = "teacher.id")
    ScheduleSaveResponse toSaveResponse(Schedule schedule);

    @Mapping(target = "lastUpdateDateSystemWorkerId", expression = "java(null)")
    @Mapping(target = "teacherId", expression = "java(null)")
    ScheduleSaveRequest toSaveRequest(ScheduleSaveRequestForTeacher scheduleSaveRequestForTeacher);

    List<ScheduleDto> toDtoList(List<Schedule> scheduleList);

    List<Schedule> toEntityList(List<ScheduleDto> scheduleDtoList);

    List<ScheduleSaveResponse> toSaveResponseList(List<Schedule> scheduleList);

    List<Schedule> toEntityListFromSaveRequestList(List<ScheduleSaveRequest> scheduleSaveRequestList);

    default Teacher createEmptyTeacherWithId(ScheduleDto scheduleDto) {
        return Teacher.createEmptyWithId(scheduleDto.getTeacherId());
    }

    default Teacher createEmptyTeacherWithId(ScheduleSaveRequest scheduleSaveRequest) {
        return Teacher.createEmptyWithId(scheduleSaveRequest.getTeacherId());
    }

    default SystemWorker createEmptyLastUpdateDateSystemWorkerWithId(ScheduleSaveRequest scheduleSaveRequest) {
        return SystemWorker.createEmptyWithId(scheduleSaveRequest.getLastUpdateDateSystemWorkerId());
    }

    default DayOfWeek createEmptyDayOfWeekWithId(ScheduleSaveRequest scheduleSaveRequest) {
        return DayOfWeek.createEmptyWithId(scheduleSaveRequest.getDayOfWeekId());
    }

    default Hour createEmptyHourWithId(ScheduleSaveRequest scheduleSaveRequest) {
        return Hour.createEmptyWithId(scheduleSaveRequest.getHourId());
    }

}

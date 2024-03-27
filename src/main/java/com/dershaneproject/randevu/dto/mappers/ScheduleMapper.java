package com.dershaneproject.randevu.dto.mappers;

import com.dershaneproject.randevu.dto.ScheduleDto;
import com.dershaneproject.randevu.entities.concretes.Schedule;
import com.dershaneproject.randevu.entities.concretes.Teacher;
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

    List<ScheduleDto> toDtoList(List<Schedule> schedules);

    List<Schedule> toEntityList(List<ScheduleDto> schedulesDto);

    default Teacher createEmptyTeacherWithId(ScheduleDto scheduleDto) {
        return Teacher.createEmptyWithId(scheduleDto.getTeacherId());
    }


}

package com.dershaneproject.randevu.dto.mappers;

import com.dershaneproject.randevu.dto.SystemWorkerDto;
import com.dershaneproject.randevu.entities.concretes.SystemWorker;
import com.dershaneproject.randevu.mappers.ScheduleMapper;
import com.dershaneproject.randevu.mappers.WeeklyScheduleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {ScheduleMapper.class, WeeklyScheduleMapper.class})
public interface SystemWorkerMapper {

    @Mapping(target = "schedulesDto", source = "schedules")
    @Mapping(target = "weeklySchedulesDto", source = "weeklySchedules")
    SystemWorkerDto toDto(SystemWorker systemWorker);

    @Mapping(target = "schedules", source = "schedulesDto")
    @Mapping(target = "weeklySchedules", source = "weeklySchedulesDto")
    SystemWorker toEntity(SystemWorkerDto systemWorkerDto);

    List<SystemWorkerDto> toDtoList(List<SystemWorker> systemWorkers);

    List<SystemWorker> toEntityList(List<SystemWorkerDto> systemWorkersDto);
}

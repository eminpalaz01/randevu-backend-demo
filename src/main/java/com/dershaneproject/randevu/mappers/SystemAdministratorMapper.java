package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.entities.concretes.SystemAdministrator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {ScheduleMapper.class, WeeklyScheduleMapper.class})
public interface SystemAdministratorMapper {

    @Mapping(target = "schedulesDto", source = "schedules")
    @Mapping(target = "weeklySchedulesDto", source = "weeklySchedules")
    SystemAdministratorDto toDto(SystemAdministrator systemAdministrator);

    @Mapping(target = "schedules", source = "schedulesDto")
    @Mapping(target = "weeklySchedules", source = "weeklySchedulesDto")
    SystemAdministrator toEntity(SystemAdministratorDto systemAdministratorDto);

    List<SystemAdministratorDto> toDtoList(List<SystemAdministrator> systemAdministrators);

    List<SystemAdministrator> toEntityList(List<SystemAdministratorDto> systemAdministratorsDto);
}

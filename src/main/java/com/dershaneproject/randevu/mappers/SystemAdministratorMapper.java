package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.entities.concretes.SystemAdministrator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SystemAdministratorMapper {

    @Mapping(target = "weeklySchedulesDto",  expression = "java(null)")
    @Mapping(target = "schedulesDto",  expression = "java(null)")
    SystemAdministratorDto toDto(SystemAdministrator systemAdministrator);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    SystemAdministrator toEntity(SystemAdministratorDto systemAdministratorDto);

    List<SystemAdministratorDto> toDtoList(List<SystemAdministrator> systemAdministrators);

    List<SystemAdministrator> toEntityList(List<SystemAdministratorDto> systemAdministratorsDto);
}

package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.dto.requests.SystemAdministratorSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemAdministratorSaveResponse;
import com.dershaneproject.randevu.entities.concretes.SystemAdministrator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SystemAdministratorMapper {

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    SystemAdministratorDto toDto(SystemAdministrator systemAdministrator);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    SystemAdministrator toEntity(SystemAdministratorDto systemAdministratorDto);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createDate", expression = "java(null)")
    @Mapping(target = "lastUpdateDate", expression = "java(null)")
    SystemAdministrator toEntity(SystemAdministratorSaveRequest systemAdministratorSaveRequest);

    SystemAdministratorSaveResponse toSaveResponse(SystemAdministrator systemAdministrator);

    List<SystemAdministratorDto> toDtoList(List<SystemAdministrator> systemAdministratorList);

    List<SystemAdministrator> toEntityList(List<SystemAdministratorDto> systemAdministratorDtoList);

    List<SystemAdministratorSaveResponse> toSaveResponseList(List<SystemAdministrator> systemAdministratorList);

    List<SystemAdministrator> toEntityListFromSaveRequestList(List<SystemAdministratorSaveRequest> systemAdministratorSaveRequestList);
}

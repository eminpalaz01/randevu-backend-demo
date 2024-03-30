package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.SystemStaffDto;
import com.dershaneproject.randevu.dto.requests.SystemStaffSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemStaffSaveResponse;
import com.dershaneproject.randevu.entities.concretes.SystemStaff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SystemStaffMapper {
    @Mapping(target = "weeklySchedulesDto",  expression = "java(null)")
    @Mapping(target = "schedulesDto",  expression = "java(null)")
    SystemStaffDto toDto(SystemStaff systemStaff);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    SystemStaff toEntity(SystemStaffDto systemStaffDto);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createDate", expression = "java(null)")
    @Mapping(target = "lastUpdateDate", expression = "java(null)")
    SystemStaff toEntity(SystemStaffSaveRequest systemStaffSaveRequest);

    SystemStaffSaveResponse toSaveResponse(SystemStaff systemStaff);

    List<SystemStaffDto> toDtoList(List<SystemStaff> systemStaffList);

    List<SystemStaff> toEntityList(List<SystemStaffDto> systemStaffDtoList);

    List<SystemStaffSaveResponse> toSaveResponseList(List<SystemStaff> systemStaffList);

    List<SystemStaff> toEntityListFromSaveRequestList(List<SystemStaffSaveRequest> systemStaffSaveRequestList);
}

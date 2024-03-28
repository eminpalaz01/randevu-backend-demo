package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.SystemStaffDto;
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

    List<SystemStaffDto> toDtoList(List<SystemStaff> systemStaffs);

    List<SystemStaff> toEntityList(List<SystemStaffDto> systemStaffsDto);
}

package com.dershaneproject.randevu.dto.mappers;

import com.dershaneproject.randevu.dto.SystemStaffDto;
import com.dershaneproject.randevu.entities.concretes.SystemStaff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {ScheduleMapper.class, WeeklyScheduleMapper.class})
public interface SystemStaffMapper {

    @Mapping(target = "schedulesDto", source = "schedules")
    @Mapping(target = "weeklySchedulesDto", source = "weeklySchedules")
    SystemStaffDto toDto(SystemStaff systemStaff);

    @Mapping(target = "schedules", source = "schedulesDto")
    @Mapping(target = "weeklySchedules", source = "weeklySchedulesDto")
    SystemStaff toEntity(SystemStaffDto systemStaffDto);

    List<SystemStaffDto> toDtoList(List<SystemStaff> systemStaffs);

    List<SystemStaff> toEntityList(List<SystemStaffDto> systemStaffsDto);
}

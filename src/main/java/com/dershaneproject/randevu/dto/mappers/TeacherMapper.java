package com.dershaneproject.randevu.dto.mappers;

import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.entities.concretes.Department;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {ScheduleMapper.class, WeeklyScheduleMapper.class, DepartmentMapper.class})
public interface TeacherMapper {

    @Mapping(target = "weeklySchedulesDto", source = "weeklySchedules")
    @Mapping(target = "schedulesDto", source = "schedules")
    @Mapping(target = "departmentId", source = "department.id")
    TeacherDto toDto(Teacher teacher);

    @Mapping(target = "weeklySchedules", source = "weeklySchedulesDto")
    @Mapping(target = "schedules", source = "weeklySchedulesDto")
    @Mapping(target = "department", expression = "java(createEmptyDepartmentWithId(teacherDto))")
    Teacher toEntity(TeacherDto teacherDto);

    List<TeacherDto> toDtoList(List<Teacher> teachers);

    List<Teacher> toEntityList(List<TeacherDto> teachersDto);

    default Department createEmptyDepartmentWithId(TeacherDto teacherDto) {
        return Department.createEmptyWithId(teacherDto.getDepartmentId());
    }
}

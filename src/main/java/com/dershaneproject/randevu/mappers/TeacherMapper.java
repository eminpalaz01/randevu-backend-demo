package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.entities.concretes.Department;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "weeklySchedulesDto",  expression = "java(null)")
    @Mapping(target = "schedulesDto",  expression = "java(null)")
    @Mapping(target = "departmentId", expression = "java(teacher.getDepartment() != null ? teacher.getDepartment().getId() : null)")
    TeacherDto toDto(Teacher teacher);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    @Mapping(target = "department", expression = "java(createEmptyDepartmentWithId(teacherDto))")
    Teacher toEntity(TeacherDto teacherDto);

    List<TeacherDto> toDtoList(List<Teacher> teachers);

    List<Teacher> toEntityList(List<TeacherDto> teachersDto);

    default Department createEmptyDepartmentWithId(TeacherDto teacherDto) {
        return Department.createEmptyWithId(teacherDto.getDepartmentId());
    }
}

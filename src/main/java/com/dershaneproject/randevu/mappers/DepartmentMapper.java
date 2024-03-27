package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.entities.concretes.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {TeacherMapper.class})
public interface DepartmentMapper {

    @Mapping(target = "teachersDto", source = "teachers")
    DepartmentDto toDto(Department department);

    @Mapping(target = "teachers", source = "teachersDto")
    Department toEntity(DepartmentDto departmentDto);

    List<DepartmentDto> toDtoList(List<Department> departments);

    List<Department> toEntityList(List<DepartmentDto> departmentsDto);
}

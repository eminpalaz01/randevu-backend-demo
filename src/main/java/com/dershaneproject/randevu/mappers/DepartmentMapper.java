package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.entities.concretes.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" )
public interface DepartmentMapper {

    @Mapping(target = "teachersDto",  expression = "java(null)")
    DepartmentDto toDto(Department department);

    @Mapping(target = "teachers",  expression = "java(null)")
    Department toEntity(DepartmentDto departmentDto);

    List<DepartmentDto> toDtoList(List<Department> departments);

    List<Department> toEntityList(List<DepartmentDto> departmentsDto);
}

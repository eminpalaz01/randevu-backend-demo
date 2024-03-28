package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.dto.requests.DepartmentSaveRequest;
import com.dershaneproject.randevu.dto.responses.DepartmentSaveResponse;
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

    @Mapping(target = "teachers",  expression = "java(null)")
    @Mapping(target = "id", expression = "java(null)")
    Department toEntity(DepartmentSaveRequest departmentSaveRequest);

    DepartmentSaveResponse toSaveResponse(Department department);

    List<DepartmentDto> toDtoList(List<Department> departmentList);

    List<Department> toEntityList(List<DepartmentDto> departmentDtoList);

    List<DepartmentSaveResponse> toSaveResponseList(List<Department> departmentList);

    List<Department> toEntityListFromSaveRequestList(List<DepartmentSaveRequest> departmentSaveRequestList);
}

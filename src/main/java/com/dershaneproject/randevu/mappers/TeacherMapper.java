package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.dto.requests.TeacherSaveRequest;
import com.dershaneproject.randevu.dto.responses.TeacherSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Department;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    @Mapping(target = "departmentId", expression = "java(teacher.getDepartment() != null ? teacher.getDepartment().getId() : null)")
    TeacherDto toDto(Teacher teacher);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    @Mapping(target = "department", expression = "java(createEmptyDepartmentWithId(teacherDto))")
    Teacher toEntity(TeacherDto teacherDto);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    @Mapping(target = "department", expression = "java(createEmptyDepartmentWithId(teacherSaveRequest))")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createDate", expression = "java(null)")
    @Mapping(target = "lastUpdateDate", expression = "java(null)")
    Teacher toEntity(TeacherSaveRequest teacherSaveRequest);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "schedules",  expression = "java(null)")
    @Mapping(target = "departmentId", expression = "java(teacher.getDepartment() != null ? teacher.getDepartment().getId() : null)")
    TeacherSaveResponse toSaveResponse(Teacher teacher);

    List<TeacherDto> toDtoList(List<Teacher> teacherList);

    List<Teacher> toEntityList(List<TeacherDto> teacherDtoList);

    List<TeacherSaveResponse> toSaveResponseList(List<Teacher> teacherList);

    List<Teacher> toEntityListFromSaveRequestList(List<TeacherSaveRequest> teacherSaveRequestList);

    default Department createEmptyDepartmentWithId(TeacherDto teacherDto) {
        return Department.createEmptyWithId(teacherDto.getDepartmentId());
    }

    default Department createEmptyDepartmentWithId(TeacherSaveRequest teacherSaveRequest) {
        return Department.createEmptyWithId(teacherSaveRequest.getDepartmentId());
    }
}

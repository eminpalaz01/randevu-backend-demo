package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.StudentDto;
import com.dershaneproject.randevu.dto.requests.StudentSaveRequest;
import com.dershaneproject.randevu.dto.responses.StudentSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    StudentDto toDto(Student student);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    Student toEntity(StudentDto studentDto);

    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "lastUpdateDate", expression = "java(null)")
    @Mapping(target = "createDate", expression = "java(null)")
    Student toEntity(StudentSaveRequest studentSaveRequest);

    StudentSaveResponse toSaveResponse(Student student);

    List<StudentDto> toDtoList(List<Student> studentList);

    List<Student> toEntityList(List<StudentDto> studentDtoList);

    List<StudentSaveResponse> toSaveResponseList(List<Student> studentList);

    List<Student> toEntityListFromSaveRequestList(List<StudentSaveRequest> studentSaveRequestList);
}

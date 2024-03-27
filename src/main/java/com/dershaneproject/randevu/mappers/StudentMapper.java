package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.StudentDto;
import com.dershaneproject.randevu.entities.concretes.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {WeeklyScheduleMapper.class})
public interface StudentMapper {

    @Mapping(target = "weeklySchedulesDto", source = "weeklySchedules")
    StudentDto toDto(Student student);

    @Mapping(target = "weeklySchedules", source = "weeklySchedulesDto")
    Student toEntity(StudentDto studentDto);

    List<StudentDto> toDtoList(List<Student> students);

    List<Student> toEntityList(List<StudentDto> studentsDto);
}

package com.dershaneproject.randevu.dto.mappers;

import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.entities.concretes.Student;
import com.dershaneproject.randevu.entities.concretes.Teacher;
import com.dershaneproject.randevu.entities.concretes.WeeklySchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" ,
        uses= {TeacherMapper.class, SystemWorkerMapper.class, DayOfWeekMapper.class ,HourMapper.class, StudentMapper.class, DepartmentMapper.class}
//        ,imports = {Teacher.class}
)
public interface WeeklyScheduleMapper {

    @Mapping(target = "hourDto", source = "hour")
    @Mapping(target = "dayOfWeekDto", source = "dayOfWeek")
    @Mapping(target = "lastUpdateDateSystemWorkerDto", source = "lastUpdateDateSystemWorker")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "studentId", expression = "java(weeklySchedule.getStudent() != null ? weeklySchedule.getStudent().getId() : null)")
    WeeklyScheduleDto toDto(WeeklySchedule weeklySchedule);

    @Mapping(target = "hour", source = "hourDto")
    @Mapping(target = "dayOfWeek", source = "dayOfWeekDto")
    @Mapping(target = "lastUpdateDateSystemWorker", source = "lastUpdateDateSystemWorkerDto")
    @Mapping(target = "teacher", expression = "java(createEmptyTeacherWithId(weeklyScheduleDto))")
    @Mapping(target = "student", expression = "java(createEmptyStudentWithId(weeklyScheduleDto))")
    WeeklySchedule toEntity(WeeklyScheduleDto weeklyScheduleDto);

    default Teacher createEmptyTeacherWithId(WeeklyScheduleDto weeklyScheduleDto) {
        return Teacher.createEmptyWithId(weeklyScheduleDto.getTeacherId());
    }

    default Student createEmptyStudentWithId(WeeklyScheduleDto weeklyScheduleDto) {
        return Student.createEmptyWithId(weeklyScheduleDto.getStudentId());
    }

    List<WeeklyScheduleDto> toDtoList(List<WeeklySchedule> weeklySchedules);

    List<WeeklySchedule> toEntityList(List<WeeklyScheduleDto> weeklySchedulesDto);


}

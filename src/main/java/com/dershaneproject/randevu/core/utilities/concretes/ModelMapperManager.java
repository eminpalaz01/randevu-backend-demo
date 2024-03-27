package com.dershaneproject.randevu.core.utilities.concretes;

import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.dto.*;
import com.dershaneproject.randevu.entities.concretes.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ModelMapperManager implements ModelMapperServiceWithTypeMappingConfigs {

	private final ModelMapper modelMapper;

	@Override
	public ModelMapper forResponse() {
		this.modelMapper.getConfiguration().setAmbiguityIgnored(true).setMatchingStrategy(MatchingStrategies.LOOSE);
		return this.modelMapper;
	}

	@Override
	public ModelMapper forRequest() {
		this.modelMapper.getConfiguration().setAmbiguityIgnored(true).setMatchingStrategy(MatchingStrategies.STANDARD);
		return this.modelMapper;
	}

	// while we adding typeMap we should add all of fields
	public void typeMappingConfigForResponse() {
		
       // Schedule
       this.modelMapper.createTypeMap(Schedule.class, ScheduleDto.class)
                .addMappings(mapper -> {
					mapper.map(Schedule::getLastUpdateDateSystemWorker, ScheduleDto::setLastUpdateDateSystemWorkerDto);
					mapper.map(Schedule::getDayOfWeek, ScheduleDto::setDayOfWeekDto);
					mapper.map(Schedule::getHour, ScheduleDto::setHourDto);
					mapper.map(Schedule::getLastUpdateDateSystemWorker, ScheduleDto::setLastUpdateDateSystemWorkerDto);
					mapper.map(src -> src.getTeacher().getId(), ScheduleDto::setTeacherId);
				});
        
        // DayOfWeek
        // Şuanda bir nesnesi yok o yüzden eklenmedi.  

        
        // Hour
        // Şuanda bir nesnesi yok o yüzden eklenmedi.  
        
        
        // Department
		this.modelMapper.createTypeMap(Department.class, DepartmentDto.class)
                .addMappings(mapper -> mapper.map(Department::getTeachers, DepartmentDto::setTeachersDto));

        
        // Teacher
		this.modelMapper.createTypeMap(Teacher.class, TeacherDto.class)
                .addMappings(mapper -> {
					mapper.map(Teacher::getSchedules, TeacherDto::setSchedulesDto);
					mapper.map(Teacher::getWeeklySchedules, TeacherDto::setWeeklySchedulesDto);
				});


        
        // WeeklySchedule
		this.modelMapper.createTypeMap(WeeklySchedule.class, WeeklyScheduleDto.class)
                .addMappings(mapper -> {
					mapper.map(WeeklySchedule::getLastUpdateDateSystemWorker, WeeklyScheduleDto::setLastUpdateDateSystemWorkerDto);
					mapper.map(WeeklySchedule::getDayOfWeek, WeeklyScheduleDto::setDayOfWeekDto);
					mapper.map(WeeklySchedule::getHour, WeeklyScheduleDto::setHourDto);
					mapper.map(WeeklySchedule::getLastUpdateDateSystemWorker, WeeklyScheduleDto::setLastUpdateDateSystemWorkerDto);
					mapper.map(src -> src.getStudent().getId(), WeeklyScheduleDto::setStudentId);
					mapper.map(src -> src.getTeacher().getId(), WeeklyScheduleDto::setTeacherId);
				});

        
        // Student
		this.modelMapper.createTypeMap(Student.class, StudentDto.class)
                .addMappings(mapper -> mapper.map(Student::getWeeklySchedules, StudentDto::setWeeklySchedulesDto));

        
        // SystemAdministrator
		this.modelMapper.createTypeMap(SystemAdministrator.class, SystemAdministratorDto.class)
                .addMappings(mapper -> {
					mapper.map(SystemAdministrator::getSchedules, SystemAdministratorDto::setSchedulesDto);
					mapper.map(SystemAdministrator::getWeeklySchedules, SystemAdministratorDto::setWeeklySchedulesDto);
				});
	
        
        // SystemStaff
		this.modelMapper.createTypeMap(SystemStaff.class, SystemStaffDto.class)
                .addMappings(mapper -> {
					mapper.map(SystemStaff::getSchedules, SystemStaffDto::setSchedulesDto);
					mapper.map(SystemStaff::getWeeklySchedules, SystemStaffDto::setWeeklySchedulesDto);
				});
	
        
        // SystemWorker
		this.modelMapper.createTypeMap(SystemWorker.class, SystemWorkerDto.class)
                .addMappings(mapper -> {
					mapper.map(SystemWorker::getSchedules, SystemWorkerDto::setSchedulesDto);
					mapper.map(SystemWorker::getWeeklySchedules, SystemWorkerDto::setWeeklySchedulesDto);
				});
	}

}

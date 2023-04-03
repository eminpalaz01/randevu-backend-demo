package com.dershaneproject.randevu.core.utilities.concretes;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dershaneproject.randevu.core.utilities.abstracts.ModelMapperServiceWithTypeMappingConfigs;
import com.dershaneproject.randevu.dto.*;
import com.dershaneproject.randevu.entities.concretes.*;

@Service
public class ModelMapperManager implements ModelMapperServiceWithTypeMappingConfigs {

	private ModelMapper modelMapper;
	
	@Autowired
	public ModelMapperManager(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;

		//  adding typeMaps
		// typeMappingConfigForResponse();
	}

	@Override
	public ModelMapper forResponse() {
		// TODO Auto-generated method stub
		this.modelMapper.getConfiguration().setAmbiguityIgnored(true).setMatchingStrategy(MatchingStrategies.LOOSE);
		return this.modelMapper;
	}

	@Override
	public ModelMapper forRequest() {
		// TODO Auto-generated method stub
		this.modelMapper.getConfiguration().setAmbiguityIgnored(true).setMatchingStrategy(MatchingStrategies.STANDARD);
		return this.modelMapper;
	}

	// while we adding typeMap we should add all of fields
	public void typeMappingConfigForResponse() {
		
       // Schedule
       this.modelMapper.createTypeMap(Schedule.class, ScheduleDto.class)
                .addMappings(mapper -> {
					mapper.map(Schedule::getLastUpdateDateSystemWorker, ScheduleDto::setLastUpdateDateSystemWorker);
					mapper.map(Schedule::getDayOfWeek, ScheduleDto::setDayOfWeek);
					mapper.map(Schedule::getHour, ScheduleDto::setHour);
					mapper.map(Schedule::getLastUpdateDateSystemWorker, ScheduleDto::setLastUpdateDateSystemWorker);
					mapper.map(src -> src.getTeacher().getId(), ScheduleDto::setTeacherId);
				});
        
        // DayOfWeek
        // Şuanda bir nesnesi yok o yüzden eklenmedi.  

        
        // Hour
        // Şuanda bir nesnesi yok o yüzden eklenmedi.  
        
        
        // Department
		this.modelMapper.createTypeMap(Department.class, DepartmentDto.class)
                .addMappings(mapper -> mapper.map(Department::getTeachers, DepartmentDto::setTeachers));

        
        // Teacher
		this.modelMapper.createTypeMap(Teacher.class, TeacherDto.class)
                .addMappings(mapper -> {
					mapper.map(Teacher::getSchedules, TeacherDto::setSchedules);
					mapper.map(Teacher::getWeeklySchedules, TeacherDto::setWeeklySchedules);
				});


        
        // WeeklySchedule
		this.modelMapper.createTypeMap(WeeklySchedule.class, WeeklyScheduleDto.class)
                .addMappings(mapper -> {
					mapper.map(WeeklySchedule::getLastUpdateDateSystemWorker, WeeklyScheduleDto::setLastUpdateDateSystemWorker);
					mapper.map(WeeklySchedule::getDayOfWeek, WeeklyScheduleDto::setDayOfWeek);
					mapper.map(WeeklySchedule::getHour, WeeklyScheduleDto::setHour);
					mapper.map(WeeklySchedule::getLastUpdateDateSystemWorker, WeeklyScheduleDto::setLastUpdateDateSystemWorker);
					mapper.map(src -> src.getStudent().getId(), WeeklyScheduleDto::setStudentId);
					mapper.map(src -> src.getTeacher().getId(), WeeklyScheduleDto::setTeacherId);
				});

        
        // Student
		this.modelMapper.createTypeMap(Student.class, StudentDto.class)
                .addMappings(mapper -> {
					mapper.map(Student::getWeeklySchedules, StudentDto::setWeeklySchedules);
				});

        
        // SystemAdministrator
		this.modelMapper.createTypeMap(SystemAdministrator.class, SystemAdministratorDto.class)
                .addMappings(mapper -> {
					mapper.map(SystemAdministrator::getSchedules, SystemAdministratorDto::setSchedules);
					mapper.map(SystemAdministrator::getWeeklySchedules, SystemAdministratorDto::setWeeklySchedules);
				});
	
        
        // SystemStaff
		this.modelMapper.createTypeMap(SystemStaff.class, SystemStaffDto.class)
                .addMappings(mapper -> {
					mapper.map(SystemStaff::getSchedules, SystemStaffDto::setSchedules);
					mapper.map(SystemStaff::getWeeklySchedules, SystemStaffDto::setWeeklySchedules);
				});
	
        
        // SystemWorker
		this.modelMapper.createTypeMap(SystemWorker.class, SystemWorkerDto.class)
                .addMappings(mapper -> {
					mapper.map(SystemWorker::getSchedules, SystemWorkerDto::setSchedules);
					mapper.map(SystemWorker::getWeeklySchedules, SystemWorkerDto::setWeeklySchedules);
				});
	}

}

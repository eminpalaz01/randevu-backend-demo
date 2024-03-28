package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.SystemWorkerDto;
import com.dershaneproject.randevu.entities.concretes.SystemWorker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SystemWorkerMapper {

    @Mapping(target = "schedulesDto",  expression = "java(null)")
    @Mapping(target = "weeklySchedulesDto",  expression = "java(null)")
    SystemWorkerDto toDto(SystemWorker systemWorker);

    @Mapping(target = "schedules",  expression = "java(null)")
    @Mapping(target = "weeklySchedules",  expression = "java(null)")
    SystemWorker toEntity(SystemWorkerDto systemWorkerDto);

//    @Mapping(target = "weeklySchedules",  expression = "java(null)")
//    @Mapping(target = "schedules",  expression = "java(null)")
//    SystemWorker toEntity(SystemWorkerSaveRequest systemWorkerSaveRequest);
//
//    SystemWorkerSaveResponse toSaveResponse(SystemWorker systemWorker);

    List<SystemWorkerDto> toDtoList(List<SystemWorker> systemWorkerList);

    List<SystemWorker> toEntityList(List<SystemWorkerDto> systemWorkerDtoList);
}

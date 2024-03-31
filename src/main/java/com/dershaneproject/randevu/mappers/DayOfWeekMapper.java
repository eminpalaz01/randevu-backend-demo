package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.requests.DayOfWeekSaveRequest;
import com.dershaneproject.randevu.dto.responses.DayOfWeekSaveResponse;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DayOfWeekMapper {

    DayOfWeekDto toDto(DayOfWeek dayOfWeek);

    DayOfWeek toEntity(DayOfWeekDto dayOfWeekDto);

    @Mapping(target = "id", expression = "java(null)")
    DayOfWeek toEntity(DayOfWeekSaveRequest dayOfWeekSaveRequest);

    DayOfWeekSaveResponse toSaveResponse(DayOfWeek dayOfWeek);

    List<DayOfWeekDto> toDtoList(List<DayOfWeek> dayOfWeekList);

    List<DayOfWeek> toEntityList(List<DayOfWeekDto> dayOfWeekDtoList);

    List<DayOfWeekSaveResponse> toSaveResponseList(List<DayOfWeek> dayOfWeekList);

    List<DayOfWeek> toEntityListFromSaveRequestList(List<DayOfWeekSaveRequest> dayOfWeekSaveRequestList);

}

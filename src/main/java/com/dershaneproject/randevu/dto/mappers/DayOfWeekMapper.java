package com.dershaneproject.randevu.dto.mappers;

import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DayOfWeekMapper {

    DayOfWeekDto toDto(DayOfWeek dayOfWeek);

    DayOfWeek toEntity(DayOfWeekDto dayOfWeekDto);

    List<DayOfWeekDto> toDtoList(List<DayOfWeek> daysOfWeek);

    List<DayOfWeek> toEntityList(List<DayOfWeekDto> daysOfWeekDto);
}

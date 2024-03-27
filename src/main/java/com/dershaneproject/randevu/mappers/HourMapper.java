package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.entities.concretes.Hour;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HourMapper {

    HourDto toDto(Hour hour);

    Hour toEntity(HourDto hourDto);

    List<HourDto> toDtoList(List<Hour> hours);

    List<Hour> toEntityList(List<HourDto> hoursDto);
}

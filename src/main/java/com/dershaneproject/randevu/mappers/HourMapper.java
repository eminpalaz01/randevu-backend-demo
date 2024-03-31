package com.dershaneproject.randevu.mappers;

import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.requests.HourSaveRequest;
import com.dershaneproject.randevu.dto.responses.HourSaveResponse;
import com.dershaneproject.randevu.entities.concretes.Hour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HourMapper {

    HourDto toDto(Hour hour);

    Hour toEntity(HourDto hourDto);

    @Mapping(target = "id", expression = "java(null)")
    Hour toEntity(HourSaveRequest hourSaveRequest);

    HourSaveResponse toSaveResponse(Hour hour);

    List<HourDto> toDtoList(List<Hour> hourList);

    List<Hour> toEntityList(List<HourDto> hourDtoList);

    List<HourSaveResponse> toSaveResponseList(List<Hour> hourList);

    List<Hour> toEntityListFromSaveRequestList(List<HourSaveRequest> hourSaveRequestList);
}

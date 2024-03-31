package com.dershaneproject.randevu.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HourSaveRequest {


    @Schema(type = "String", pattern = "HH:mm")
    @JsonFormat(pattern="HH:mm")
    @NotNull
    private LocalTime time;

}

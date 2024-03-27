package com.dershaneproject.randevu.api.controllers.doc;

import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.dto.requests.HourSaveRequest;
import com.dershaneproject.randevu.dto.responses.HourSaveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface IHourController {

    @Operation(summary = "Save hour")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save The Hour",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HourSaveResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content)})
    ResponseEntity<DataResult<HourSaveResponse>> save(@RequestBody HourSaveRequest hourSaveRequest);
}

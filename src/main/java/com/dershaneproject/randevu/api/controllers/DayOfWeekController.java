package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.DayOfWeekService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DayOfWeekDto;
import com.dershaneproject.randevu.dto.requests.DayOfWeekSaveRequest;
import com.dershaneproject.randevu.dto.responses.DayOfWeekSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/days-of-week")
@RequiredArgsConstructor
public class DayOfWeekController {

	private final DayOfWeekService dayOfWeekService;

	@PostMapping
	public ResponseEntity<DataResult<DayOfWeekSaveResponse>> save(@RequestBody DayOfWeekSaveRequest dayOfWeekSaveRequest) throws BusinessException {
		return ResponseEntity.status(HttpStatus.CREATED).body(dayOfWeekService.save(dayOfWeekSaveRequest));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<DayOfWeekDto>>> findAll() throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(dayOfWeekService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<DayOfWeekDto>> findById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(dayOfWeekService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(dayOfWeekService.deleteById(id));
	}

	@PutMapping("/{id}/name")
	public ResponseEntity<DataResult<DayOfWeekDto>> updateNameById(@PathVariable long id,
																   @RequestBody DayOfWeekDto dayOfWeekDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(dayOfWeekService.updateNameById(id, dayOfWeekDto.getName()));
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {
		return ResponseEntity.status(HttpStatus.OK).body(dayOfWeekService.getCount());
	}

}

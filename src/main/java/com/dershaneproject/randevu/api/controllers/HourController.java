package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.HourService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.requests.HourSaveRequest;
import com.dershaneproject.randevu.dto.responses.HourSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hours")
@RequiredArgsConstructor
public class HourController {

	private final HourService hourService;

	@PostMapping
	public ResponseEntity<DataResult<HourSaveResponse>> save(@RequestBody HourSaveRequest hourSaveRequest) {

		return ResponseEntity.ok(hourService.save(hourSaveRequest));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<HourDto>>> findAll() {

		return ResponseEntity.ok(hourService.findAll());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(hourService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<HourDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(hourService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(hourService.deleteById(id));
	}

	@PutMapping("/{id}/time")
	public ResponseEntity<DataResult<HourDto>> updateTimeById(@PathVariable long id, @RequestBody HourDto hourDto) {

		return ResponseEntity.ok(hourService.updateTimeById(id, hourDto.getTime()));
	}

}

package com.dershaneproject.randevu.api.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dershaneproject.randevu.business.abstracts.DayOfWeekService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DayOfWeekDto;

@RestController
@RequestMapping("/api/v1/days-of-week")
@RequiredArgsConstructor
public class DaysOfWeekController {

	private final DayOfWeekService dayOfWeekService;

	@PostMapping
	public ResponseEntity<DataResult<DayOfWeekDto>> save(@RequestBody DayOfWeekDto dayOfWeekDto) {

		return ResponseEntity.ok(dayOfWeekService.save(dayOfWeekDto));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<DayOfWeekDto>>> findAll() {

		return ResponseEntity.ok(dayOfWeekService.findAll());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(dayOfWeekService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<DayOfWeekDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(dayOfWeekService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(dayOfWeekService.deleteById(id));
	}

	@PutMapping("/{id}/name")
	public ResponseEntity<DataResult<DayOfWeekDto>> updateNameById(@PathVariable long id,
			@RequestBody DayOfWeekDto dayOfWeekDto) {

		return ResponseEntity.ok(dayOfWeekService.updateNameById(id, dayOfWeekDto.getName()));
	}

}

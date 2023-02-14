package com.dershaneproject.randevu.api.controllers;

import java.util.List;
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

import com.dershaneproject.randevu.business.abstracts.HourService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.HourDto;

@RestController
@RequestMapping("/api/hour")
public class HoursController {

	private HourService hourService;

	@Autowired
	public HoursController(com.dershaneproject.randevu.business.abstracts.HourService hourService) {
		this.hourService = hourService;
	}

	@PostMapping
	public ResponseEntity<DataResult<HourDto>> save(@RequestBody HourDto hourDto) {

		return ResponseEntity.ok(hourService.save(hourDto));
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

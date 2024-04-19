package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.api.controllers.doc.IHourController;
import com.dershaneproject.randevu.business.abstracts.HourService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.HourDto;
import com.dershaneproject.randevu.dto.requests.HourSaveRequest;
import com.dershaneproject.randevu.dto.responses.HourSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hours")
@RequiredArgsConstructor
public class HourController implements IHourController {

	private final HourService hourService;

	@PostMapping
	public ResponseEntity<DataResult<HourSaveResponse>> save(@RequestBody HourSaveRequest hourSaveRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(hourService.save(hourSaveRequest));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<HourDto>>> findAll() throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(hourService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<HourDto>> findById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(hourService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(hourService.deleteById(id));
	}

	@PutMapping("/{id}/time")
	public ResponseEntity<DataResult<HourDto>> updateTimeById(@PathVariable long id, @RequestBody HourDto hourDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(hourService.updateTimeById(id, hourDto.getTime()));
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {
		return ResponseEntity.status(HttpStatus.OK).body(hourService.getCount());
	}

}

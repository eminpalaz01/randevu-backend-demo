package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.SystemStaffService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemStaffDto;
import com.dershaneproject.randevu.dto.requests.SystemStaffSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemStaffSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system-staffs")
@RequiredArgsConstructor
public class SystemStaffController {

	private final SystemStaffService systemStaffService;

	@PostMapping
	public ResponseEntity<DataResult<SystemStaffSaveResponse>> save(@RequestBody SystemStaffSaveRequest systemStaffSaveRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(systemStaffService.save(systemStaffSaveRequest));
	}
// Performance problem
//	@GetMapping
//	public ResponseEntity<DataResult<List<SystemStaffDto>>> findAll(@RequestParam(required = false, defaultValue = "false") Boolean withSchedules,
//																	@RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) throws BusinessException {
//		if (withSchedules && withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findAllWithAllSchedules());}
//		if (withSchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findAllWithSchedules());}
//		if (withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findAllWithWeeklySchedules());}
//		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findAll());
//	}

	@GetMapping
	public ResponseEntity<DataResult<List<SystemStaffDto>>> findAll() throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findAll());
	}

// Performance problem
//	@GetMapping("/{id}")
//	public ResponseEntity<DataResult<SystemStaffDto>> findById(@PathVariable long id,
//															   @RequestParam(required = false, defaultValue = "false") Boolean withSchedules,
//															   @RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) throws BusinessException {
//		if (withSchedules && withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findByIdWithAllSchedules(id));}
//		if (withSchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findByIdWithSchedules(id));}
//		if (withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findByIdWithWeeklySchedules(id));}
//		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findById(id));
//	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<SystemStaffDto>> findById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.deleteById(id));
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<DataResult<SystemStaffDto>> updateEmailById(@PathVariable long id,
																	  @RequestBody SystemStaffDto systemStaffDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.updateEmailById(id, systemStaffDto.getEmail()));
	}

	@PutMapping("/{id}/user-name")
	public ResponseEntity<DataResult<SystemStaffDto>> updateUserNameById(@PathVariable long id,
																		 @RequestBody SystemStaffDto systemStaffDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.updateUserNameById(id, systemStaffDto.getUserName()));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<DataResult<SystemStaffDto>> updatePasswordById(@PathVariable long id,
																		 @RequestBody SystemStaffDto systemStaffDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.updatePasswordById(id, systemStaffDto.getPassword()));
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {
		return ResponseEntity.status(HttpStatus.OK).body(systemStaffService.getCount());
	}

}

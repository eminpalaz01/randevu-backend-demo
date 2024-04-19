package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.SystemAdministratorService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.dto.requests.SystemAdministratorSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemAdministratorSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system-administrators")
@RequiredArgsConstructor
public class SystemAdministratorController {

	private final SystemAdministratorService systemAdministratorService;

	@PostMapping
	public ResponseEntity<DataResult<SystemAdministratorSaveResponse>> save(@RequestBody SystemAdministratorSaveRequest systemAdministratorSaveRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(systemAdministratorService.save(systemAdministratorSaveRequest));
	}

// Performance problem
//	@GetMapping
//	public ResponseEntity<DataResult<List<SystemAdministratorDto>>> findAll(@RequestParam(required = false, defaultValue = "false") Boolean withSchedules,
//																			@RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) throws BusinessException {
//		if (withSchedules && withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findAllWithAllSchedules());}
//		if (withSchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findAllWithSchedules());}
//		if (withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findAllWithWeeklySchedules());}
//		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findAll());
//	}

	@GetMapping
	public ResponseEntity<DataResult<List<SystemAdministratorDto>>> findAll() throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findAll());
	}

// Performance problem
//	@GetMapping("/{id}")
//	public ResponseEntity<DataResult<SystemAdministratorDto>> findById(@PathVariable long id,
//																	   @RequestParam(required = false, defaultValue = "false") Boolean withSchedules,
//																	   @RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) throws BusinessException {
//		if (withSchedules && withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findByIdWithAllSchedules(id));}
//		if (withSchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findByIdWithSchedules(id));}
//		if (withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findByIdWithWeeklySchedules(id));}
//		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findById(id));
//	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<SystemAdministratorDto>> findById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.deleteById(id));
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updateEmailById(@PathVariable long id,
																			  @RequestBody SystemAdministratorDto systemAdministratorSaveRequest) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.updateEmailById(id, systemAdministratorSaveRequest.getEmail()));
	}

	@PutMapping("/{id}/user-name")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updateUserNameById(@PathVariable long id,
																				 @RequestBody SystemAdministratorDto systemAdministratorSaveRequest) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.updateUserNameById(id, systemAdministratorSaveRequest.getUserName()));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updatePasswordById(@PathVariable long id,
																				 @RequestBody SystemAdministratorDto systemAdministratorSaveRequest) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.updatePasswordById(id, systemAdministratorSaveRequest.getPassword()));
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {
		return ResponseEntity.status(HttpStatus.OK).body(systemAdministratorService.getCount());
	}

}

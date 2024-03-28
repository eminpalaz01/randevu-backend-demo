package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.SystemAdministratorService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemAdministratorDto;
import com.dershaneproject.randevu.dto.requests.SystemAdministratorSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemAdministratorSaveResponse;
import lombok.RequiredArgsConstructor;
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

		return ResponseEntity.ok(systemAdministratorService.save(systemAdministratorSaveRequest));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<SystemAdministratorDto>>> findAll(@RequestParam(required = false, defaultValue = "false") Boolean withSchedules,
																			@RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) {
		if (withSchedules && withWeeklySchedules) { return ResponseEntity.ok(systemAdministratorService.findAllWithAllSchedules());}
		if (withSchedules) { return ResponseEntity.ok(systemAdministratorService.findAllWithSchedules());}
		if (withWeeklySchedules) { return ResponseEntity.ok(systemAdministratorService.findAllWithWeeklySchedules());}
		return ResponseEntity.ok(systemAdministratorService.findAll());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(systemAdministratorService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<SystemAdministratorDto>> findById(@PathVariable long id,
																	   @RequestParam(required = false, defaultValue = "false") Boolean withSchedules,
																	   @RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) {
		if (withSchedules && withWeeklySchedules) { return ResponseEntity.ok(systemAdministratorService.findByIdWithAllSchedules(id));}
		if (withSchedules) { return ResponseEntity.ok(systemAdministratorService.findByIdWithSchedules(id));}
		if (withWeeklySchedules) { return ResponseEntity.ok(systemAdministratorService.findByIdWithWeeklySchedules(id));}
		return ResponseEntity.ok(systemAdministratorService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(systemAdministratorService.deleteById(id));
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updateEmailById(@PathVariable long id,
																			  @RequestBody SystemAdministratorDto systemAdministratorSaveRequest) {

		return ResponseEntity.ok(systemAdministratorService.updateEmailById(id, systemAdministratorSaveRequest.getEmail()));
	}

	@PutMapping("/{id}/user-name")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updateUserNameById(@PathVariable long id,
																				 @RequestBody SystemAdministratorDto systemAdministratorSaveRequest) {

		return ResponseEntity
				.ok(systemAdministratorService.updateUserNameById(id, systemAdministratorSaveRequest.getUserName()));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updatePasswordById(@PathVariable long id,
																				 @RequestBody SystemAdministratorDto systemAdministratorSaveRequest) {

		return ResponseEntity
				.ok(systemAdministratorService.updatePasswordById(id, systemAdministratorSaveRequest.getPassword()));
	}

}
package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.SystemStaffService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemStaffDto;
import com.dershaneproject.randevu.dto.requests.SystemStaffSaveRequest;
import com.dershaneproject.randevu.dto.responses.SystemStaffSaveResponse;
import lombok.RequiredArgsConstructor;
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

		return ResponseEntity.ok(systemStaffService.save(systemStaffSaveRequest));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<SystemStaffDto>>> findAll() {

		return ResponseEntity.ok(systemStaffService.findAll());
	}
	
	@GetMapping("/schedules-and-weekly-schedules")
	public ResponseEntity<DataResult<List<SystemStaffDto>>> findAllWithAllSchedules() {

		return ResponseEntity.ok(systemStaffService.findAllWithAllSchedules());
	}
	
	@GetMapping("/schedules")
	public ResponseEntity<DataResult<List<SystemStaffDto>>> findAllWithSchedules() {

		return ResponseEntity.ok(systemStaffService.findAllWithSchedules());
	}
	
	@GetMapping("/weekly-schedules")
	public ResponseEntity<DataResult<List<SystemStaffDto>>> findAllWithWeeklySchedules() {

		return ResponseEntity.ok(systemStaffService.findAllWithWeeklySchedules());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(systemStaffService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<SystemStaffDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(systemStaffService.findById(id));
	}
	
	@GetMapping("/schedules-and-weekly-schedules/{id}")
	public ResponseEntity<DataResult<SystemStaffDto>> findByIdWithAllSchedules(@PathVariable long id) {

		return ResponseEntity.ok(systemStaffService.findByIdWithAllSchedules(id));
	}
	
	@GetMapping("/schedules/{id}")
	public ResponseEntity<DataResult<SystemStaffDto>> findByIdWithSchedules(@PathVariable long id) {

		return ResponseEntity.ok(systemStaffService.findByIdWithSchedules(id));
	}
	
	@GetMapping("/weekly-schedules/{id}")
	public ResponseEntity<DataResult<SystemStaffDto>> findByIdWithWeeklySchedules(@PathVariable long id) {

		return ResponseEntity.ok(systemStaffService.findByIdWithWeeklySchedules(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(systemStaffService.deleteById(id));
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<DataResult<SystemStaffDto>> updateEmailById(@PathVariable long id,
			@RequestBody SystemStaffDto systemStaffDto) {

		return ResponseEntity.ok(systemStaffService.updateEmailById(id, systemStaffDto.getEmail()));
	}

	@PutMapping("/{id}/user-name")
	public ResponseEntity<DataResult<SystemStaffDto>> updateUserNameById(@PathVariable long id,
			@RequestBody SystemStaffDto systemStaffDto) {

		return ResponseEntity.ok(systemStaffService.updateUserNameById(id, systemStaffDto.getUserName()));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<DataResult<SystemStaffDto>> updatePasswordById(@PathVariable long id,
			@RequestBody SystemStaffDto systemStaffDto) {

		return ResponseEntity.ok(systemStaffService.updatePasswordById(id, systemStaffDto.getPassword()));
	}

}

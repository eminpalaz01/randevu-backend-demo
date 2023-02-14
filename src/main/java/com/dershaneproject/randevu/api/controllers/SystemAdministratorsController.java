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
import com.dershaneproject.randevu.business.abstracts.SystemAdministratorService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.SystemAdministratorDto;

@RestController
@RequestMapping("/api/system-administrator")
public class SystemAdministratorsController {

	private SystemAdministratorService systemAdministratorService;

	@Autowired
	public SystemAdministratorsController(SystemAdministratorService systemAdministratorService) {
		this.systemAdministratorService = systemAdministratorService;
	}

	@PostMapping
	public ResponseEntity<DataResult<SystemAdministratorDto>> save(
			@RequestBody SystemAdministratorDto systemAdministratorDto) {

		return ResponseEntity.ok(systemAdministratorService.save(systemAdministratorDto));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<SystemAdministratorDto>>> findAll() {

		return ResponseEntity.ok(systemAdministratorService.findAll());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(systemAdministratorService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<SystemAdministratorDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(systemAdministratorService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(systemAdministratorService.deleteById(id));
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updateEmailById(@PathVariable long id,
			@RequestBody SystemAdministratorDto systemAdministratorDto) {

		return ResponseEntity.ok(systemAdministratorService.updateEmailById(id, systemAdministratorDto.getEmail()));
	}

	@PutMapping("/{id}/user-name")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updateUserNameById(@PathVariable long id,
			@RequestBody SystemAdministratorDto systemAdministratorDto) {

		return ResponseEntity
				.ok(systemAdministratorService.updateUserNameById(id, systemAdministratorDto.getUserName()));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<DataResult<SystemAdministratorDto>> updatePasswordById(@PathVariable long id,
			@RequestBody SystemAdministratorDto systemAdministratorDto) {

		return ResponseEntity
				.ok(systemAdministratorService.updatePasswordById(id, systemAdministratorDto.getPassword()));
	}

}

package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.DepartmentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.dto.requests.DepartmentSaveRequest;
import com.dershaneproject.randevu.dto.responses.DepartmentSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

	private final DepartmentService departmentService;

	@PostMapping
	public ResponseEntity<DataResult<DepartmentSaveResponse>> save(@RequestBody DepartmentSaveRequest departmentSaveRequest) {

		return ResponseEntity.ok(departmentService.save(departmentSaveRequest));
	}
	
	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(departmentService.getCount());
	}

	@GetMapping("/teachers")
	public ResponseEntity<DataResult<List<DepartmentDto>>> findAllWithTeachers() {
		return ResponseEntity.ok(departmentService.findAllWithTeachers());
	}

	@GetMapping
	public ResponseEntity<DataResult<List<DepartmentDto>>> findAll() {
		return ResponseEntity.ok(departmentService.findAll());
	}

	@GetMapping("/teachers/{id}")
	public ResponseEntity<DataResult<DepartmentDto>> findWithTeachersById(@PathVariable long id) {

		return ResponseEntity.ok(departmentService.findWithTeachersById(id));

	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<DepartmentDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(departmentService.findById(id));

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(departmentService.deleteById(id));

	}

	@PutMapping("/{id}/compressing")
	public ResponseEntity<DataResult<DepartmentDto>> updateCompressingById(@PathVariable long id,
			@RequestBody DepartmentDto departmentDto) {
		return ResponseEntity.ok(departmentService.updateCompressingById(id, departmentDto.getCompressing()));
	}

	@PutMapping("/{id}/name")
	public ResponseEntity<DataResult<DepartmentDto>> updateNameById(@PathVariable long id,
			@RequestBody DepartmentDto departmentDto) {
		return ResponseEntity.ok(departmentService.updateNameById(id, departmentDto.getName()));

	}

}

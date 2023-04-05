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
import com.dershaneproject.randevu.business.abstracts.DepartmentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DepartmentDto;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentsController {

	private final DepartmentService departmentService;

	@PostMapping
	public ResponseEntity<DataResult<DepartmentDto>> save(@RequestBody DepartmentDto departmentDto) {

		return ResponseEntity.ok(departmentService.save(departmentDto));
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

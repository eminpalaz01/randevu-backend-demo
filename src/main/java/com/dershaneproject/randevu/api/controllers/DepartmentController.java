package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.DepartmentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.DepartmentDto;
import com.dershaneproject.randevu.dto.requests.DepartmentSaveRequest;
import com.dershaneproject.randevu.dto.responses.DepartmentSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
		return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.save(departmentSaveRequest));
	}
	
	@GetMapping
	public ResponseEntity<DataResult<List<DepartmentDto>>> findAll(@RequestParam(required = false, defaultValue = "false") boolean withTeachers) throws BusinessException {
		if (withTeachers)
			return ResponseEntity.status(HttpStatus.OK).body(departmentService.findAllWithTeachers());
		return ResponseEntity.status(HttpStatus.OK).body(departmentService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<DepartmentDto>> findById(@PathVariable long id,
															  @RequestParam(required = false, defaultValue = "false") boolean withTeachers) throws BusinessException {
		if (withTeachers)
			return ResponseEntity.status(HttpStatus.OK).body(departmentService.findWithTeachersById(id));
		return ResponseEntity.status(HttpStatus.OK).body(departmentService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(departmentService.deleteById(id));
	}

	@PutMapping("/{id}/compressing")
	public ResponseEntity<DataResult<DepartmentDto>> updateCompressingById(@PathVariable long id,
																		   @RequestBody DepartmentDto departmentDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(departmentService.updateCompressingById(id, departmentDto.getCompressing()));
	}

	@PutMapping("/{id}/name")
	public ResponseEntity<DataResult<DepartmentDto>> updateNameById(@PathVariable long id,
																	@RequestBody DepartmentDto departmentDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(departmentService.updateNameById(id, departmentDto.getName()));
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {
		return ResponseEntity.status(HttpStatus.OK).body(departmentService.getCount());
	}

}

package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.TeacherService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.TeacherDto;
import com.dershaneproject.randevu.dto.requests.TeacherSaveRequest;
import com.dershaneproject.randevu.dto.responses.TeacherSaveResponse;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

	private final TeacherService teacherService;

	@PostMapping
	public ResponseEntity<DataResult<TeacherSaveResponse>> save(@RequestBody TeacherSaveRequest teacherSaveRequest) throws BusinessException {
		return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.save(teacherSaveRequest));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<TeacherDto>>> findAll() throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.findAll());
	}
	
	@GetMapping("/department/{departmentId}")
	public ResponseEntity<DataResult<List<TeacherDto>>> getByDepartmentId(@PathVariable long departmentId) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.getByDepartmentId(departmentId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<TeacherDto>> findById(@PathVariable long id,
														   @RequestParam(required = false, defaultValue = "false") Boolean withSchedules,
														   @RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) throws BusinessException {
		if (withSchedules && withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(teacherService.findByIdWithAllSchedules(id));}
		if (withSchedules) { return ResponseEntity.status(HttpStatus.OK).body(teacherService.findByIdWithSchedules(id));}
		if (withWeeklySchedules) { return ResponseEntity.status(HttpStatus.OK).body(teacherService.findByIdWithWeeklySchedules(id));}
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.deleteById(id));
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<DataResult<TeacherDto>> updateEmailById(@PathVariable long id,
																  @RequestBody TeacherDto teacherDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.updateEmailById(id, teacherDto.getEmail()));
	}

	@PutMapping("/{id}/user-name")
	public ResponseEntity<DataResult<TeacherDto>> updateUserNameById(@PathVariable long id,
																	 @RequestBody TeacherDto teacherDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.updateUserNameById(id, teacherDto.getUserName()));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<DataResult<TeacherDto>> updatePasswordById(@PathVariable long id,
																	 @RequestBody TeacherDto teacherDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.updatePasswordById(id, teacherDto.getPassword()));
	}

	@PutMapping("/{id}/teacher-number")
	public ResponseEntity<DataResult<TeacherDto>> updateTeacherNumberById(@PathVariable long id,
																		  @RequestBody TeacherDto teacherDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.updateTeacherNumberById(id, teacherDto.getTeacherNumber()));
	}

	@PutMapping("/{id}/department")
	public ResponseEntity<DataResult<TeacherDto>> updateDepartmentIdById(@PathVariable long id,
																		 @RequestBody TeacherDto teacherDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.updateDepartmentById(id, teacherDto.getDepartmentId()));
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {
		return ResponseEntity.status(HttpStatus.OK).body(teacherService.getCount());
	}
}

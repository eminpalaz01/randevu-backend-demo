package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.StudentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.StudentDto;
import com.dershaneproject.randevu.dto.requests.StudentSaveRequest;
import com.dershaneproject.randevu.dto.responses.StudentSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@PostMapping
	public ResponseEntity<DataResult<StudentSaveResponse>> save(@RequestBody StudentSaveRequest studentSaveRequest) {

		return ResponseEntity.ok(studentService.save(studentSaveRequest));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<StudentDto>>> findAll(@RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) {
		if (withWeeklySchedules) { return ResponseEntity.ok(studentService.findAllWithWeeklySchedules());}
		return ResponseEntity.ok(studentService.findAll());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(studentService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<StudentDto>> findById(@PathVariable long id,
														   @RequestParam(required = false, defaultValue = "false") Boolean withWeeklySchedules) {
		if (withWeeklySchedules) { return ResponseEntity.ok(studentService.findByIdWithWeeklySchedules(id));}
		return ResponseEntity.ok(studentService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(studentService.deleteById(id));
	}

	@PutMapping("/{id}/student-number")
	public ResponseEntity<DataResult<StudentDto>> updateStudentNumberById(@PathVariable long id,
			@RequestBody StudentDto studentDto) {

		return ResponseEntity.ok(studentService.updateStudentNumberById(id, studentDto.getStudentNumber()));
	}

	@PutMapping("/{id}/user-name")
	public ResponseEntity<DataResult<StudentDto>> updateUserNameById(@PathVariable long id,
			@RequestBody StudentDto studentDto) {

		return ResponseEntity.ok(studentService.updateUserNameById(id, studentDto.getUserName()));
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<DataResult<StudentDto>> updateEmailById(@PathVariable long id,
			@RequestBody StudentDto studentDto) {

		return ResponseEntity.ok(studentService.updateEmailById(id, studentDto.getEmail()));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<DataResult<StudentDto>> updatePasswordById(@PathVariable long id,
			@RequestBody StudentDto studentDto) {

		return ResponseEntity.ok(studentService.updatePasswordById(id, studentDto.getPassword()));
	}

}

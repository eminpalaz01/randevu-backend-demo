package com.dershaneproject.randevu.api.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dershaneproject.randevu.business.abstracts.StudentService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.StudentDto;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@PostMapping
	public ResponseEntity<DataResult<StudentDto>> save(@RequestBody StudentDto studentDto) {

		return ResponseEntity.ok(studentService.save(studentDto));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<StudentDto>>> findAll() {

		return ResponseEntity.ok(studentService.findAll());
	}
	
	@GetMapping("/weekly-schedules")
	public ResponseEntity<DataResult<List<StudentDto>>> findAllWithWeeklySchedules() {

		return ResponseEntity.ok(studentService.findAllWithWeeklySchedules());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(studentService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<StudentDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(studentService.findById(id));
	}
	
	@GetMapping("/weekly-schedules/{id}")
	public ResponseEntity<DataResult<StudentDto>> findByIdWithSchedules(@PathVariable long id) {

		return ResponseEntity.ok(studentService.findByIdWithWeeklySchedules(id));
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

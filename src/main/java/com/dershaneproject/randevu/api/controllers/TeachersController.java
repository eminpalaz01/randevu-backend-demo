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
import com.dershaneproject.randevu.business.abstracts.TeacherService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.TeacherDto;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeachersController {

	private final TeacherService teacherService;

	@PostMapping
	public ResponseEntity<DataResult<TeacherDto>> save(@RequestBody TeacherDto teacherDto) {

		return ResponseEntity.ok(teacherService.save(teacherDto));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<TeacherDto>>> findAll() {

		return ResponseEntity.ok(teacherService.findAll());
	}
	
	@GetMapping("/department/{departmentId}")
	public ResponseEntity<DataResult<List<TeacherDto>>> getByDepartmentId(@PathVariable long departmentId) {

		return ResponseEntity.ok(teacherService.getByDepartmentId(departmentId));
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(teacherService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<TeacherDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(teacherService.findById(id));
	}
	
	@GetMapping("/schedules-and-weekly-schedules/{id}")
	public ResponseEntity<DataResult<TeacherDto>> findByIdWithAllSchedules(@PathVariable long id) {

		return ResponseEntity.ok(teacherService.findByIdWithAllSchedules(id));
	}
	
	@GetMapping("/schedules/{id}")
	public ResponseEntity<DataResult<TeacherDto>> findByIdWithSchedules(@PathVariable long id) {

		return ResponseEntity.ok(teacherService.findByIdWithSchedules(id));
	}
	
	@GetMapping("/weekly-schedules/{id}")
	public ResponseEntity<DataResult<TeacherDto>> findByIdWithWeeklySchedules(@PathVariable long id) {

		return ResponseEntity.ok(teacherService.findByIdWithWeeklySchedules(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(teacherService.deleteById(id));
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<DataResult<TeacherDto>> updateEmailById(@PathVariable long id,
			@RequestBody TeacherDto teacherDto) {

		return ResponseEntity.ok(teacherService.updateEmailById(id, teacherDto.getEmail()));
	}

	@PutMapping("/{id}/user-name")
	public ResponseEntity<DataResult<TeacherDto>> updateUserNameById(@PathVariable long id,
			@RequestBody TeacherDto teacherDto) {

		return ResponseEntity.ok(teacherService.updateUserNameById(id, teacherDto.getUserName()));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<DataResult<TeacherDto>> updatePasswordById(@PathVariable long id,
			@RequestBody TeacherDto teacherDto) {

		return ResponseEntity.ok(teacherService.updatePasswordById(id, teacherDto.getPassword()));
	}

	@PutMapping("/{id}/teacher-number")
	public ResponseEntity<DataResult<TeacherDto>> updateTeacherNumberById(@PathVariable long id,
			@RequestBody TeacherDto teacherDto) {

		return ResponseEntity.ok(teacherService.updateTeacherNumberById(id, teacherDto.getTeacherNumber()));
	}

	@PutMapping("/{id}/department")
	public ResponseEntity<DataResult<TeacherDto>> updateDepartmentIdById(@PathVariable long id,
			@RequestBody TeacherDto teacherDto) {

		return ResponseEntity.ok(teacherService.updateDepartmentById(id, teacherDto.getDepartmentId()));
	}
}

package com.dershaneproject.randevu.api.controllers;

import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;
import com.dershaneproject.randevu.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weekly-schedules")
@RequiredArgsConstructor
public class WeeklyScheduleController {

	private final WeeklyScheduleService weeklyScheduleService;

//	@PostMapping
//	public ResponseEntity<DataResult<WeeklyScheduleSaveResponse>> save(@RequestBody WeeklyScheduleSaveRequest weeklyScheduleSaveRequest) throws BusinessException {
//		return ResponseEntity.status(HttpStatus.CREATED).body(weeklyScheduleService.save(weeklyScheduleSaveRequest));
//	}
//
//	@PostMapping("/all")
//	public ResponseEntity<DataResult<List<WeeklyScheduleSaveResponse>>> saveAll(@RequestBody List<WeeklyScheduleSaveRequest> weeklyScheduleSaveRequestList) throws BusinessException {
//		return ResponseEntity.status(HttpStatus.CREATED).body(weeklyScheduleService.saveAll(weeklyScheduleSaveRequestList));
//	}

	@GetMapping
	public ResponseEntity<DataResult<List<WeeklyScheduleDto>>> findAll() throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.findAll());
	}
	@GetMapping("/teacher/{teacherId}")
	public ResponseEntity<DataResult<List<WeeklyScheduleDto>>> findAllByTeacherId(@PathVariable long teacherId) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.findAllByTeacherId(teacherId));
	}

	@GetMapping("/student/{studentId}")
	public ResponseEntity<DataResult<List<WeeklyScheduleDto>>> findAllByStudentId(@PathVariable long studentId) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.findAllByStudentId(studentId));
	}

	@GetMapping("/system-worker/{systemWorkerId}")
	public ResponseEntity<DataResult<List<WeeklyScheduleDto>>> findAllBySystemWorkerId(@PathVariable long systemWorkerId) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.findAllBySystemWorkerId(systemWorkerId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> findById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.deleteById(id));
	}

	@PutMapping("/{id}/day-of-week")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateDayOfWeekById(@PathVariable long id,
																			 @RequestBody WeeklyScheduleDto weeklyScheduleDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.updateDayOfWeekById(id, weeklyScheduleDto.getDayOfWeek().getId()));
	}

	@PutMapping("/{id}/hour")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateHourById(@PathVariable long id,
																		@RequestBody WeeklyScheduleDto weeklyScheduleDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.updateHourById(id, weeklyScheduleDto.getHour().getId()));
	}

	@PutMapping("/{id}/full")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateFullById(@PathVariable long id,
																		@RequestBody WeeklyScheduleDto weeklyScheduleDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.updateFullById(id, weeklyScheduleDto.getFull()));
	}

	@PutMapping("/{id}/teacher")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateTeacherById(@PathVariable long id,
																		   @RequestBody WeeklyScheduleDto weeklyScheduleDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.updateTeacherById(id, weeklyScheduleDto.getTeacherId()));
	}

	@PutMapping("/{id}/student")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateStudentById(@PathVariable long id,
																		   @RequestBody WeeklyScheduleDto weeklyScheduleDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.updateStudentById(id, weeklyScheduleDto.getStudentId()));
	}

	@PutMapping("/{id}/last-update-date-system-worker")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateLastUpdateDateSystemWorkerById(@PathVariable long id,
																							  @RequestBody WeeklyScheduleDto weeklyScheduleDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService
				.updateLastUpdateDateSystemWorkerById(id, weeklyScheduleDto.getLastUpdateDateSystemWorker().getId()));
	}

	@PutMapping("/{id}/description")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateDescriptionById(@PathVariable long id,
																			   @RequestBody WeeklyScheduleDto weeklyScheduleDto) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.updateDescriptionById(id, weeklyScheduleDto.getDescription()));
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {
		return ResponseEntity.status(HttpStatus.OK).body(weeklyScheduleService.getCount());
	}

}

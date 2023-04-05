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
import com.dershaneproject.randevu.business.abstracts.ScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.ScheduleDto;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class SchedulesController {

	private final ScheduleService scheduleService;

	@PostMapping
	public ResponseEntity<DataResult<ScheduleDto>> save(@RequestBody ScheduleDto scheduleDto) {

		return ResponseEntity.ok(scheduleService.save(scheduleDto));
	}

	@PostMapping("/all")
	public ResponseEntity<DataResult<List<ScheduleDto>>> saveAll(@RequestBody List<ScheduleDto> schedulesDto) {

		return ResponseEntity.ok(scheduleService.saveAll(schedulesDto));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<ScheduleDto>>> findAll() {

		return ResponseEntity.ok(scheduleService.findAll());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(scheduleService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<ScheduleDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(scheduleService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(scheduleService.deleteById(id));
	}

	@PutMapping("/{id}/day-of-week")
	public ResponseEntity<DataResult<ScheduleDto>> updateDayOfWeekById(@PathVariable long id,
			@RequestBody ScheduleDto scheduleDto) {

		return ResponseEntity.ok(scheduleService.updateDayOfWeekById(id, scheduleDto.getDayOfWeek().getId()));
	}

	@PutMapping("/{id}/hour")
	public ResponseEntity<DataResult<ScheduleDto>> updateHourById(@PathVariable long id,
			@RequestBody ScheduleDto scheduleDto) {

		return ResponseEntity.ok(scheduleService.updateHourById(id, scheduleDto.getHour().getId()));
	}

	@PutMapping("/{id}/full")
	public ResponseEntity<DataResult<ScheduleDto>> updateFullById(@PathVariable long id,
			@RequestBody ScheduleDto scheduleDto) {

		return ResponseEntity.ok(scheduleService.updateFullById(id, scheduleDto.getFull()));
	}

	@PutMapping("/{id}/teacher")
	public ResponseEntity<DataResult<ScheduleDto>> updateTeacherById(@PathVariable long id,
			@RequestBody ScheduleDto scheduleDto) {

		return ResponseEntity.ok(scheduleService.updateTeacherById(id, scheduleDto.getTeacherId()));
	}

	@PutMapping("/{id}/last-update-date-system-worker")
	public ResponseEntity<DataResult<ScheduleDto>> updateLastUpdateDateSystemWorkerById(@PathVariable long id,
			@RequestBody ScheduleDto scheduleDto) {

		return ResponseEntity.ok(scheduleService.updateLastUpdateDateSystemWorkerById(id,
				scheduleDto.getLastUpdateDateSystemWorker().getId()));
	}

	@PutMapping("/{id}/description")
	public ResponseEntity<DataResult<ScheduleDto>> updateDescriptionById(@PathVariable long id,
			@RequestBody ScheduleDto scheduleDto) {

		return ResponseEntity.ok(scheduleService.updateDescriptionById(id, scheduleDto.getDescription()));
	}

}

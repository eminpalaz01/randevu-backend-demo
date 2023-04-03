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
import com.dershaneproject.randevu.business.abstracts.WeeklyScheduleService;
import com.dershaneproject.randevu.core.utilities.concretes.DataResult;
import com.dershaneproject.randevu.core.utilities.concretes.Result;
import com.dershaneproject.randevu.dto.WeeklyScheduleDto;

@RestController
@RequestMapping("/api/weekly-schedule")
public class WeeklySchedulesController {

	private WeeklyScheduleService weeklyScheduleService;

	@Autowired
	public WeeklySchedulesController(WeeklyScheduleService weeklyScheduleService) {
		this.weeklyScheduleService = weeklyScheduleService;
	}

	@PostMapping
	public ResponseEntity<DataResult<WeeklyScheduleDto>> save(@RequestBody WeeklyScheduleDto weeklyScheduleDto) {

		return ResponseEntity.ok(weeklyScheduleService.save(weeklyScheduleDto));
	}

	@PostMapping("/all")
	public ResponseEntity<DataResult<List<WeeklyScheduleDto>>> saveAll(@RequestBody List<WeeklyScheduleDto> weeklySchedulesDto) {

		return ResponseEntity.ok(weeklyScheduleService.saveAll(weeklySchedulesDto));
	}

	@GetMapping
	public ResponseEntity<DataResult<List<WeeklyScheduleDto>>> findAll() {

		return ResponseEntity.ok(weeklyScheduleService.findAll());
	}

	@GetMapping("/count")
	public ResponseEntity<DataResult<Long>> getCount() {

		return ResponseEntity.ok(weeklyScheduleService.getCount());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> findById(@PathVariable long id) {

		return ResponseEntity.ok(weeklyScheduleService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Result> deleteById(@PathVariable long id) {

		return ResponseEntity.ok(weeklyScheduleService.deleteById(id));
	}

	@PutMapping("/{id}/day-of-week")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateDayOfWeekById(@PathVariable long id,
			@RequestBody WeeklyScheduleDto weeklyScheduleDto) {

		return ResponseEntity.ok(weeklyScheduleService.updateDayOfWeekById(id, weeklyScheduleDto.getDayOfWeek().getId()));
	}

	@PutMapping("/{id}/hour")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateHourById(@PathVariable long id,
			@RequestBody WeeklyScheduleDto weeklyScheduleDto) {

		return ResponseEntity.ok(weeklyScheduleService.updateHourById(id, weeklyScheduleDto.getHour().getId()));
	}

	@PutMapping("/{id}/full")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateFullById(@PathVariable long id,
			@RequestBody WeeklyScheduleDto weeklyScheduleDto) {

		return ResponseEntity.ok(weeklyScheduleService.updateFullById(id, weeklyScheduleDto.getFull()));
	}

	@PutMapping("/{id}/teacher")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateTeacherById(@PathVariable long id,
			@RequestBody WeeklyScheduleDto weeklyScheduleDto) {

		return ResponseEntity.ok(weeklyScheduleService.updateTeacherById(id, weeklyScheduleDto.getTeacherId()));
	}
	
	@PutMapping("/{id}/student")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateStudentById(@PathVariable long id,
			@RequestBody WeeklyScheduleDto weeklyScheduleDto) {

		return ResponseEntity.ok(weeklyScheduleService.updateStudentById(id, weeklyScheduleDto.getStudentId()));
	}

	@PutMapping("/{id}/last-update-date-system-worker")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateLastUpdateDateSystemWorkerById(@PathVariable long id,
			@RequestBody WeeklyScheduleDto weeklyScheduleDto) {

		return ResponseEntity.ok(weeklyScheduleService.updateLastUpdateDateSystemWorkerById(id,
				weeklyScheduleDto.getLastUpdateDateSystemWorker().getId()));
	}

	@PutMapping("/{id}/description")
	public ResponseEntity<DataResult<WeeklyScheduleDto>> updateDescriptionById(@PathVariable long id,
			@RequestBody WeeklyScheduleDto weeklyScheduleDto) {

		return ResponseEntity.ok(weeklyScheduleService.updateDescriptionById(id, weeklyScheduleDto.getDescription()));
	}

}

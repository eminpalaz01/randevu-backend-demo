package com.dershaneproject.randevu.core.utilities.concretes;

import java.util.Comparator;

import com.dershaneproject.randevu.dto.ScheduleDto;

public class ScheduleDtoComparator implements Comparator<ScheduleDto> {

	@Override
	public int compare(ScheduleDto s1, ScheduleDto s2) { // Compare by day of week first
		Integer s1DayOfWeekId = (int) (s1.getDayOfWeek().getId());
		int dayOfWeekCompare = s1DayOfWeekId.compareTo((int) (s2.getDayOfWeek().getId()));
		
		if(dayOfWeekCompare == 0) {
			Integer s1HourId = (int)(s1.getHour().getId());
			int hourCompare = s1HourId.compareTo((int)(s2.getHour().getId()));
			return hourCompare;
		}
		return dayOfWeekCompare;
	}
}
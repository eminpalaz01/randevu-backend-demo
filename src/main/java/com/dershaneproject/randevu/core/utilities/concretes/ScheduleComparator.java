package com.dershaneproject.randevu.core.utilities.concretes;

import java.util.Comparator;
import com.dershaneproject.randevu.entities.concretes.Schedule;

public class ScheduleComparator implements Comparator<Schedule>{

	@Override
	public int compare(Schedule o1, Schedule o2) {
		// TODO Auto-generated method stub
		Integer s1DayOfWeekId = (int) (o1.getDayOfWeek().getId());
		int dayOfWeekCompare = s1DayOfWeekId.compareTo((int) (o2.getDayOfWeek().getId()));
		
		if(dayOfWeekCompare == 0) {
			Integer s1HourId = (int)(o1.getHour().getId());
			int hourCompare = s1HourId.compareTo((int)(o2.getHour().getId()));
			return hourCompare;
		}
		return dayOfWeekCompare;
	}

}

package model;

import java.util.Date;
import java.util.List;

public class Schedule {
	private int size;
	private List<Point> scheduleLocations;
	private List<Date> scheduleTimes;
	
	public List<Point> getScheduleLocations() {
		return scheduleLocations;
	}
	public void setScheduleLocations(List<Point> scheduleLocations) {
		this.scheduleLocations = scheduleLocations;
	}
	public List<Date> getScheduleTimes() {
		return scheduleTimes;
	}
	public void setScheduleTimes(List<Date> scheduleTimes) {
		this.scheduleTimes = scheduleTimes;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}

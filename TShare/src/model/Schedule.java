package model;

import java.util.List;

public class Schedule {
	private int size;
	private List<Point> scheduleLocations;
	private List<Long> scheduleTimes;
	
	public Schedule(List<Point> scheduleLocations, List<Long> scheduleTimes){
		this.scheduleLocations = scheduleLocations;
		this.scheduleTimes = scheduleTimes;
		this.size = scheduleLocations.size();
	}
	public List<Point> getScheduleLocations() {
		return scheduleLocations;
	}
	public void setScheduleLocations(List<Point> scheduleLocations) {
		this.scheduleLocations = scheduleLocations;
	}
	public List<Long> getScheduleTimes() {
		return scheduleTimes;
	}
	public void setScheduleTimes(List<Long> scheduleTimes) {
		this.scheduleTimes = scheduleTimes;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}

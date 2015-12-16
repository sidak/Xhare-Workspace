package model;

import java.util.List;

public class Schedule {
	private List<Point> scheduleLocations;
	private List<Long> scheduleTimes;
	private List<Long> scheduleSlackTimes;
	private List<Byte> schedulePointTypes; 
	
	public Schedule(List<Point> scheduleLocations, List<Long> scheduleTimes, 
			List<Long> scheduleSlackTimes, List<Byte> schedulePointTypes){
		this.scheduleLocations = scheduleLocations;
		this.scheduleTimes = scheduleTimes;
		this.scheduleSlackTimes = scheduleSlackTimes;
		this.schedulePointTypes = schedulePointTypes;
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
	public List<Long> getScheduleSlackTimes() {
		return scheduleSlackTimes;
	}
	public void setScheduleSlackTimes(List<Long> scheduleSlackTimes) {
		this.scheduleSlackTimes = scheduleSlackTimes;
	}
	public List<Byte> getSchedulePointTypes() {
		return schedulePointTypes;
	}
	public void setSchedulePointTypes(List<Byte> schedulePointTypes) {
		this.schedulePointTypes = schedulePointTypes;
	}
}

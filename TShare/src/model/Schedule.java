package model;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
	private List<Point> scheduleLocations;
	private List<Long> scheduleTimes; // estimated arrival time
	private List<Long> scheduleSlackTimes;
	
	public Schedule(List<Point> scheduleLocations, List<Long> scheduleTimes, 
			List<Long> scheduleSlackTimes){
		this.scheduleLocations = scheduleLocations;
		this.scheduleTimes = scheduleTimes;
		this.scheduleSlackTimes = scheduleSlackTimes;
	}
	public Schedule(){
		this.scheduleLocations = new ArrayList<Point>();
		this.scheduleSlackTimes = new ArrayList<Long>();
		this.scheduleTimes = new ArrayList<Long>();
		
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
	public void copyFrom(Schedule schedule) {
		
		List<Long> slackTimes = schedule.getScheduleSlackTimes();
		List<Long> arrivalTimes = schedule.getScheduleTimes();
		List<Point> locations = schedule.getScheduleLocations();
		
		for(int i=0; i<locations.size(); i++){
			this.scheduleLocations.add(locations.get(i));
			this.scheduleSlackTimes.add(slackTimes.get(i));
			this.scheduleTimes.add(arrivalTimes.get(i));
		}
		
		
	}
	
}

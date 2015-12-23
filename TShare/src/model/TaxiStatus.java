package model;

import java.util.ArrayList;
import java.util.List;

import util.DateTimeHelper;
import util.DistanceHelper;

public class TaxiStatus {
	
	private int id;
	private long timestamp;
	private Point location;
	private int numBookedPassengers;
	private int capacity;
	private Schedule schedule;
	
	public TaxiStatus(){
		
	}
	public TaxiStatus(int id, Point src, Point dest, long timestamp, int capacity){
		this.id = id;
		this.timestamp = timestamp;
		this.location = src;
		this.numBookedPassengers = 0;
		this.capacity = capacity;
		
		long estimatedArrivalTime = DistanceHelper.estimatedDynamicTimeInMilliSeconds(src, dest);
		long slackTime = DateTimeHelper.toMilliSeconds(6.0);
		List<Point> locations = new ArrayList<Point>();
		locations.add(dest);
		List<Long> arrivalTimes = new ArrayList<Long>();
		arrivalTimes.add(estimatedArrivalTime + timestamp);
		List<Long> slackTimes = new ArrayList<Long>();
		slackTimes.add(slackTime);
		
		this.schedule = new Schedule(locations, arrivalTimes, slackTimes);
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public int getNumBookedPassengers() {
		return numBookedPassengers;
	}
	public void setNumBookedPassengers(int numBookedPassengers) {
		this.numBookedPassengers = numBookedPassengers;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	
	
}

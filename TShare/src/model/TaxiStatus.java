package model;

import java.util.Date;
import java.util.List;

public class TaxiStatus {
	
	private int id;
	private Date timestamp;
	private Point location;
	private int numBookedPassengers;
	private int capacity;
	private List<Point> scheduleLocations;
	private List<Date> scheduleTimes;
	
	public TaxiStatus(){
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
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
	
	
	
}

package model;

import java.util.Date;
import java.util.List;

public class TaxiStatus {
	
	private int id;
	private Date timestamp;
	private Point location;
	private int numBoardedPassengers;
	private int numBookedPassengers;
	private int capacity;
	private List<Point> schedule;
	
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
	public int getNumBoardedPassengers() {
		return numBoardedPassengers;
	}
	public void setNumBoardedPassengers(int numBoardedPassengers) {
		this.numBoardedPassengers = numBoardedPassengers;
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
	public List<Point> getSchedule() {
		return schedule;
	}
	public void setSchedule(List<Point> schedule) {
		this.schedule = schedule;
	}
	
	
}

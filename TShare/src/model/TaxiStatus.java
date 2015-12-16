package model;

public class TaxiStatus {
	
	private int id;
	private long timestamp;
	private Point location;
	private int numBookedPassengers;
	private int capacity;
	private Schedule schedule;
	
	public TaxiStatus(){
		
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

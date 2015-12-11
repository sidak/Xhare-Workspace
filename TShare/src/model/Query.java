package model;

import java.util.Date;

public class Query {
	private int id;
	private Date timestamp;
	private Point pickupPoint;
	private Point deliveryPoint;
	private Date[] pickupWindow;
	private Date[] deliveryWindow;
	
	private final int EARLY_BOUND_INDEX = 0;
	private final int LATE_BOUND_INDEX = 1;
	
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
	public Point getPickupPoint() {
		return pickupPoint;
	}
	public void setPickupPoint(Point pickupPoint) {
		this.pickupPoint = pickupPoint;
	}
	public Point getDeliveryPoint() {
		return deliveryPoint;
	}
	public void setDeliveryPoint(Point deliveryPoint) {
		this.deliveryPoint = deliveryPoint;
	}
	public Date[] getPickupWindow() {
		return pickupWindow;
	}
	public void setPickupWindow(Date[] pickupWindow) {
		this.pickupWindow = pickupWindow;
	}
	public Date[] getDeliveryWindow() {
		return deliveryWindow;
	}
	public void setDeliveryWindow(Date[] deliveryWindow) {
		this.deliveryWindow = deliveryWindow;
	}
	
	public Date getPickupWindowEarlyBound(){
		return this.pickupWindow[EARLY_BOUND_INDEX];
	}
	
	public Date getPickupWindowLateBound(){
		return this.pickupWindow[LATE_BOUND_INDEX];
	}
	
	public Date getDeliveryWindowEarlyBound(){
		return this.deliveryWindow[EARLY_BOUND_INDEX];
	}
	
	public Date getDeliveryWindowLateBound(){
		return this.deliveryWindow[LATE_BOUND_INDEX];
	}
	
}

package model;

public class Query {
	private int id;
	private Long timestamp;
	private Point pickupPoint;
	private Point deliveryPoint;
	private Long[] pickupWindow;
	private Long[] deliveryWindow;
	
	private final int EARLY_BOUND_INDEX = 0;
	private final int LATE_BOUND_INDEX = 1;
	
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
	public Long[] getPickupWindow() {
		return pickupWindow;
	}
	public void setPickupWindow(Long[] pickupWindow) {
		this.pickupWindow = pickupWindow;
	}
	public Long[] getDeliveryWindow() {
		return deliveryWindow;
	}
	public void setDeliveryWindow(Long[] deliveryWindow) {
		this.deliveryWindow = deliveryWindow;
	}
	
	public Long getPickupWindowEarlyBound(){
		return this.pickupWindow[EARLY_BOUND_INDEX];
	}
	
	public Long getPickupWindowLateBound(){
		return this.pickupWindow[LATE_BOUND_INDEX];
	}
	
	public Long getDeliveryWindowEarlyBound(){
		return this.deliveryWindow[EARLY_BOUND_INDEX];
	}
	
	public Long getDeliveryWindowLateBound(){
		return this.deliveryWindow[LATE_BOUND_INDEX];
	}
	
}

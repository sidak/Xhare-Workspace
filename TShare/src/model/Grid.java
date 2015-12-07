package model;

public class Grid {
	private int id;
	private double minLat;
	private double maxLat;
	private double minLng;
	private double maxLng;
	private Point center;
	
	public boolean isInGrid(Point pt){
		if(compareRange(pt.getLat(), minLat, maxLat) &&
				compareRange(pt.getLng(), minLng, maxLng)){
			return true;
		}
		return false;
	}
	// ask about the corners of the grid 
	public boolean compareRange(double val, double min, double max){
		if(Double.compare(val, min) >= 0 && Double.compare(val, max) <=0 ){
			return true;
		}
		return false;
	}
	
	public Point getGeoCenter(){
		return new Point((minLat+maxLat)/2, (minLng + maxLng)/2);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getMinLat() {
		return minLat;
	}

	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	public double getMinLng() {
		return minLng;
	}

	public void setMinLng(double minLng) {
		this.minLng = minLng;
	}

	public double getMaxLng() {
		return maxLng;
	}

	public void setMaxLng(double maxLng) {
		this.maxLng = maxLng;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

}

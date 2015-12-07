package model;

public class Edge {
	private Point dest;
	private double dist;
	private double speedLimit;
	
	public Edge(Point v, double dist, double sl){
		dest = v;
		this.dist = dist;
		this.speedLimit = sl;
	}

	public Point getDest() {
		return dest;
	}

	public void setDest(Point dest) {
		this.dest = dest;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public double getSpeedLimit() {
		return speedLimit;
	}

	public void setSpeedLimit(double speedLimit) {
		this.speedLimit = speedLimit;
	}
	
}

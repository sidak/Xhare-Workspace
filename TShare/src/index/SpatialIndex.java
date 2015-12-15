package index;

import util.DoubleHelper;

public class SpatialIndex implements Comparable<SpatialIndex>{
	private int gridIdx;
	private double gridDist;
	
	public SpatialIndex(int gridIdx, double gridDist){
		this.gridIdx = gridIdx;
		this.gridDist = gridDist;
	}
	public int getGridIdx() {
		return gridIdx;
	}
	public void setGridIdx(int gridIdx) {
		this.gridIdx = gridIdx;
	}
	public double getGridDist() {
		return gridDist;
	}
	public void setGridDist(double gridDist) {
		this.gridDist = gridDist;
	}
	
	@Override
	public int compareTo(SpatialIndex spatialIndex) {
		if(DoubleHelper.greaterThan(getGridDist(), spatialIndex.getGridDist())) return 1;
		else if(DoubleHelper.lessThan(getGridDist(), spatialIndex.getGridDist())) return -1;
		else return 0;
	}
}

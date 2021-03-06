package index;

import util.DoubleHelper;

public class TemporalIndex implements Comparable<TemporalIndex> {
	private int gridIdx;
	private double gridTime;
	
	public TemporalIndex(int gridIdx, double gridTime){
		this.gridIdx = gridIdx;
		this.gridTime = gridTime;
	}
	public int getGridIdx() {
		return gridIdx;
	}
	public void setGridIdx(int gridIdx) {
		this.gridIdx = gridIdx;
	}
	public double getGridTime() {
		return gridTime;
	}
	public void setGridTime(double gridTime) {
		this.gridTime = gridTime;
	}
	@Override
	public int compareTo(TemporalIndex temporalIndex) {
		if(DoubleHelper.greaterThan(getGridTime(), temporalIndex.getGridTime())) return 1;
		else if(DoubleHelper.lessThan(getGridTime(), temporalIndex.getGridTime())) return -1;
		else return 0;
	}
	
	
}

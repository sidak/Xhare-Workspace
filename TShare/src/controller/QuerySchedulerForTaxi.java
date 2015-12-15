package controller;

import java.util.ArrayList;
import java.util.List;

import model.Point;
import model.Query;
import model.TaxiStatus;
import util.DateTimeHelper;
import util.DistanceHelper;

public class QuerySchedulerForTaxi {
	private Query query;
	private TaxiStatus taxiStatus;
	private List<Integer> queryInsertionPositions;
	
	
	public QuerySchedulerForTaxi(Query query, TaxiStatus taxiStatus){
		this.query = query;
		this.taxiStatus = taxiStatus;
		queryInsertionPositions = scheduleQuery();
	}
	
	public int getMinDistanceIncrease(){
		return calcDistIncrease(queryInsertionPositions.get(0), queryInsertionPositions.get(1));
	}
	 
	public List<Integer> scheduleQuery(){
		List<Integer> insertionPositions = new ArrayList<Integer>();
		
		int scheduleSize = taxiStatus.getSchedule().getSize();
		
		int minDistanceIncrease = Integer.MAX_VALUE;
		int querySrcInsertionIdx = -1;
		int queryDestInsertionIdx = -1;
		
		for(int i=0; i<=scheduleSize; i++){
			for(int j = i+1; j<=scheduleSize+1; j++){
					
				if(isInsertionFeasible(i,j)){
					int distanceIncrease = calcDistIncrease(i,j);
					if(distanceIncrease < minDistanceIncrease){
						minDistanceIncrease = distanceIncrease;
						querySrcInsertionIdx = i;
						queryDestInsertionIdx = j;
					}
				}
				
			}
		}
		
		insertionPositions.add(querySrcInsertionIdx);
		insertionPositions.add(queryDestInsertionIdx);
		
		return insertionPositions;
	}
					
	public void insertInSchedule(int querySrcInsertionIdx, int queryDestInsertionIdx) {
		taxiStatus.getSchedule().getScheduleLocations()
			.add(queryInsertionPositions.get(0), query.getPickupPoint());
		taxiStatus.getSchedule().getScheduleLocations()
		.add(queryInsertionPositions.get(1), query.getDeliveryPoint());
		// TODO: insert times
	}

	private int calcDistIncrease(int i, int j) {
		// TODO Auto-generated method stub
		
		return 0;
	}

	
	private boolean canReachQueryPickupPointInTime(){
		long currTime = System.currentTimeMillis();
		long timeToSrcFromTaxiLoc = DateTimeHelper.toMilliSeconds(
				DistanceHelper.estimatedDynamicDistance(
						taxiStatus.getLocation(),
						query.getPickupPoint()
					)
				);
		
		if((currTime + timeToSrcFromTaxiLoc) <= DateTimeHelper.toMilliSeconds(query.getPickupWindowLateBound())){
			return true;
		}
		else return false;
		
	}
	
	private void removePoint(int i, Point pickupPoint) {
		// TODO Auto-generated method stub
		taxiStatus.getSchedule().getScheduleLocations().add(i, pickupPoint);
		//taxiStatus.getScheduleTimes().add(i, element);
		
	}

	private void insertPoint(Point pickupPoint) {
		// TODO Auto-generated method stub
		
		
	}
	
	/**
	 * Checks if Q.o and Q.d can be inserted without disturbing subsequent points in the schedule
	 * @param i insertion position for Q.o
	 * @param j insertion position for Q.d
	 * @return True, if insertion is feasible. Otherwise false.
	 */
	public boolean isInsertionFeasible(int i, int j) {
		
		if(!canReachQueryPickupPointInTime()){
			return false;
		}
		int timeDelayBySrcInsertion = calcTimeDelayByInsertion(query.getPickupPoint(), i);
		
		if(isSubsequentScheduleDestroyed(timeDelayBySrcInsertion, i)){
			return false;
		}
		double scheduledArrivalTimeDest = DistanceHelper.estimatedDynamicTimeInHours(taxiStatus.getSchedule().getScheduleLocations().get(j), query.getPickupPoint());
		Point prevLoc = taxiStatus.getSchedule().getScheduleLocations().get(j);
		if(!canReachQueryDeliveryPointInTime(scheduledArrivalTimeDest, prevLoc)){
			return false;
		}
		
		int timeDelayByDestInsertion = calcTimeDelayByInsertion(query.getDeliveryPoint(), j);
		
		if(isSubsequentScheduleDestroyed(timeDelayByDestInsertion, j)){
			return false;
		}
		// TODO: check line 12 comment of algo
		return true;
	}
	
	private boolean canReachQueryDeliveryPointInTime(double scheduledArrivalTimeDest, Point prevLoc) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isSubsequentScheduleDestroyed(int timeDelayBySrcInsertion, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	private int calcTimeDelayByInsertion(Point pickupPoint, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean canScheduleQuery(){
		if(queryInsertionPositions.get(0) == -1 && queryInsertionPositions.get(1) == -1){
			return false;
		}
		else return true;
	}
}

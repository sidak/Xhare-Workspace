package controller;

import java.util.ArrayList;
import java.util.List;

import model.Point;
import model.Query;
import model.Schedule;
import model.TaxiStatus;
import util.DateTimeHelper;
import util.DistanceHelper;
import util.DoubleHelper;

public class QuerySchedulerForTaxi {
	private Query query;
	private TaxiStatus taxiStatus;
	private List<Integer> queryInsertionPositions;
	
	
	public QuerySchedulerForTaxi(Query query, TaxiStatus taxiStatus){
		this.query = query;
		this.taxiStatus = taxiStatus;
		queryInsertionPositions = scheduleQuery();
	}
	
	public double getMinDistanceIncrease(){
		return calcDistIncrease(queryInsertionPositions.get(0), queryInsertionPositions.get(1));
	}
	 
	public List<Integer> scheduleQuery(){
		List<Integer> insertionPositions = new ArrayList<Integer>();
		
		int scheduleSize = taxiStatus.getSchedule().getSize();
		
		double minDistanceIncrease = Double.MAX_VALUE;
		int querySrcInsertionIdx = -1;
		int queryDestInsertionIdx = -1;
		
		for(int i=0; i<=scheduleSize; i++){
			for(int j = i+1; j<=scheduleSize+1; j++){
					
				if(isInsertionFeasible(i,j)){
					double distanceIncrease = calcDistIncrease(i, j);
					if(DoubleHelper.lessThan(distanceIncrease, minDistanceIncrease)){
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
					
	public void insertInSchedule() {
		int querySrcInsertionIdx = queryInsertionPositions.get(0);
		int queryDestInsertionIdx = queryInsertionPositions.get(1);
		Point pickupPoint = query.getPickupPoint();
		Point deliveryPoint = query.getDeliveryPoint();
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		List<Long> scheduleTimes = taxiStatus.getSchedule().getScheduleTimes();
		
		long estimatedArrivalTimeAtSrc, estimatedArrivalTimeAtDest;
		
		if(querySrcInsertionIdx == 0){
			estimatedArrivalTimeAtSrc = DistanceHelper.estimatedDynamicTimeInMilliSeconds(taxiStatus.getLocation(), pickupPoint) + 
					taxiStatus.getTimestamp();
			
		}
		else{
			estimatedArrivalTimeAtSrc = DistanceHelper.estimatedDynamicTimeInMilliSeconds(scheduleLocations.get(querySrcInsertionIdx-1), pickupPoint) +
					scheduleTimes.get(querySrcInsertionIdx-1);
		}
		
		scheduleLocations.add(querySrcInsertionIdx, pickupPoint);
		scheduleTimes.add(querySrcInsertionIdx, estimatedArrivalTimeAtSrc);

		
		estimatedArrivalTimeAtDest = DistanceHelper.estimatedDynamicTimeInMilliSeconds(scheduleLocations.get(queryDestInsertionIdx-1), deliveryPoint) +
				scheduleTimes.get(queryDestInsertionIdx-1);
		
		
		scheduleLocations.add(queryDestInsertionIdx, deliveryPoint);
		scheduleTimes.add(queryDestInsertionIdx, estimatedArrivalTimeAtDest);
		
		taxiStatus.setSchedule(new Schedule(scheduleLocations, scheduleTimes));
	}

	private double calcDistIncrease(int i, int j) {
		
		Point pickupPoint = query.getPickupPoint();
		Point deliveryPoint = query.getDeliveryPoint();
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		int scheduleSize = scheduleLocations.size();
		if(i==0 && j==1){
			return DistanceHelper.estimatedDynamicDistance(pickupPoint, deliveryPoint) + 
				    DistanceHelper.estimatedDynamicDistance(deliveryPoint, scheduleLocations.get(0));
		}
		else if (i==scheduleSize && j == (scheduleSize + 1)){
			return DistanceHelper.estimatedDynamicDistance(pickupPoint, scheduleLocations.get(scheduleSize-1)) +
					DistanceHelper.estimatedDynamicDistance(pickupPoint, deliveryPoint);
		}
		else{
			return DistanceHelper.estimatedDynamicDistance(pickupPoint, scheduleLocations.get(i)) +
					DistanceHelper.estimatedDynamicDistance(scheduleLocations.get(j-1), deliveryPoint);
		}
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

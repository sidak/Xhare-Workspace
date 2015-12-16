package controller;

import java.util.ArrayList;
import java.util.List;

import model.Point;
import model.Query;
import model.Schedule;
import model.TaxiStatus;
import util.DistanceHelper;
import util.DoubleHelper;

public class QuerySchedulerForTaxi {
	private Query query;
	private TaxiStatus taxiStatus;
	private List<Integer> queryInsertionPositions;
	private Schedule bestSchedule;
	
	
	public QuerySchedulerForTaxi(Query query, TaxiStatus taxiStatus){
		this.query = query;
		this.taxiStatus = taxiStatus;
		queryInsertionPositions = scheduleQuery();
	}
	
	public Schedule getBestSchedule() {
		return bestSchedule;
	}
	public double getMinDistanceIncrease(){
		return calcDistIncrease(queryInsertionPositions.get(0), queryInsertionPositions.get(1));
	}
	 
	public List<Integer> scheduleQuery(){
		List<Integer> insertionPositions = new ArrayList<Integer>();
		
		int scheduleSize = taxiStatus.getSchedule().getScheduleLocations().size();
		
		double minDistanceIncrease = Double.MAX_VALUE;
		int querySrcInsertionIdx = -1;
		int queryDestInsertionIdx = -1;
		
		Schedule originalSchedule = taxiStatus.getSchedule();
		
		for(int i=0; i<=scheduleSize; i++){
			for(int j = i+1; j<=scheduleSize+1; j++){
					
				if(isInsertionFeasible(i,j)){
					double distanceIncrease = calcDistIncrease(i, j);
					if(DoubleHelper.lessThan(distanceIncrease, minDistanceIncrease)){
						minDistanceIncrease = distanceIncrease;
						querySrcInsertionIdx = i;
						queryDestInsertionIdx = j;
						bestSchedule = taxiStatus.getSchedule();
					}
					taxiStatus.setSchedule(originalSchedule);
				}
				
			}
		}
		
		insertionPositions.add(querySrcInsertionIdx);
		insertionPositions.add(queryDestInsertionIdx);
		
		return insertionPositions;
	}
	
	/*
	public void insertInSchedule() {
		int querySrcInsertionIdx = queryInsertionPositions.get(0);
		int queryDestInsertionIdx = queryInsertionPositions.get(1);
		
		Point pickupPoint = query.getPickupPoint();
		Point deliveryPoint = query.getDeliveryPoint();
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		List<Long> scheduleTimes = taxiStatus.getSchedule().getScheduleTimes();
		
		long estimatedArrivalTimeAtSrc = findEstimatedArrivalTimeAtSrc(querySrcInsertionIdx);
		
		scheduleLocations.add(querySrcInsertionIdx, pickupPoint);
		scheduleTimes.add(querySrcInsertionIdx, estimatedArrivalTimeAtSrc);
			
		long estimatedArrivalTimeAtDest = findEstimatedArrivalTimeAtDest(queryDestInsertionIdx);
		
		
		scheduleLocations.add(queryDestInsertionIdx, deliveryPoint);
		scheduleTimes.add(queryDestInsertionIdx, estimatedArrivalTimeAtDest);
		
		taxiStatus.setSchedule(new Schedule(scheduleLocations, scheduleTimes));
	}
	*/
	
	private long findEstimatedArrivalTimeAtDest(int queryDestInsertionIdx) {
		Point deliveryPoint = query.getDeliveryPoint();
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		List<Long> scheduleTimes = taxiStatus.getSchedule().getScheduleTimes();
		
		long estimatedArrivalTimeAtDest;
		estimatedArrivalTimeAtDest = DistanceHelper.estimatedDynamicTimeInMilliSeconds(scheduleLocations.get(queryDestInsertionIdx-1), deliveryPoint) +
				scheduleTimes.get(queryDestInsertionIdx-1);
		return estimatedArrivalTimeAtDest;
	}

	private long findEstimatedArrivalTimeAtSrc(int querySrcInsertionIdx) {
		Point pickupPoint = query.getPickupPoint();
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		List<Long> scheduleTimes = taxiStatus.getSchedule().getScheduleTimes();
		
		long estimatedArrivalTimeAtSrc;
		
		if(querySrcInsertionIdx == 0){
			estimatedArrivalTimeAtSrc = DistanceHelper.estimatedDynamicTimeInMilliSeconds(taxiStatus.getLocation(), pickupPoint) + 
					taxiStatus.getTimestamp();
			
		}
		else{
			estimatedArrivalTimeAtSrc = DistanceHelper.estimatedDynamicTimeInMilliSeconds(scheduleLocations.get(querySrcInsertionIdx-1), pickupPoint) +
					scheduleTimes.get(querySrcInsertionIdx-1);
		}
		return estimatedArrivalTimeAtSrc;
	}

	private double calcDistIncrease(int i, int j) {
		Point pickupPoint = query.getPickupPoint();
		Point deliveryPoint = query.getDeliveryPoint();
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		int scheduleSize = scheduleLocations.size();
		if(i==(j-1)){
			if(i==0){
				return DistanceHelper.estimatedDynamicDistance(pickupPoint, deliveryPoint) + 
					    DistanceHelper.estimatedDynamicDistance(deliveryPoint, scheduleLocations.get(0));
			}
			else if (i==scheduleSize){
				return DistanceHelper.estimatedDynamicDistance(scheduleLocations.get(scheduleSize-1), pickupPoint) +
						DistanceHelper.estimatedDynamicDistance(pickupPoint, deliveryPoint);
			}
			else{
				return DistanceHelper.estimatedDynamicDistance(scheduleLocations.get(i-1), pickupPoint)
						+ DistanceHelper.estimatedDynamicDistance(pickupPoint, deliveryPoint)
						+ DistanceHelper.estimatedDynamicDistance(deliveryPoint, scheduleLocations.get(j-1))
						- DistanceHelper.estimatedDynamicDistance(scheduleLocations.get(i-1), scheduleLocations.get(j-1));
			}
		}
		else{
			double distIncrease = 0;
			if(i > 0){
				distIncrease += DistanceHelper.estimatedDynamicDistance(scheduleLocations.get(i-1), pickupPoint);
				distIncrease -= DistanceHelper.estimatedDynamicDistance(scheduleLocations.get(i-1), scheduleLocations.get(i));				
			}
			distIncrease += DistanceHelper.estimatedDynamicDistance(pickupPoint, scheduleLocations.get(i));
			
			if(j<=scheduleSize){
				distIncrease += DistanceHelper.estimatedDynamicDistance(deliveryPoint, scheduleLocations.get(j-1));
				distIncrease -= DistanceHelper.estimatedDynamicDistance(scheduleLocations.get(j-2), scheduleLocations.get(j-1));								
			}
			distIncrease += DistanceHelper.estimatedDynamicDistance(scheduleLocations.get(j-2), deliveryPoint);
			return distIncrease;
		}	
		
	}

	private void removePoint(int i) {
		taxiStatus.getSchedule().getScheduleLocations().remove(i);
		taxiStatus.getSchedule().getScheduleTimes().remove(i);
		
	}

	private void insertPoint(int i, Point point, long timeStamp) {
		taxiStatus.getSchedule().getScheduleLocations().add(i, point);
		taxiStatus.getSchedule().getScheduleTimes().add(i, timeStamp);
		
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
		long timeDelayBySrcInsertion = calcTimeDelayByInsertion(query.getPickupPoint(), i, 0);
		
		if(isSubsequentScheduleDestroyed(timeDelayBySrcInsertion, i)){
			return false;
		}
		
		insertPoint(i, query.getPickupPoint(), findEstimatedArrivalTimeAtSrc(i));
		updateSubsequentSchedule(timeDelayBySrcInsertion, i);
				
		if(!canReachQueryDeliveryPointInTime(j)){
			removePoint(i);
			return false;
		}
		
		long timeDelayByDestInsertion = calcTimeDelayByInsertion(query.getDeliveryPoint(), j, 0);
		
		if(isSubsequentScheduleDestroyed(timeDelayByDestInsertion, j)){
			removePoint(i);
			return false;
		}
		
		insertPoint(j, query.getDeliveryPoint(), findEstimatedArrivalTimeAtDest(j));
		updateSubsequentSchedule(timeDelayByDestInsertion, j);
		
		return true;
	}
	
	private void updateSubsequentSchedule(long timeDelayBySrcInsertion, int i) {
		// TODO Auto-generated method stub
		
	}

	private boolean canReachQueryPickupPointInTime(){
		long currTime = System.currentTimeMillis();
		long timeToSrcFromTaxiLoc = DistanceHelper.estimatedDynamicTimeInMilliSeconds(
						taxiStatus.getLocation(),
						query.getPickupPoint()
					);
		
		
		if((currTime + timeToSrcFromTaxiLoc) <= query.getPickupWindowLateBound()){
			return true;
		}
		else return false;
		
	}
	
	private boolean canReachQueryDeliveryPointInTime(int j) {
		long scheduledArrivalTimeDest = taxiStatus.getSchedule().getScheduleTimes().get(j-1);
		Point prevLoc = taxiStatus.getSchedule().getScheduleLocations().get(j-1);
		long timeBetween = DistanceHelper.estimatedDynamicTimeInMilliSeconds(prevLoc, query.getDeliveryPoint());
		
		if( (scheduledArrivalTimeDest + timeBetween) > query.getDeliveryWindowLateBound()){
			return false;
		}
		else return true;
	}

	private boolean isSubsequentScheduleDestroyed(long timeDelayBySrcInsertion, int i) {
		// TODO Auto-generated method stub
		
		return false;
	}
	
	/**
	 * Calculate time delay caused by inserting a new point before ith point in the schedule 
	 * @param point The point which is inserted
	 * @param i The index before which you are inserting
	 * @param waitingTime The time spent waiting for passenger if the taxi arrives early
	 * @return Time delay
	 */
	private long calcTimeDelayByInsertion(Point point, int i, long waitingTime) {
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		int scheduleSize = scheduleLocations.size();
		
		long timeDelay = 0;
		if(i > 0){
			timeDelay += DistanceHelper.estimatedDynamicTimeInMilliSeconds(scheduleLocations.get(i-1), point);
		}
		if(i < scheduleSize){
			timeDelay += DistanceHelper.estimatedDynamicTimeInMilliSeconds(point, scheduleLocations.get(i));
		}
		
		timeDelay += waitingTime;
		
		if(i > 0 && i < scheduleSize){
			timeDelay -= DistanceHelper.estimatedDynamicTimeInMilliSeconds(scheduleLocations.get(i-1), scheduleLocations.get(i));
		}
		return timeDelay;
	}

	public boolean canScheduleQuery(){
		if(queryInsertionPositions.get(0) == -1 && queryInsertionPositions.get(1) == -1){
			return false;
		}
		else return true;
	}

	

}

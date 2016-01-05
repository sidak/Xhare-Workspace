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
	private Schedule bestSchedule;
	private double bestScheduleDistIncrease;
	
	public QuerySchedulerForTaxi(Query query, TaxiStatus taxiStatus){
		this.query = query;
		this.taxiStatus = taxiStatus;
		queryInsertionPositions = scheduleQuery();
	}
	
	public Schedule getBestSchedule() {
		return bestSchedule;
	}
	public double getMinDistanceIncrease(){
		
		return bestScheduleDistIncrease;
	}
	 
	public List<Integer> scheduleQuery(){
		List<Integer> insertionPositions = new ArrayList<Integer>();
		
		int scheduleSize = taxiStatus.getSchedule().getScheduleLocations().size();
		//System.out.println("schedule size is "+ scheduleSize);
		double minDistanceIncrease = Double.MAX_VALUE;
		int querySrcInsertionIdx = -1;
		int queryDestInsertionIdx = -1;
		
		Schedule originalSchedule = new Schedule();
		originalSchedule.copyFrom(taxiStatus.getSchedule());
		
		for(int i=0; i<=scheduleSize; i++){
			for(int j = i+1; j<=scheduleSize + 1; j++){
					
				if(isInsertionFeasible(i,j)){
					System.out.println("The insertion is feasible for indices: " + i + " and " + j);
					//System.out.println("size of original schedule is "+ originalSchedule.getScheduleLocations().size());
					taxiStatus.setSchedule(originalSchedule);
					//System.out.println("insertion is feasible for i "+i+" and for j "+j);
					double distanceIncrease = calcDistIncrease(i, j);
					System.out.println("The increase in distance by this manner is " + distanceIncrease);
					if(DoubleHelper.lessThan(distanceIncrease, minDistanceIncrease)){
						minDistanceIncrease = distanceIncrease;
						querySrcInsertionIdx = i;
						queryDestInsertionIdx = j;
						bestSchedule = taxiStatus.getSchedule();
					}
				}
				else {
					System.out.println("The insertion is not feasible for indices: " +i + " and " + j);
					taxiStatus.setSchedule(originalSchedule);
				}
			}
		}
		bestScheduleDistIncrease = minDistanceIncrease;
		
		insertionPositions.add(querySrcInsertionIdx);
		insertionPositions.add(queryDestInsertionIdx);
		
		return insertionPositions;
	}
	
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
	// works based on original schedule and not changed schedule
	private double calcDistIncrease(int i, int j) {
		Point pickupPoint = query.getPickupPoint();
		Point deliveryPoint = query.getDeliveryPoint();
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		int scheduleSize = scheduleLocations.size();
		if(i==(j-1)){
			if(i==0){
				// the schedule is remaining the same
				//System.out.println(DistanceHelper.estimatedDynamicDistance(pickupPoint, deliveryPoint));
				//System.out.println(DistanceHelper.estimatedDynamicDistance(deliveryPoint, scheduleLocations.get(0)));
				//System.out.println(DistanceHelper.estimatedDynamicDistance(taxiStatus.getLocation(), pickupPoint));
				//System.out.println(DistanceHelper.estimatedDynamicDistance(taxiStatus.getLocation(), scheduleLocations.get(0)));
				return DistanceHelper.estimatedDynamicDistance(pickupPoint, deliveryPoint) + 
					    DistanceHelper.estimatedDynamicDistance(deliveryPoint, scheduleLocations.get(0)) +
					    DistanceHelper.estimatedDynamicDistance(taxiStatus.getLocation(), pickupPoint) - 
					    DistanceHelper.estimatedDynamicDistance(taxiStatus.getLocation(), scheduleLocations.get(0));
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
			else{
				distIncrease += DistanceHelper.estimatedDynamicDistance(taxiStatus.getLocation(), pickupPoint);
				distIncrease -= DistanceHelper.estimatedDynamicDistance(taxiStatus.getLocation(), scheduleLocations.get(i));				

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
		taxiStatus.getSchedule().getScheduleSlackTimes().remove(i);
	}

	private void insertPoint(int i, Point point, long arrivalTime, long lateBound) {
		taxiStatus.getSchedule().getScheduleLocations().add(i, point);
		//System.out.println("the arrival time for new point will be " + arrivalTime);
		taxiStatus.getSchedule().getScheduleTimes().add(i, arrivalTime);
		//System.out.println("the slack time for new point will be " + (lateBound-arrivalTime));
		taxiStatus.getSchedule().getScheduleSlackTimes().add(i, (lateBound - arrivalTime));
	}
	
	/**
	 * Checks if Q.o and Q.d can be inserted without disturbing subsequent points in the schedule
	 * @param i insertion position for Q.o
	 * @param j insertion position for Q.d
	 * @return True, if insertion is feasible. Otherwise false.
	 */
	public boolean isInsertionFeasible(int i, int j) {
		System.out.println("\nIn insertion feasibility test: " + i + " and " + j);
		if(!canReachQueryPickupPointInTime()){
			System.out.println("The query pickup point cannot be inserted in the current schedule at idx = "+i);
			return false;
		}
		//System.out.println("It can reach query pickup point in time");
		long timeDelayBySrcInsertion = calcTimeDelayByInsertion(query.getPickupPoint(), i, 0);
		System.out.println("Time delay by pickup point insertion (in milliseconds) is " +timeDelayBySrcInsertion);
		if(isSubsequentScheduleDestroyed(timeDelayBySrcInsertion, i)){
			System.out.println("The query pickup point cannot be inserted in the current schedule at idx = "+i);

			return false;
		}
		
		if(!isValidNewPoint(findEstimatedArrivalTimeAtSrc(i), query.getPickupWindowLateBound())){
			System.out.println("The query pickup point cannot be inserted in the current schedule at idx = "+i);

			return false;
		}
		
		insertPoint(i, query.getPickupPoint(), findEstimatedArrivalTimeAtSrc(i), query.getPickupWindowLateBound());
		System.out.println("The query pickup point can inserted in the current schedule at idx = "+i);
		updateSubsequentSchedule(timeDelayBySrcInsertion, i);
				
		if(!canReachQueryDeliveryPointInTime(j)){
			removePoint(i);
			System.out.println("The query delivery point cannot be inserted in the current schedule at idx = "+j);
			return false;
		}
		//System.out.println("It can reach query delivery point in time");
		long timeDelayByDestInsertion = calcTimeDelayByInsertion(query.getDeliveryPoint(), j, 0);
		System.out.println("Time delay by delivery point insertion (in milliseconds) is " +timeDelayByDestInsertion);

		if(isSubsequentScheduleDestroyed(timeDelayByDestInsertion, j)){
			removePoint(i);
			System.out.println("The query delivery point cannot be inserted in the current schedule at idx = "+j);
			return false;
		}
		
		if(!isValidNewPoint(findEstimatedArrivalTimeAtSrc(j), query.getDeliveryWindowLateBound())){
			removePoint(i);
			System.out.println("The query delivery point cannot be inserted in the current schedule at idx = "+j);
			return false;
		}
		insertPoint(j, query.getDeliveryPoint(), findEstimatedArrivalTimeAtDest(j), query.getDeliveryWindowLateBound());
		System.out.println("The query delivery point can inserted in the current schedule at idx = "+j);
		updateSubsequentSchedule(timeDelayByDestInsertion, j);
		
		return true;
	}
	
	public List<Integer> getQueryInsertionPositions() {
		return queryInsertionPositions;
	}

	private boolean isValidNewPoint(long arrivalTime, long lateBound) {
		long slackTime = lateBound - arrivalTime;
		if(slackTime<0) return false;
		else return true;
	}

	private void updateSubsequentSchedule(long timeDelayByPointInsertion, int idx) {
		List<Long> slackTimes = taxiStatus.getSchedule().getScheduleSlackTimes();
		List<Long> arrivalTimes = taxiStatus.getSchedule().getScheduleTimes();
		List<Point> scheduleLocations = taxiStatus.getSchedule().getScheduleLocations();
		int scheduleSize = slackTimes.size();
		
		for(int i = idx+1; i<scheduleSize; i++){
			System.out.println("Updating schedule after insertion at index =  " + i);
			long oldArrivalTime = arrivalTimes.get(i);
			//System.out.println("old Arrival Time " + oldArrivalTime);
			arrivalTimes.set(i, oldArrivalTime + timeDelayByPointInsertion);
			//System.out.println("new Arrival Time " + (oldArrivalTime +timeDelayByPointInsertion) );
			
			long oldSlackTime = slackTimes.get(i);
			//System.out.println("old slack Time " + oldSlackTime);
			
			slackTimes.set(i, oldSlackTime - timeDelayByPointInsertion);
			//System.out.println("old slack Time " + (oldSlackTime - timeDelayByPointInsertion));
		}
		
		taxiStatus.setSchedule(new Schedule(scheduleLocations, arrivalTimes, slackTimes));
		
	}
	
	private boolean isSubsequentScheduleDestroyed(long timeDelayByPointInsertion, int idx) {
		List<Long> slackTimes = taxiStatus.getSchedule().getScheduleSlackTimes();
		int scheduleSize = slackTimes.size();
		//System.out.println("idx is " + idx);
		//System.out.println("time delay by src ins is " + timeDelayByPointInsertion);
		for(int i = idx; i<scheduleSize; i++){
			//System.out.println("slackTime for i " +i+" is "+ slackTimes.get(i));
			if((slackTimes.get(i)-timeDelayByPointInsertion) < 0 ){
				return true;
			}
		}
		return false;
	}
	
	private boolean canReachQueryPickupPointInTime(){
		long currTime = DateTimeHelper.getCurrTime();
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
		
		//System.out.println("scheduled arrival time at pt before inserting dest "+ scheduledArrivalTimeDest);
		//System.out.println("timeBetween "+ timeBetween);
		//System.out.println("query delivery late bd "+query.getDeliveryWindowLateBound());
		if( (scheduledArrivalTimeDest + timeBetween) > query.getDeliveryWindowLateBound()){
			return false;
		}
		else return true;
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
		
		Point prevPoint, nextPoint, currPoint = point;
		
		if(i==0){
			prevPoint = taxiStatus.getLocation();
		}
		else prevPoint = scheduleLocations.get(i-1);
		
		if(i<scheduleSize){
			nextPoint = scheduleLocations.get(i);
		}
		else{
			nextPoint = null;
		}
		
		long timeDelay = 0;
		long prevCurrTime, currNextTime, prevNextTime;
		prevCurrTime = DistanceHelper.estimatedDynamicTimeInMilliSeconds(prevPoint, currPoint);
		currNextTime = DistanceHelper.estimatedDynamicTimeInMilliSeconds(currPoint, nextPoint);
		prevNextTime = DistanceHelper.estimatedDynamicTimeInMilliSeconds(prevPoint, nextPoint);
		
		//System.out.println("Time between prevPoint and currPoint is " +prevCurrTime);
		timeDelay += prevCurrTime;
		//System.out.println("Time between currPoint and nextPoint is " +currNextTime);
		timeDelay += currNextTime;
		//System.out.println("Time between prevPoint and currPoint is " + prevNextTime);
		timeDelay -= prevNextTime;
		timeDelay += waitingTime;

		return timeDelay;
	}

	public boolean canScheduleQuery(){
		if(queryInsertionPositions.get(0) == -1 && queryInsertionPositions.get(1) == -1){
			return false;
		}
		else return true;
	}

	

}

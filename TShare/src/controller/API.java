package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import index.SpatialIndex;
import index.TaxiIndex;
import index.TemporalIndex;
import model.Grid;
import model.Point;
import model.Query;
import model.TaxiStatus;
import preprocessor.Preprocessor;
import util.DistanceHelper;
import util.DoubleHelper;

public class API {
	private static Preprocessor preprocessor;
	private static int taxiId = 0;
	private static int queryId = 0;
	private static final int TAXI_CAPACITY = 4;
	private static final long WINDOW_LENGTH = 5*60*1000; 
	private static Map<Integer, List<SpatialIndex>> spatialGridIndex;
	private static Map<Integer, List<TemporalIndex>> temporalGridIndex;
	private static Map<Integer, List<TaxiIndex>> taxiGridIndex;
	private static List< List<Double> > gridDistMatrix;
	private static List< List<Double> > gridTimeMatrix;
	private static List<Grid> grids;
	
	private static Map<Integer, TaxiStatus> taxis;
	
	public static int supply(Point src, Point dest, long startTime){
		return supply(src, dest, startTime, TAXI_CAPACITY);
		
	}
	
	public static int supply(Point src, Point dest, long startTime, int capacity){
		TaxiStatus taxiStatus = new TaxiStatus(taxiId, src, dest, startTime, capacity);
		taxis.put(taxiId, taxiStatus);
		taxiId ++;
		
		int srcGridIdx = preprocessor.calcGridIndex(src);
		int destGridIdx = preprocessor.calcGridIndex(dest);
		
		//TODO: instead of list, use a set to order it by timestamp
	
		if(!taxiGridIndex.containsKey(srcGridIdx)){
			List<TaxiIndex> taxiIndices = new ArrayList<TaxiIndex>();
			taxiGridIndex.put(srcGridIdx, taxiIndices);
		}
		taxiGridIndex.get(srcGridIdx).add(new TaxiIndex(srcGridIdx, startTime));
		
		if(!taxiGridIndex.containsKey(destGridIdx)){
			List<TaxiIndex> taxiIndices = new ArrayList<TaxiIndex>();
			taxiGridIndex.put(destGridIdx, taxiIndices);
		}
		taxiGridIndex.get(destGridIdx)
			.add(new TaxiIndex(destGridIdx, 
					DistanceHelper.estimatedDynamicTimeInMilliSeconds(src, dest)
					)
				);
		
		Collections.sort(taxiGridIndex.get(srcGridIdx));
		Collections.sort(taxiGridIndex.get(destGridIdx));
		
		return (taxiId-1);
		
	}
	
	public static List<Integer> search(Point src, Point dest, long startTimeInMillis, long endTimeInMillis){
		List<Integer> selectedTaxiIds = new ArrayList<Integer>();
		
		Query query = new Query();
		query.setId(queryId);
		queryId ++;
		
		query.setPickupPoint(src);
		query.setDeliveryPoint(dest);
		query.setTimestamp(System.currentTimeMillis());
		
		Long[] pickupWindow = new Long[2];
		pickupWindow[0] = startTimeInMillis;
		pickupWindow[1] = startTimeInMillis + WINDOW_LENGTH;
		query.setPickupWindow(pickupWindow);
		
		Long[] deliveryWindow = new Long[2];
		deliveryWindow[0] = endTimeInMillis;
		deliveryWindow[1] = endTimeInMillis + WINDOW_LENGTH;
		query.setDeliveryWindow(deliveryWindow);
				
		DualSidedSearch dualSidedSearch = new DualSidedSearch();
		dualSidedSearch.setQuery(query);
		
		List<TaxiIndex> candidateTaxis = dualSidedSearch.searchCandidateTaxis(
				spatialGridIndex, temporalGridIndex, taxiGridIndex, preprocessor);
		
		QuerySchedulerForTaxi queryScheduler;
		double minDistInc = Double.MAX_VALUE;
		int bestTaxiId = -1;
		
		for(int i=0; i<candidateTaxis.size(); i++){
			int id = candidateTaxis.get(i).getTaxiId();
			
			TaxiStatus taxiStatus = new TaxiStatus();
			taxiStatus = taxis.get(id);
			
			queryScheduler = new QuerySchedulerForTaxi(query, taxiStatus);
			double distInc = queryScheduler.getMinDistanceIncrease();
			if(DoubleHelper.lessThan(distInc, minDistInc)){
				minDistInc = distInc;
				bestTaxiId = id;
			}
			queryScheduler = null;
		}
		
		if(bestTaxiId !=-1) selectedTaxiIds.add(bestTaxiId);
		
		return selectedTaxiIds;
	}
	
	public static void book(int taxiId, Point src, Point dest){
	
	}
	
	public static void main(String[] args) {
		
		preprocessor = new Preprocessor();
		preprocessor.doPreprocessing();
		
		grids = preprocessor.getGrids();
		gridDistMatrix = preprocessor.getGridDistMatrix();
		gridTimeMatrix = preprocessor.getGridTimeMatrix();
		
		temporalGridIndex = preprocessor.getTemporalGridIndex();
		spatialGridIndex = preprocessor.getSpatialGridIndex();
		taxiGridIndex = new HashMap<Integer, List<TaxiIndex>>();
		taxis = new HashMap<Integer, TaxiStatus>();
		
	}
}

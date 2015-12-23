package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import index.SpatialIndex;
import index.TaxiIndex;
import index.TemporalIndex;
import model.Query;
import preprocessor.Preprocessor;
import util.DateTimeHelper;
import util.ListHelper;

public class DualSidedSearch {
	private Query query;
	
	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}
	
	public List<TaxiIndex> searchCandidateTaxis(
			Map<Integer, List<SpatialIndex>> spatialGridIndex, 
			Map<Integer, List<TemporalIndex>> temporalGridIndex, 
			Map<Integer, List<TaxiIndex>> taxiGridIndex, Preprocessor preprocessor){
		
		List<TaxiIndex> candidateTaxis = new ArrayList<TaxiIndex>();
			
		int srcGridIdx = preprocessor.calcGridIndex(query.getPickupPoint());
		System.out.println("Src grid is " +srcGridIdx);
		int destGridIdx = preprocessor.calcGridIndex(query.getDeliveryPoint());
		System.out.println("Dest grid is " +destGridIdx);

		List<TemporalIndex> srcTemporalGridList = temporalGridIndex.get(srcGridIdx);
		System.out.println("Src Temporal Grid List size is "+srcTemporalGridList.size());
		List<TemporalIndex> destTemporalGridList = temporalGridIndex.get(destGridIdx);
		System.out.println("Dest Temporal Grid List size is "+destTemporalGridList.size());
		
		Map<Integer, Boolean> isPickableGridNearSrc = new HashMap<Integer, Boolean>();
		for(int i =0; i<srcTemporalGridList.size(); i++){
			if(cannotPickupFromNearbyGrid(srcTemporalGridList.get(i).getGridTime())){
				break;
			}
			else{
				System.out.println("Is pickable grid near src, idx of which is " + srcTemporalGridList.get(i).getGridIdx());
				isPickableGridNearSrc.put(srcTemporalGridList.get(i).getGridIdx(), true);
			}
		}
		System.out.println("Size of isPickableGridNearSrc is "+ isPickableGridNearSrc.size());
		Map<Integer, Boolean> isDeliverableGridNearDest = new HashMap<Integer, Boolean>();
		for(int i=0; i<destTemporalGridList.size(); i++){
			if(cannotDeliverFromNearbyGrid(destTemporalGridList.get(i).getGridTime())){
				break;
			}
			else{
				System.out.println("Is deliverable grid near src, idx of which is " + destTemporalGridList.get(i).getGridIdx());
				isDeliverableGridNearDest.put(destTemporalGridList.get(i).getGridIdx(), true);
			}
		}
		System.out.println("Size of isDeliverableGridNearDest is "+ isDeliverableGridNearDest.size());
		
		List<Integer> pickableGridsOrderedSpatially = new ArrayList<Integer>();
		List<Integer> deliverableGridsOrderedSpatially = new ArrayList<Integer>();
		
		List<SpatialIndex> srcSpatialGridList = spatialGridIndex.get(srcGridIdx);
		List<SpatialIndex> destSpatialGridList = spatialGridIndex.get(destGridIdx);
		
		for(int i=0; i<srcSpatialGridList.size(); i++){
			int gridIdx = srcSpatialGridList.get(i).getGridIdx();
			if(!isPickableGridNearSrc.containsKey(gridIdx)){
				continue;
			}
			else if(isPickableGridNearSrc.get(gridIdx).booleanValue()){
				pickableGridsOrderedSpatially.add(gridIdx);
			}
		}
		
		System.out.println("Size of pickableGridsOrderedSpatially is "+ pickableGridsOrderedSpatially.size());
		System.out.println(pickableGridsOrderedSpatially.toString());
		for(int i=0; i<destSpatialGridList.size(); i++){
			int gridIdx = destSpatialGridList.get(i).getGridIdx();
			if(!isDeliverableGridNearDest.containsKey(gridIdx)){
				continue;
			}
			else if(isDeliverableGridNearDest.get(gridIdx).booleanValue()){
				deliverableGridsOrderedSpatially.add(gridIdx);
			}
		}
		System.out.println("Size of deliverableGridsOrderedSpatially is "+ deliverableGridsOrderedSpatially.size());
		System.out.println(deliverableGridsOrderedSpatially.toString());

		List<TaxiIndex> srcTaxis = new ArrayList<TaxiIndex>(), destTaxis = new ArrayList<TaxiIndex>();
		if(taxiGridIndex.containsKey(srcGridIdx)) srcTaxis = filterNotPickableTaxis(taxiGridIndex.get(srcGridIdx), 0);
		if(taxiGridIndex.containsKey(destGridIdx)) destTaxis = filterNotDeliverableTaxis(taxiGridIndex.get(destGridIdx), 0);
		
		int pickableGridsSpatiallySize = pickableGridsOrderedSpatially.size();
		int deliverableGridsSpatiallySize = deliverableGridsOrderedSpatially.size();
		
		int pickableGridIdx = 0;
		int deliverableGridIdx = 0;
		
		List<TaxiIndex> taxiIntersectionList = ListHelper.findListIntersection(srcTaxis, destTaxis);
		
		while(candidateTaxis.size() == 0){
			
			if(taxiIntersectionList!=null && taxiIntersectionList.size()>0){
				candidateTaxis = taxiIntersectionList;
				break;
			}
			else if(pickableGridIdx == pickableGridsSpatiallySize && deliverableGridIdx == deliverableGridsSpatiallySize){
				break;
			}
			else{
				if(pickableGridIdx < pickableGridsSpatiallySize){
					System.out.println("Index of grid near src: " +pickableGridsOrderedSpatially.get(pickableGridIdx));
					if(taxiGridIndex.containsKey(pickableGridsOrderedSpatially.get(pickableGridIdx))){
						//srcTaxis.addAll(filterNotPickableTaxis(taxiGridIndex.get(pickableGridsOrderedSpatially.get(pickableGridIdx)), temporalGridIndex.get(pickableGridsOrderedSpatially.get(pickableGridIdx)).get(srcGridIdx).getGridTime()));
						srcTaxis.addAll(taxiGridIndex.get(pickableGridsOrderedSpatially.get(pickableGridIdx)));
						System.out.println("\nNumber of taxis feasible near src "+ srcTaxis.size() );
					}
					pickableGridIdx ++;
				}
				if(deliverableGridIdx < deliverableGridsSpatiallySize){
					System.out.println("Index of grid near dest: " +deliverableGridsOrderedSpatially.get(deliverableGridIdx)); 
					if(taxiGridIndex.containsKey(deliverableGridsOrderedSpatially.get(deliverableGridIdx))){
						//destTaxis.addAll(filterNotDeliverableTaxis(taxiGridIndex.get(deliverableGridsOrderedSpatially.get(deliverableGridIdx)), temporalGridIndex.get(deliverableGridsOrderedSpatially.get(deliverableGridIdx)).get(destGridIdx).getGridTime()));
						destTaxis.addAll(taxiGridIndex.get(deliverableGridsOrderedSpatially.get(deliverableGridIdx)));
						System.out.println("\nNumber of taxis feasible near dest "+ destTaxis.size() +"\n" );
					}
					deliverableGridIdx ++;
				}
				taxiIntersectionList = ListHelper.findListIntersection(srcTaxis, destTaxis);
			}	
			
		}
		
		return candidateTaxis;
		
	}
	private List<TaxiIndex> filterNotPickableTaxis(List<TaxiIndex> taxiList, double gridTime){
		if(taxiList.isEmpty()){
			return null;
		}
		List<TaxiIndex> filteredTaxiList = new ArrayList<TaxiIndex>();
		
		for(int i = 0; i<taxiList.size(); i++){
			TaxiIndex taxiIndex = taxiList.get(i);
			if(canPickupByTaxiFromNearbyGrid(taxiIndex.getTimestamp(), gridTime)){
				filteredTaxiList.add(taxiIndex);
			}
			else break;
		}
		
		return filteredTaxiList;
	}
	private List<TaxiIndex> filterNotDeliverableTaxis(List<TaxiIndex> taxiList, double gridTime){
		if(taxiList.isEmpty()){
			return null;
		}
		List<TaxiIndex> filteredTaxiList = new ArrayList<TaxiIndex>();
		
		for(int i = 0; i<taxiList.size(); i++){
			TaxiIndex taxiIndex = taxiList.get(i);
			if(canDeliverTaxiToNearbyGrid(taxiIndex.getTimestamp(), gridTime)){
				filteredTaxiList.add(taxiIndex);
			}
			else break;
		}
		
		return filteredTaxiList;
	}
	private boolean canDeliverTaxiToNearbyGrid(Long taxiTimestamp, double gridTime) {
		System.out.println("In canDeliverTaxiToNearbyGrid");
		System.out.println("Curr time is "+ DateTimeHelper.getCurrTime());
		System.out.println("Taxi Timestamp is  "+ taxiTimestamp);
		System.out.println("time to reach grid is "+ DateTimeHelper.toMilliSeconds(gridTime));
		System.out.println("Late bound of delivery window is "+query.getDeliveryWindowLateBound());
		
		
		long taxiTimeStampMilliSeconds = taxiTimestamp;
		long maxTaxiTimeStamp = query.getDeliveryWindowLateBound() - DateTimeHelper.toMilliSeconds(gridTime);
		System.out.println("diff is " + maxTaxiTimeStamp );
		return taxiTimeStampMilliSeconds <= maxTaxiTimeStamp && taxiTimestamp >= DateTimeHelper.getCurrTime();
	}

	private boolean canPickupByTaxiFromNearbyGrid(long taxiTimestamp, double gridTime) {
		System.out.println("In canPickupTaxiFromNearbyGrid");
		System.out.println("Curr time is "+ DateTimeHelper.getCurrTime());
		System.out.println("Taxi Timestamp is  "+ taxiTimestamp);
		System.out.println("time to reach grid is "+ DateTimeHelper.toMilliSeconds(gridTime));
		System.out.println("Late bound of pickup window is "+query.getPickupWindowLateBound());
		
		long taxiTimeStampMilliSeconds = taxiTimestamp;
		long maxTaxiTimeStamp = query.getPickupWindowLateBound() - DateTimeHelper.toMilliSeconds(gridTime);
		System.out.println("diff is " + maxTaxiTimeStamp );
		return taxiTimeStampMilliSeconds <= maxTaxiTimeStamp  && taxiTimestamp >= DateTimeHelper.getCurrTime();
	}

	private boolean cannotPickupFromNearbyGrid(double gridTime) {
		System.out.println("Curr time is "+ DateTimeHelper.getCurrTime());
		System.out.println("time to reach grid is "+ DateTimeHelper.toMilliSeconds(gridTime));
		System.out.println("Late bound of pickup window is "+query.getPickupWindowLateBound());
		System.out.println("");
		if((DateTimeHelper.getCurrTime() + DateTimeHelper.toMilliSeconds(gridTime)) 
				<= query.getPickupWindowLateBound()){
			return false;
		}
		return true;
	}
	
	private boolean cannotDeliverFromNearbyGrid(double gridTime){
		System.out.println("Curr time is "+ DateTimeHelper.getCurrTime());
		System.out.println("time to reach grid is "+ DateTimeHelper.toMilliSeconds(gridTime));
		System.out.println("Late bound of delivery window is "+query.getDeliveryWindowLateBound());
		System.out.println("");
		if((DateTimeHelper.getCurrTime() + DateTimeHelper.toMilliSeconds(gridTime)) 
				<= query.getDeliveryWindowLateBound()){
			return false;
		}
		return true;
	}
}

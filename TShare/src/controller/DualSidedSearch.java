package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import index.SpatialIndex;
import index.TaxiIndex;
import index.TemporalIndex;
import model.Query;
import preprocessor.GraphPreprocessor;
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
			Map<Integer, List<TaxiIndex>> taxiGridIndex){
		
		List<TaxiIndex> candidateTaxis = new ArrayList<TaxiIndex>();
		
		GraphPreprocessor preprocessor = new GraphPreprocessor();
		
		int srcGridIdx = preprocessor.calcGridIndex(query.getPickupPoint());
		int destGridIdx = preprocessor.calcGridIndex(query.getPickupPoint());
		
		List<TemporalIndex> srcTemporalGridList = temporalGridIndex.get(srcGridIdx);
		List<TemporalIndex> destTemporalGridList = temporalGridIndex.get(destGridIdx);
		
		Map<Integer, Boolean> isPickableGridNearSrc = new HashMap<Integer, Boolean>();
		for(int i =0; i<srcTemporalGridList.size(); i++){
			if(cannotPickupFromNearbyGrid(srcTemporalGridList.get(i).getGridTime())){
				break;
			}
			else{
				isPickableGridNearSrc.put(srcTemporalGridList.get(i).getGridIdx(), true);
			}
		}
		
		Map<Integer, Boolean> isDeliverableGridNearDest = new HashMap<Integer, Boolean>();
		for(int i=0; i<destTemporalGridList.size(); i++){
			if(cannotDeliverFromNearbyGrid(destTemporalGridList.get(i).getGridTime())){
				break;
			}
			else{
				isDeliverableGridNearDest.put(destTemporalGridList.get(i).getGridIdx(), true);
			}
		}
		
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
		
		for(int i=0; i<destSpatialGridList.size(); i++){
			int gridIdx = destSpatialGridList.get(i).getGridIdx();
			if(!isDeliverableGridNearDest.containsKey(gridIdx)){
				continue;
			}
			else if(isDeliverableGridNearDest.get(gridIdx).booleanValue()){
				deliverableGridsOrderedSpatially.add(gridIdx);
			}
		}
		
		List<TaxiIndex> srcTaxis = filterNotPickableTaxis(taxiGridIndex.get(srcGridIdx), 0);
		List<TaxiIndex> destTaxis = filterNotPickableTaxis(taxiGridIndex.get(destGridIdx), 0);
		
		int pickableGridsSpatiallySize = pickableGridsOrderedSpatially.size();
		int deliverableGridsSpatiallySize = deliverableGridsOrderedSpatially.size();
		
		int pickableGridIdx = 0;
		int deliverableGridIdx = 0;
		
		List<TaxiIndex> taxiIntersectionList = ListHelper.findListIntersection(srcTaxis, destTaxis);
		
		while(candidateTaxis.size() == 0){
			
			if(taxiIntersectionList.size()>0){
				candidateTaxis = taxiIntersectionList;
				break;
			}
			else if(pickableGridIdx == pickableGridsSpatiallySize && deliverableGridIdx == deliverableGridsSpatiallySize){
				break;
			}
			else{
				if(pickableGridIdx < pickableGridsSpatiallySize){
					srcTaxis.addAll(filterNotPickableTaxis(taxiGridIndex.get(pickableGridsOrderedSpatially.get(pickableGridIdx)), temporalGridIndex.get(pickableGridIdx).get(srcGridIdx).getGridTime()));
					pickableGridIdx ++;
				}
				if(deliverableGridIdx < deliverableGridsSpatiallySize){
					destTaxis.addAll(filterNotPickableTaxis(taxiGridIndex.get(deliverableGridsOrderedSpatially.get(deliverableGridIdx)), temporalGridIndex.get(deliverableGridIdx).get(destGridIdx).getGridTime()));
					deliverableGridIdx ++;
				}
				taxiIntersectionList = ListHelper.findListIntersection(srcTaxis, destTaxis);
			}	
			
		}
		
		return candidateTaxis;
		
	}
	private List<TaxiIndex> filterNotPickableTaxis(List<TaxiIndex> taxiList, double gridTime){
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
	private boolean canPickupByTaxiFromNearbyGrid(long taxiTimestamp, double gridTime) {
		long taxiTimeStampMilliSeconds = taxiTimestamp;
		long maxTaxiTimeStamp = System.currentTimeMillis() - DateTimeHelper.toMilliSeconds(gridTime);
		return taxiTimeStampMilliSeconds <= maxTaxiTimeStamp;
	}

	private boolean cannotPickupFromNearbyGrid(double gridTime) {
		if((System.currentTimeMillis() + DateTimeHelper.toMilliSeconds(gridTime)) 
				<= query.getPickupWindowLateBound()){
			return true;
		}
		return false;
	}
	
	private boolean cannotDeliverFromNearbyGrid(double gridTime){
		if((System.currentTimeMillis() + DateTimeHelper.toMilliSeconds(gridTime)) 
				<= query.getDeliveryWindowLateBound()){
			return true;
		}
		return false;
	}
}

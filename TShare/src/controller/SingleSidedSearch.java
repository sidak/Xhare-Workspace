package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import index.TaxiIndex;
import index.TemporalIndex;
import model.Query;
import util.DateTimeHelper;

public class SingleSidedSearch {
	private Query query;
	
	public SingleSidedSearch(Query query){
		this.query = query;
	}
	
	public Query getQuery() {
		return query;
	}
	
	public void setQuery(Query query) {
		this.query = query;
	}
	
	public List<TaxiIndex> searchCandidateTaxis(
			Map<Integer, List<TemporalIndex>> temporalGridIndex, 
			Map<Integer, List<TaxiIndex>> taxiGridIndex){
		
		List<TaxiIndex> candidateTaxis = new ArrayList<TaxiIndex>();

		Preprocessor preprocessor = new Preprocessor();
		int srcGridIdx = preprocessor.calcGridIndex(query.getPickupPoint());
		
		List<TemporalIndex> temporalGridList = temporalGridIndex.get(srcGridIdx);
		for(int i=0; i<temporalGridList.size(); i++){
			
			TemporalIndex temporalIndex = temporalGridList.get(i);
			if(cannotPickupFromNearbyGrid(temporalIndex.getGridTime())){
				break;
			}
			List<TaxiIndex> taxiList = taxiGridIndex.get(temporalIndex.getGridIdx());
			for(int j = 0; j<taxiList.size(); j++){
				TaxiIndex taxiIndex = taxiList.get(j);
				if(canPickupByTaxiFromNearbyGrid(taxiIndex.getTimestamp(), temporalIndex.getGridTime())){
					candidateTaxis.add(taxiIndex);
				}
				else break;
			}
		}
		
		return candidateTaxis;
	}

	private boolean canPickupByTaxiFromNearbyGrid(Date taxiTimestamp, double gridTime) {
		long taxiTimeStampMilliSeconds = DateTimeHelper.toMilliSeconds(taxiTimestamp);
		long maxTaxiTimeStamp = System.currentTimeMillis() - DateTimeHelper.toMilliSeconds(gridTime);
		return taxiTimeStampMilliSeconds <= maxTaxiTimeStamp;
	}

	private boolean cannotPickupFromNearbyGrid(double gridTime) {
		if((System.currentTimeMillis() + DateTimeHelper.toMilliSeconds(gridTime)) 
				<= DateTimeHelper.toMilliSeconds(query.getPickupWindowLateBound())){
			return true;
		}
		return false;
	}
		
}

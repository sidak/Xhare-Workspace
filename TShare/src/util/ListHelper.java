package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import index.TaxiIndex;

public class ListHelper {

	public static List<TaxiIndex> findListIntersection(List<TaxiIndex> srcTaxis, List<TaxiIndex> destTaxis) {
		List<TaxiIndex> intersectionList = new ArrayList<TaxiIndex>();
		int srcIdx = 0;
		int destIdx = 0;
		
		Comparator<TaxiIndex> idBasedComparator = new Comparator<TaxiIndex>() {

			@Override
			public int compare(TaxiIndex tIdx1, TaxiIndex tIdx2) {
				if(tIdx1.getTaxiId() < tIdx2.getTaxiId()){
					return -1;
				}
				else if (tIdx1.getTaxiId() > tIdx2.getTaxiId()){
					return 1;
				}
				else return 0;
			}
			
		}; 
		
		Collections.sort(srcTaxis, idBasedComparator);
		Collections.sort(destTaxis, idBasedComparator);
		
		while(srcIdx < srcTaxis.size() && destIdx < destTaxis.size()){
			int srcTaxiId = srcTaxis.get(srcIdx).getTaxiId();
			int destTaxiId = destTaxis.get(destIdx).getTaxiId();
					
			if(srcTaxiId == destTaxiId){
				intersectionList.add(srcTaxis.get(srcIdx));
				srcIdx ++;
				destIdx ++;
			}
			else if(srcTaxiId < destTaxiId){
				srcIdx ++;
			}
			else{
				destIdx ++;
			}
		}
		
		return intersectionList;
	}

}

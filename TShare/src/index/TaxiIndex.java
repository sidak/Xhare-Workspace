package index;

public class TaxiIndex implements Comparable<TaxiIndex>{
	private int taxiId;
	private Long timestamp;
	
	public TaxiIndex(int taxiId, long timestamp){
		this.taxiId = taxiId;
		this.timestamp = timestamp;
	}
	public int getTaxiId() {
		return taxiId;
	}
	public void setTaxiId(int taxiId) {
		this.taxiId = taxiId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public int compareTo(TaxiIndex o) {
		if(timestamp < o.getTimestamp()) return -1;
		else if(timestamp > o.getTimestamp()) return 1;
		else return 0;
	}
}

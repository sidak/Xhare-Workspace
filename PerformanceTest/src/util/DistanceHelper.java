package util;


public class DistanceHelper {
	
	public static final int XAXIS_TOWARDS_EAST = 1;
	public static final int YAXIS_TOWARDS_NORTH = -1;
	
	private static final double EARTH_RADIUS_IN_MILES=3958.75;
	private static final double MILE_TO_KM=1.609344;
	private static final double EARTH_RADIUS_IN_KM = EARTH_RADIUS_IN_MILES * MILE_TO_KM;
	
	private static double speedLimitInKmPerHr = 60;
	
	/**
	 * Calculates distance between two points using haversine formula
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return distance in kilometers
	 */
	 
	public static double distBetween(double lat1, double lng1, double lat2,
			double lng2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distInKm = Math.abs((EARTH_RADIUS_IN_KM) * c);
		return distInKm;
	}

	public static double getSpeedLimitInKmPerHr() {
		return speedLimitInKmPerHr;
	}

	public static void setSpeedLimitInKmPerHr(double speedLimitInKmPerHr) {
		DistanceHelper.speedLimitInKmPerHr = speedLimitInKmPerHr;
	}
	
}

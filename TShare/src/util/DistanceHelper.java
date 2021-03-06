package util;

import model.Point;

public class DistanceHelper {
	
	public static final int XAXIS_TOWARDS_EAST = 1;
	public static final int YAXIS_TOWARDS_NORTH = -1;
	
	private static final double EARTH_RADIUS_IN_MILES=3958.75;
	private static final double MILE_TO_KM=1.609344;
	private static final double EARTH_RADIUS_IN_KM = EARTH_RADIUS_IN_MILES * MILE_TO_KM;
	private static final double SPEED_LIMIT_KM_PER_HR = 60;

	/*
	 * @return return distance between two geo points in kilometers
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
		double dist = Math.abs((EARTH_RADIUS_IN_MILES*MILE_TO_KM) * c);
		return dist;
	}
	
	public static double distBetween(Point src, Point dest) {
		if(src == null || dest == null) return 0;
		double lat1, lng1, lat2, lng2;
		lat1 = src.getLat();
		lng1 = src.getLng();
		lat2 = dest.getLat();
		lng2 = dest.getLng();
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = Math.abs((EARTH_RADIUS_IN_MILES*MILE_TO_KM) * c);
		return dist;
	}
	
	public static double timeBetween(Point src, Point dest, double speedLimitInKmPerHr){
		return distBetween(src, dest)/speedLimitInKmPerHr ;
	}
	
	public static double timeBetween(Point src, Point dest){
		return distBetween(src, dest)/SPEED_LIMIT_KM_PER_HR;
	}
	
	public static Point findPointAtDistance(Point src, double dist, int axis){
		double destLat=0.0, destLng=0.0;
		double srcLat, srcLng;
		srcLat = src.getLat();
		srcLng = src.getLng();
		
		if(axis == XAXIS_TOWARDS_EAST){
			destLat = srcLat;
			destLng = findLngTowardEast(dist, srcLat, srcLng);
		}
		else{
			destLng = srcLng;
			destLat = findLatTowardsNorth(dist, srcLat);
		}
		return new Point(destLat, destLng);
	}

	public static double findLatTowardsNorth(double dist, double srcLat) {
		return Math.toDegrees(
					Math.asin(
						Math.sin(Math.toRadians(srcLat))* 
						Math.cos(dist/(double)EARTH_RADIUS_IN_KM)
							+
						Math.cos(Math.toRadians(srcLat))* 
						Math.sin(dist/(double)EARTH_RADIUS_IN_KM)* 
						Math.cos(Math.toRadians(0.0))
					)
				);
	}

	public static double findLngTowardEast(double dist, double srcLat, double srcLng) {
		return Math.toDegrees(
					Math.toRadians(srcLng) + 
					Math.atan2(
							Math.sin(Math.toRadians(90.0))*
							Math.sin(dist/(double)EARTH_RADIUS_IN_KM)*
							Math.cos(Math.toRadians(srcLat)),
							
							Math.cos(dist/(double)EARTH_RADIUS_IN_KM)
					)
				);
	}
	
	// This methods can be implemented using tripgo api
	public static double estimatedDynamicDistance(Point src, Point dest){
		return distBetween(src, dest);
	}
	
	public static double estimatedDynamicTimeInHours(Point src, Point dest){
		return estimatedDynamicDistance(src, dest)/SPEED_LIMIT_KM_PER_HR;
	}
	
	public static long estimatedDynamicTimeInMilliSeconds(Point src, Point dest){
		return DateTimeHelper.toMilliSeconds(estimatedDynamicTimeInHours(src, dest));
	}
	
}

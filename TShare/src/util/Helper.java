package util;

import model.Point;

public class Helper {
	
	private static final int XAXIS_TOWARDS_EAST = 1;
	private static final double EARTHRADIUSINMILES=3958.75;
	private static final double MILETOKM=1.609344;
	private static final double EARTHRADIUSINKM = EARTHRADIUSINMILES * MILETOKM;

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
		double dist = Math.abs((EARTHRADIUSINMILES*MILETOKM) * c);
		return dist;
	}
	
	public static double distBetween(Point src, Point dest) {
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
		double dist = Math.abs((EARTHRADIUSINMILES*MILETOKM) * c);
		return dist;
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
						Math.cos(dist/(double)EARTHRADIUSINKM)
							+
						Math.cos(Math.toRadians(srcLat))* 
						Math.sin(dist/(double)EARTHRADIUSINKM)* 
						Math.cos(Math.toRadians(0.0))
					)
				);
	}

	public static double findLngTowardEast(double dist, double srcLat, double srcLng) {
		return Math.toDegrees(
					Math.toRadians(srcLng) + 
					Math.atan2(
							Math.sin(Math.toRadians(90.0))*
							Math.sin(dist/(double)EARTHRADIUSINKM)*
							Math.cos(Math.toRadians(srcLat)),
							
							Math.cos(dist/(double)EARTHRADIUSINKM)
					)
				);
	}
}

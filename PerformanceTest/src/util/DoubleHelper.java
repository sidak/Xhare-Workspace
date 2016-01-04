package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleHelper {
	
	private final static double EPSILON = 0.000001;
	
	public static boolean equals(double a, double b){
	    return a == b ? true : Math.abs(a - b) < EPSILON;
	}
	
	public static boolean equals(double a, double b, double epsilon){
	    return a == b ? true : Math.abs(a - b) < epsilon;
	}
	
	public static boolean greaterThan(double a, double b){
	    return greaterThan(a, b, EPSILON);
	}
	
	public static boolean greaterThan(double a, double b, double epsilon){
	    return a - b > epsilon;
	}
	
	public static boolean lessThan(double a, double b){
	    return lessThan(a, b, EPSILON);
	}
	
	public static boolean lessThan(double a, double b, double epsilon){
	    return b - a > epsilon;
	}
	
	public static double roundToNPlaces(double value, int places){
		if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}

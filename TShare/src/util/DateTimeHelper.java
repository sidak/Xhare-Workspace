package util;

import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {
	
	private static long currTime;
	
	public static long toMilliSeconds(double gridTimeInHours) {
		gridTimeInHours *= 3600;
		gridTimeInHours *= 1000;
		long milliSeconds = (long)gridTimeInHours;
		return milliSeconds;
	}

	public static long toMilliSeconds(Date pickupWindowLateBound) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(pickupWindowLateBound);
		long lateBoundtime = cal.getTimeInMillis();
		long currTime = System.currentTimeMillis();
		long timeDiff = currTime - lateBoundtime;
		return timeDiff;
	}
	
	public static long toSeconds(double gridTimeInHours) {
		return (toMilliSeconds(gridTimeInHours)/1000);
	}

	public static long toSeconds(Date pickupWindowLateBound) {
		return (toMilliSeconds(pickupWindowLateBound)/1000);
	}

	public static long getCurrTime() {
		return currTime;
	}

	public static void setCurrTime(long currTime) {
		DateTimeHelper.currTime = currTime;
	}


}

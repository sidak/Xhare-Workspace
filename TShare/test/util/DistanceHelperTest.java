package util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import model.Point;

public class DistanceHelperTest {

	@Test
	public void testDistBetweenPointPoint() {
		assertEquals("dist calc should be correct", 27.80, 
				DistanceHelper.distBetween(new Point(0.75, 0), new Point(1,0)), 0.01);
		
	}

	@Test
	public void testFindPointAtDistance() {
		Point src = new Point(0.75, 0.0);
		
		assertEquals("latitude of dest shd be 1.0", 1.0, 
				DistanceHelper.findPointAtDistance(src, 27.8, DistanceHelper.YAXIS_TOWARDS_NORTH).getLat(), 0.0001);
		
		assertEquals("longitude of dest shd be 0.0", 0.0, 
				DistanceHelper.findPointAtDistance(src, 27.8, DistanceHelper.YAXIS_TOWARDS_NORTH).getLng(), 0.0001);
		
		
	}

}

package util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import model.Point;

public class HelperTest {

	@Test
	public void testDistBetweenPointPoint() {
		assertEquals("dist calc should be correct", 27.80, 
				Helper.distBetween(new Point(0.75, 0), new Point(1,0)), 0.01);
		
	}

	@Test
	public void testFindPointAtDistance() {
		Point src = new Point(0.75, 0.0);
		
		assertEquals("latitude of dest shd be 1.0", 1.0, 
				Helper.findPointAtDistance(src, 27.8, Helper.YAXIS_TOWARDS_NORTH).getLat(), 0.0001);
		
		assertEquals("longitude of dest shd be 0.0", 0.0, 
				Helper.findPointAtDistance(src, 27.8, Helper.YAXIS_TOWARDS_NORTH).getLng(), 0.0001);
		
		
	}

}

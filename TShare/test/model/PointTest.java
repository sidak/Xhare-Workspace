package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {

	@Test
	public void testIsGeoEqual() {
		Point tester = new Point(0.5, 0.5);
		assertTrue("0.5000, 0.5000 is same as 0.5, 0.5", tester.isGeoEqual(new Point(0.500, 0.500)));
		assertFalse("0.50001, 0.50002 is same as 0.5, 0.5", tester.isGeoEqual(new Point(0.5001, 0.5002)));
		assertTrue("0.4999999, 0.5000 is same as 0.5, 0.5", tester.isGeoEqual(new Point(0.4999999, 0.500)));
		assertTrue("0.5000001, 0.5000 is same as 0.5, 0.5", tester.isGeoEqual(new Point(0.5000001, 0.500)));
		
	}

}

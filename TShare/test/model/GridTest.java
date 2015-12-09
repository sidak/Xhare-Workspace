package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class GridTest {

	@Test
	public void testIsInGrid() {
		Grid tester = new Grid(0, 0.0, 0.0, 10.0, 10.0);
		assertTrue("5.0, 5.0 must be in the grid", tester.isInGrid(new Point(5.0, 5.0)));
		assertFalse("15.0, 15.0 must not be in the grid", tester.isInGrid(new Point(15.0, 15.0)));
		assertTrue("10, 10 must  be in the grid", tester.isInGrid(new Point(10, 10)));
		assertTrue("0, 10 must  be in the grid", tester.isInGrid(new Point(0, 10)));
		assertTrue("10, 0 must  be in the grid", tester.isInGrid(new Point(10, 0)));
		assertTrue("0, 0 must  be in the grid", tester.isInGrid(new Point(0, 0)));
	}

	@Test
	public void testGetGeoCenter() {
		Grid tester = new Grid(0, 0.0, 0.0, 10.0, 10.0);
		assertEquals("5.0 shld be within 0.01 of lat center of grid", 5.0, tester.getGeoCenter().getLat(), 0.000000000000000000000001);
		
	}

}

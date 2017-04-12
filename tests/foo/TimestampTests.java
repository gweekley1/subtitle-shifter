package foo;

import static org.junit.Assert.*;

import org.junit.Test;


public class TimestampTests {

	@Test
	public void testPositiveCarry() {
		Timestamp t = new Timestamp("00:00:00,000");
		t.shift(1000);
		Timestamp expected = new Timestamp("00:00:01,000");
		
		if (!t.equals(expected))
			fail("1: " + t + " is not equal to " + expected);
		
		
		t = new Timestamp("00:59:58,999");
		t.shift(1001);
		expected = new Timestamp("01:00:00,000");
		
		if (!t.equals(expected))
			fail("2: " + t + " is not equal to " + expected);
	}

	@Test
	public void testNegativeCarry() {
		Timestamp t = new Timestamp("01:00:00,000");
		t.shift(-1);
		Timestamp expected = new Timestamp("00:59:59,999");
		
		if (!t.equals(expected))
			fail("1: " + t + " is not equal to " + expected);
		
		
		t = new Timestamp("01:00:01,001");
		t.shift(-60 * 1000);
		expected = new Timestamp("00:59:01,001");
		
		if (!t.equals(expected))
			fail("2: " + t + " is not equal to " + expected);
	}
	
	@Test
	public void testFloor() {
		
		Timestamp t = new Timestamp("00:00:00,001");
		t.shift(-2);
		Timestamp expected = new Timestamp("00:00:00,000");
		
		if (!t.equals(expected))
			fail("1: " + t + " is not equal to " + expected);
	}
}

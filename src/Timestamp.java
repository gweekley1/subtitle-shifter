
/*
 *  This is a timestamp class for holding SRT timestamps
 *  They are of the format HH:mm:ss,SSS
 */

public class Timestamp {
	
	// This wrapper class allows an int to be passed by reference despite being a primitive
	private class IntWrapper {
		public int val;
		public IntWrapper(String in) {
			val = Integer.valueOf(in);
		}
	}
	
	private IntWrapper hours;
	private IntWrapper minutes;
	private IntWrapper seconds;
	private IntWrapper milliseconds;
	
	// this ctor assumes the input is of the format HH:mm:ss,SSS
	public Timestamp(String timestamp) {
		hours = new IntWrapper(timestamp.substring(0,2));
		minutes = new IntWrapper(timestamp.substring(3,5));
		seconds = new IntWrapper(timestamp.substring(6,8));
		milliseconds = new IntWrapper(timestamp.substring(9));
	}
	
	public String toString() {
		return String.format("%1$02d:%2$02d:%3$02d,%4$03d", 
				hours.val, minutes.val, seconds.val, milliseconds.val);
	}
	
	// adjusts the timestamp by the number of milliseconds, carrying over from milliseconds to seconds, et al
	// will not reduce a timestamp below 00:00:00,000
	public Timestamp shift(int ms) {
		
		addAndCarry(seconds, milliseconds, 1000, ms % 1000);
		addAndCarry(minutes, seconds, 60, (ms / 1000) % 60);
		addAndCarry(hours, minutes, 60, (ms / 60000) % 60);
	
		if (hours.val < 0) {
			milliseconds.val = 0;
			seconds.val = 0;
			minutes.val = 0;
			hours.val = 0;
		}
		
		return this;
	}
	
	/* helper method adds a number to a lower order field and carries over to a higher field
	 * 
	 * @param high The higher order field, in units equal to low*ratio
	 * @param low The lower order field, in arbitrary units
	 * @param ratio The ratio between high's units and low's units
	 * @param summand The number to be added to low
	 */
	private void addAndCarry(IntWrapper high, IntWrapper low, int ratio, int summand) {
		low.val += summand;
		
		// calculate and apply the carryover
		if (Math.abs(low.val) >= ratio) {
			high.val += low.val / ratio;
			low.val %= ratio;
		}

		// correct low's value when there is negative carryover
		// eg `01s,100ms - 200ms = 1000ms - 100ms = 900ms`, not `-100ms`
		if (low.val < 0) {
			low.val = ratio + low.val;
			--high.val;
		}		
	}
	
}

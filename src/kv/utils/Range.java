package kv.utils;

/**
 *  不允许交错的段出现
 * */
public class Range implements Comparable<Range> {
	
	private int start;
	private int end;
	
	public Range(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public int getStart() { return start; }
	
	public void setStart(int start) { this.start = start; }
	
	public int getEnd() { return end; }
	
	public void setEnd(int end) { this.end = end; }

	@Override
	public int compareTo(Range range) {
		if (start > range.getEnd()) {
			return -1;
		} else if (end < range.getStart()) {
			return 1;
		} else if (start <= range.getStart() 
				&& end >= range.getEnd()) {
			return 0;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
}

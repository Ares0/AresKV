package kv.utils;


// type
public enum DataType {

	BYTE_TYPE(1), BOOLEAN_TYPE(2), SHORT_TYPE(3), INT_TYPE(4),
	LONG_TYPE(5), FLOAT_TYPE(6), DOUBLE_TYPE(7), CHAR_TYPE(8), STRING_TYPE(9),
	SET_TYPE(15), LIST_TYPE(16), MAP_TYPE(17), SORTED_SET_TYPE(18);

	private int val;
	
	DataType(int val) {
		this.val = val;
	}
	
	public int getVal() {
		return val;
	}
	
}

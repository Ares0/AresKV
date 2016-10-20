package kv.utils;

import java.io.Serializable;

public class KVObject implements Serializable {

	private static final long serialVersionUID = -8776525355295644753L;

	private int type;
	
	private Object value;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
}

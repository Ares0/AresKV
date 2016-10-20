package kv.utils;

import com.alibaba.fastjson.annotation.JSONField;

public class KVObject {

	private DataType t; // type
	
	private Object v; // value
	
	public KVObject() {}

	@JSONField(serialize = false)
	public DataType getT() {
		return t;
	}

	@JSONField(deserialize = false)
	public void setT(DataType type) {
		this.t = type;
	}
	
	@JSONField(name = "t")
	public int getTCode() {
		return t.getVal();
	}
	
	@JSONField(name = "t")
	public void setTCode(int type) {
		this.t = DataType.getType(type);
	}

	public Object getV() {
		return v;
	}

	public void setV(Object value) {
		this.v = value;
	}
	
}

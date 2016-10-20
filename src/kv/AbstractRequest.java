package kv;

public abstract class AbstractRequest {
	
	protected int keytype;
	
	protected int valuetype;

	public int getKeyType() {
		return keytype;
	}

	public void setKeyType(int keytype) {
		this.keytype = keytype;
	}

	public int getValueType() {
		return valuetype;
	}

	public void setValueType(int valuetype) {
		this.valuetype = valuetype;
	}
	
}

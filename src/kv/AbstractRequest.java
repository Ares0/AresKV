package kv;

public abstract class AbstractRequest {
	
	protected int keytype;
	
	protected int valuetype;

	public int getKeytype() {
		return keytype;
	}

	public void setKeytype(int keytype) {
		this.keytype = keytype;
	}

	public int getValuetype() {
		return valuetype;
	}

	public void setValuetype(int valuetype) {
		this.valuetype = valuetype;
	}
	
}

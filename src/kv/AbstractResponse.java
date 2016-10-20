package kv;

public abstract class AbstractResponse {
	
	protected int keytype;
	
	protected int valuetype;
	
	protected boolean isMove;

	public boolean isMove() {
		return isMove;
	}

	public void setMove(boolean isMove) {
		this.isMove = isMove;
	}
	
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

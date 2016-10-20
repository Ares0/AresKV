package kv.bean;

import kv.utils.KVObject;

// remote
public class RemoteResponse {

	protected String k;
	
	protected KVObject v;
	
	protected long ci;
	
	protected boolean d;
	
	protected boolean m;

	public boolean isM() {
		return m;
	}

	public void setM(boolean move) {
		this.m = move;
	}

	public long getCi() {
		return ci;
	}

	public void setCi(long clientId) {
		this.ci = clientId;
	}

	public boolean isD() {
		return d;
	}

	public void setD(boolean watch) {
		this.d = watch;
	}
	
	public String getK() {
		return k;
	}

	public void setK(String key) {
		this.k = key;
	}

	public KVObject getV() {
		return v;
	}

	public void setV(KVObject value) {
		this.v = value;
	}
	
}

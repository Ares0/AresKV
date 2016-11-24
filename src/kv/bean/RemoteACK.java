package kv.bean;

// ack
public class RemoteACK {

	private boolean i; // isRep
	
	private String h; // host

	public RemoteACK(String host) {
		this(false, host);
	}
	
	public RemoteACK(boolean isRep, String host) {
		this.i = isRep;
		this.h = host;
	}

	public boolean isI() {
		return i;
	}

	public void setI(boolean isRep) {
		this.i = isRep;
	}

	public String getH() {
		return h;
	}

	public void setH(String host) {
		this.h = host;
	}

}

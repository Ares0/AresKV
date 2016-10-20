package kv.bean;

import java.io.Serializable;

public class RemoteACK implements Serializable {

	private static final long serialVersionUID = -6579869166567310122L;
	
	private boolean isRep;
	
	private String host;

	public RemoteACK(String host) {
		this(false, host);
	}
	
	public RemoteACK(boolean isRep, String host) {
		this.isRep = isRep;
		this.host = host;
	}

	public boolean isRep() {
		return isRep;
	}

	public void setRep(boolean isRep) {
		this.isRep = isRep;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}

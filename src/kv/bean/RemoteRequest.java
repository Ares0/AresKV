package kv.bean;

import java.io.Serializable;

import kv.utils.KVObject;

// remote
public class RemoteRequest extends AbstractRequest implements Serializable{

	private static final long serialVersionUID = 3869708429918179386L;
	
	public RemoteRequest(int command, String key, KVObject value, long clientId) {
		volidateCommond(command);
		
		this.command = command;
		this.key = key;
		this.value = value;
		this.clientId = clientId;
	}
	
}

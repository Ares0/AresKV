package kv.bean;

import java.io.Serializable;

import kv.Command;
import kv.utils.KVObject;

public abstract class AbstractRequest implements Serializable {
	
	private static final long serialVersionUID = -7130345855161529489L;

	protected String key;
	
	protected KVObject value;
	
	protected Command command;
	
	protected boolean watch;
	
	protected long expireTime;
	
	protected long clientId;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public KVObject getValue() {
		return value;
	}

	public void setValue(KVObject value) {
		this.value = value;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public boolean isWatch() {
		return watch;
	}

	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}
	
	protected void volidateCommond(Command command) {
		if (command != Command.PUT && command != Command.GET 
				&& command != Command.REMOVE && command != Command.RESET && command != Command.CLOSE
				&& command != Command.EXPIRE && command != Command.DIRTY
				&& command != Command.ADD_CLUSTER_NODE && command != Command.CHANGE_CLUSTER_RANGE) {
			throw new IllegalArgumentException();
		}
	}
	
}

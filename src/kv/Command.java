package kv;


// command
public enum Command {

	PUT(1), GET(2), REMOVE(3), RESET(4), CLOSE(5),
	EXPIRE(11), DIRTY(12), ADD_CLUSTER_NODE(21), CHANGE_CLUSTER_RANGE(22);
	
	private int val;
	
	Command(){}
	
	Command(int val) {
		this.val = val;
	}
	
	public int getVal() {
		return val;
	}

	public static Command getCommand(int cc) {
		for (Command c : Command.values()) {
			if (c.getVal() == cc) {
				return c;
			}
		}
		return null;
	}

}

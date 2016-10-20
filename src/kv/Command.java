package kv;

public interface Command {

	public static int PUT = 1;
	
	public static int GET = 2;
	
	public static int REMOVE = 3;
	
	public static int RESET = 4;
	
	public static int CLOSE = 5;
	
	public static int EXPIRE = 7;
	
	public static int DIRTY = 8;
	
	public static int ADD_CLUSTER_NODE = 10;
	
	public static int CHANGE_CLUSTER_RANGE = 11;
	
}

package kv;

public interface Command {

	public static int PUT = 1;
	
	public static int GET = 2;
	
	public static int REMOVE = 3;
	
	public static int RESET = 4;
	
	public static int CLOSE = 5;
	
}

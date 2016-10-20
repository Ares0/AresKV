package kv.synchro;

public class SynchronousFactory {

	private static final SpinSynchronous spin = new SpinSynchronous();
	
	private static final SleepSynchronous sleep = new SleepSynchronous();
	
	public static SpinSynchronous getSpinSynchronous() {
		return spin;
	}
	
	public static SleepSynchronous getSleepSynchronous() {
		return sleep;
	}
	
}

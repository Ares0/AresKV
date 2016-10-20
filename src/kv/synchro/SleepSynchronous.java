package kv.synchro;

public class SleepSynchronous implements Synchronous {

	private long sleepTime;
	
	private static long DEFAULT_SLEEP_TIME = 1;
	
	public SleepSynchronous() {
		this(DEFAULT_SLEEP_TIME);
	}
	
	public SleepSynchronous(long sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	@Override
	public void doSynchronous() {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

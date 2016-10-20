package kv.queue;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kv.db.Request;
import kv.synchro.SleepSynchronous;
import kv.synchro.Synchronous;

/**
 *  queue
 * 环状数组实现。
 * */
public class RequestRingQueue<K, V> implements RequestQueue<K, V> {
	
	private int capacity;
	
	private volatile int writeIndex;
	
	private volatile int readIndex;
	
	private Request<K, V>[] ringBuffer;
	
	private Lock lock;
	
	private Synchronous writeSyn;
	
	private Synchronous readSyn;
	
	private static int DEFAULT_CAPACITY = 64;
	
	public RequestRingQueue() {
		this(DEFAULT_CAPACITY);
	}
	
	public RequestRingQueue(int capacity) {
		this(capacity, new SleepSynchronous(), new SleepSynchronous());
	}
	
	@SuppressWarnings("unchecked")
	public RequestRingQueue(int capacity, Synchronous writeSyn, Synchronous readSyn) {
		this.capacity = capacity;
		
		this.writeSyn = writeSyn;
		this.readSyn = readSyn;
		
		lock = new ReentrantLock();
		ringBuffer = new Request[capacity];
	}
	
	// produce
	public void produce(Request<K, V> com) {
		lock.lock();
		
		int ringReadIndex;
		int ringWriteIndex = 0;
		
		if (!(readIndex ==0 && writeIndex == 0)) {
			ringReadIndex = getRingIndex(readIndex);
			ringWriteIndex = getRingIndex(writeIndex);
			
			int ringWriteTurn = getRingTurn(writeIndex);
			int ringReadTurn = getRingTurn(readIndex);
			
			while (ringWriteTurn > ringReadTurn && ringWriteIndex >= ringReadIndex) {
				writeSyn.doSynchronous();
			} 
			
			while (ringWriteIndex >= this.capacity) {
				writeSyn.doSynchronous();
			}
		}
		
		writeIndex++;
		ringBuffer[ringWriteIndex] = com;
		
		lock.unlock();
	}

	// consume
	public Request<K, V> consume() {
		
		while (readIndex ==0 && writeIndex == 0) {
			readSyn.doSynchronous();
		}
		
		int ringWriteIndex = getRingIndex(writeIndex);
		int ringReadIndex = getRingIndex(readIndex);
		
		int ringWriteTurn = getRingTurn(writeIndex);
		int ringReadTurn = getRingTurn(readIndex);
		
		while ((ringReadTurn == ringWriteTurn && readIndex >= writeIndex) 
				|| (ringReadTurn > ringWriteTurn && ringReadIndex >= ringWriteIndex)) {
		    readSyn.doSynchronous();
		}
		
		while (ringReadIndex >= this.capacity) {
			writeSyn.doSynchronous();
		}
		
		readIndex++;
		return ringBuffer[ringReadIndex];
	}

	private int getRingIndex(int i) {
		return i == 0 ? i : i % capacity;
	}
	
	private int getRingTurn(int t) {
		return t == 0 ? t : t / capacity;
	}
	
}

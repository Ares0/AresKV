package test.current;

import java.net.UnknownHostException;

import kv.cluster.Follower;

public class KVConfig2 {

	public static void main(String[] args) throws UnknownHostException {
		Follower f = new Follower(null);
		f.start();
	}
	
}

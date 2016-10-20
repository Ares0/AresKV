package test.current;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {

	public static void main(String[] args) throws UnknownHostException {
		System.out.println("1".hashCode());
		System.out.println("fafhkdas".hashCode() % 16384);
		
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}

}

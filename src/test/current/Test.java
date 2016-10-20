package test.current;

public class Test {

	public static void main(String[] args) {
		System.out.println("1".hashCode());
		System.out.println("fafhk".hashCode() % 16383);
	}

}

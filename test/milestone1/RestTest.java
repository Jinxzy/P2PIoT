package milestone1;


import org.apache.commons.codec.digest.DigestUtils;


public class RestTest {
	public static void main(String[] args) {
		Node n1 = new Node("localhost", 50001); //12d
		Node n2 = new Node("localhost", 50002); //5ff
		Node n3 = new Node("localhost", 50003); //073
		Node n4 = new Node("localhost", 50004);
		Node n5 = new Node("localhost", 50005);
		
		n1.join();
		n2.join("localhost", 50001);
		n3.join("localhost", 50002);
//		n4.join("localhost", 50001);
//		n5.join("localhost", 50004);
		
		System.out.println(hashIPPortToID("localhost", 50001));
		System.out.println(hashIPPortToID("localhost", 50002));
		System.out.println(hashIPPortToID("localhost", 50003));
		System.out.println(hashIPPortToID("localhost", 50004));
		System.out.println(hashIPPortToID("localhost", 50005));
		
	}
	
	private static String hashIPPortToID(String ip, int port) {
		String res = DigestUtils.sha1Hex(ip + ":" + port); //Sha1 hash using Apache commons library
		return res;
	}

}

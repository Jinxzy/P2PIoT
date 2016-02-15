package milestone1;

public class RestTest {
	public static void main(String[] args) {
		Node n1 = new Node("localhost", 50001); //12d
		Node n2 = new Node("localhost", 50002); //5ff
		Node n3 = new Node("localhost", 50003); //073
		Node n4 = new Node("localhost", 50004); //7bd
		Node n5 = new Node("localhost", 50005); //e66
		
		n1.join();					 //1
		n2.join("localhost", 50001); //5
		n3.join("localhost", 50002); //0
		n4.join("localhost", 50001); //7
		n5.join("localhost", 50004); //e
		
		n2.leave();
	}
}

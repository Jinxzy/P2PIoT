package milestone1;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NodeUnitTest {
	Node n1;
	Node n2;
	Node n3;
	Node n4;
	Node n5;
	
	@Before
	public void setup() {
		n1 = new Node("localhost", 50001); //12d
		n2 = new Node("localhost", 50002); //5ff
		n3 = new Node("localhost", 50003); //073
		n4 = new Node("localhost", 50004); //7bd
		n5 = new Node("localhost", 50005); //e66
	}
	
	@After
	public void teardown() {
		
	}
	
	@Test
	public void nodeStartsOwnNetwork() {
		n1.join();
		
		//Predecessor and successor is itself
		assertEquals(n1.getID(), n1.getSuccessor().getID());
		assertEquals(n1.getID(), n1.getPredecessor().getID());
	}
	
	@Test
	public void nodeJoinsExistingNetwork() {
		n1.join();
		n2.join("localhost", 50001);
		
		//Joining node gets correct pre/suc
		assertEquals(n2.getSuccessor().getID(), n1.getID());
		assertEquals(n2.getPredecessor().getID(), n1.getID());
		
		//Existing nodes pre/suc gets updated
		assertEquals(n1.getSuccessor().getID(), n2.getID());
		assertEquals(n1.getPredecessor().getID(), n2.getID());
		
	}
}

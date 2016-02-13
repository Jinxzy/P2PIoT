package milestone1;

import org.apache.commons.codec.digest.DigestUtils;

public class Node {
	private Node predecessor;
	private Node successor;
	private String id;
	
	//Mostly for testing purposes
	private String ip;
	private String port;
	
	public Node(String ip, String port) {
		this.ip = ip;
		this.port = port;
		id = DigestUtils.sha1Hex(ip + ":" + port); //Sha1 hash using Apache commons library
	}

	public void join(Node n) { //n is existing known node to bootstrap into the network
		//Initialize own successor/predecessors
		successor = n.findSuccessor(id);
		System.out.println(port + " found successor: " + successor.getPort());
		predecessor = successor.getPredecessor();
		System.out.println(port + " found predecessor: " + predecessor.getPort());
		
		//Update successors and predecessor with this node
		System.out.println("Updating other peers");
		updateOthers(); 
		
	}
	
	public void join() { //No known node, this node starts new network with just this node in it
		predecessor = this;
		successor = this;
		System.out.println("New network created");
	}
	
	public void leave() {
		predecessor.setSuccessor(successor);
		successor.setPredecessor(predecessor);
	}
	
	public Node findSuccessor(String id) {
		Node n = findPredecessor(id);
		return n.successor;
		
	}
	
	//The two checks here could be combined into one for efficiency, but I left them like this for now for clarity
	public Node findPredecessor(String id) {
		
		//Check if searched ID is greater/equal to this node, AND this nodes successor ID is smaller than this node's ID. 
		//If this is the case, we're crossing the "0-line" of the ring, and this node must be predecessor 
		//if (id >= this.id && successorID < this.id) 
		if(id.compareTo(this.id) >= 0 && successor.getID().compareTo(this.id) <= 0) {
			return this;
		}
		
		//Checks if the id searched for is greater/equal to this nodes ID, and smaller than successors nodes ID, in which case this node should be returned
		//if(id >= this.id && id < successorID()). 
		else if(id.compareTo(this.id) >= 0 && id.compareTo(successor.getID()) < 0) {
			return this;
		}
		else return successor.findPredecessor(id);
		
	}
	
	private void updateOthers() {
		predecessor.setSuccessor(this);
		successor.setPredecessor(this);
	}
	
	public String getID() {
		return id;
	}
	
	public String getPort() {
		return port;
	}
	
	public Node getPredecessor() {
		return predecessor;
	}
	
	public Node getSuccessor() {
		return successor;
	}
	
	public void setPredecessor(Node n) {
		predecessor = n;
	}
	
	public void setSuccessor(Node n) {
		successor = n;
	}
	
	//This was supposed to handle updating peers when entering the network more safely than a public setter for succ/pred, but didn't work so I left it for later
	
//	public void updateTables(Node n) {
//		System.out.println(port + ": updating successor");
//		//If received node's ID is between this and successor, make it the new successor
//		//if (nID > this.id && nID <= successorID)
//		if(this.id.compareTo(n.getID()) < 0 && n.getID().compareTo(successor.getID()) >= 0) {
//			successor = n;
//			System.out.println(port + ": New successor: " + n.port);
//		}
//		
//		System.out.println(port + ": updating predecessor");
//		//If received node's ID is between this and predecessor, make it the new predecessor
//		//if (nID < this.id && nID > predecessorID)
//		if(n.getID().compareTo(this.id) > 0 && predecessor.getID().compareTo(n.getID()) <= 0 )
//		{
//			predecessor = n;
//			System.out.println(port + ": New predecessor: " + n.port);
//		}
//	}
//	
	
}

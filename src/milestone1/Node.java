package milestone1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.sun.net.httpserver.HttpServer;

import org.apache.commons.codec.digest.DigestUtils;



@SuppressWarnings("restriction")

@Path("/")
public class Node {
	private NodeInfo thisNode;
	private NodeInfo predecessor;
	private NodeInfo successor;
	private RequestSender requestSender;
	private HttpServer requestHandler;
	private String id;
	
	private String ip;
	private int port;
	private NodeServer nodeServer;
	
	
	public Node(String ip, int port) {
		this.ip = ip;
		this.port = port;
		nodeServer = new NodeServer(port);
		id = hashIPPortToID(ip, port);
		thisNode = new NodeInfo(ip, port, id);
		requestSender = new RequestSender(thisNode);
	}

	public void join() { //No known node, this node starts new network with just this node in it
		predecessor = new NodeInfo(ip, port, id); //Itself
		successor = new NodeInfo(ip, port, id); //Itself
		System.out.println("New network created");
		listenToRequests();
	}

	public void join(String ip, int port) { //n is existing known node to bootstrap into the network

		System.out.println(this.port + ": Joining");
		
		//Initialize own successor/predecessors
		successor = requestSender.findIdSuccessor(ip, port, thisNode.getID());
		System.out.println(this.port + " found successor: " + successor.getPort());
		
		//No ID needed for path
		predecessor = requestSender.getNodePredecessor(successor);
		System.out.println(this.port + " found predecessor: " + predecessor.getPort());
		
		//Update successors and predecessor with this node
		System.out.println("Updating other peers");
		updateOthers(); 
		listenToRequests();
		System.out.println(this.port + ": Listening");
	}


	private void updateOthers() {
		requestSender.updateNodeSuccessor(predecessor, thisNode);
		requestSender.updateNodePredecessor(successor, thisNode);

	}

	public void leave() {
		requestSender.updateNodeSuccessor(predecessor, successor);
		requestSender.updateNodePredecessor(successor, predecessor);
		System.out.println(this.port + ": left network");
	}


	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, NodeInfo> status() { // return the node info
		Map<String, NodeInfo> data = new HashMap<String, NodeInfo>();
		data.put("node", thisNode);
		data.put("predecessor", this.predecessor);
		data.put("successor", this.successor);
		return data;
	}

	@GET
	@Path("/successor-of/{param}")
	public NodeInfo findSuccessor(@PathParam("param") String id) {
		NodeInfo n = findPredecessor(id);
		//No id needed for path
		NodeInfo nSuc = requestSender.getNodeSuccessor(n);
		return nSuc;
	}
	
	@GET
	@Path("/predecessor-of/{param}")
	public NodeInfo findPredecessor(@PathParam("param") String id) {
		
		//Checks if the id searched for is greater/equal to this nodes ID, and smaller/equal than successors nodes ID, in which case this node should be returned
		//if(id >= this.id && id < successorID()). 
		if(id.compareTo(this.id) > 0 && id.compareTo(successor.getID()) <= 0) {
			return thisNode;
		}
		
		//Check if successor is smaller than this node ID, and if searched ID is in between. If so we're crossing the '0' line, and this node is predecessor
		//if(succID < this.id)
		  //if(id > this.id || id < succID
		else if(successor.getID().compareTo(this.id) < 0) {
			if(id.compareTo(this.id) > 0 || id.compareTo(successor.getID()) < 0) {
				return thisNode;
			}
		}
		
		//Special case, only this node in network
		else if(successor.getID() == this.id && predecessor.getID() == this.id) {
			return thisNode;
		}
		
		return requestSender.findIdPredecessor(successor, id);
	}
	

	
	public String getID() {
		return id;
	}
	
	public int getPort() {
		return port;
	}
	
	@GET
	@Path("/predecessor")
	public NodeInfo getPredecessor() {
		return predecessor;
	}
	
	@GET
	@Path("/successor")
	public NodeInfo getSuccessor() {
		return successor;
	}
	
	@PUT
	@Path("/predecessor")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setPredecessor(NodeInfo n) {
		predecessor = n;
		return Response.status(200).entity(n).build();
	}
	
	@PUT
	@Path("/successor")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSuccessor(NodeInfo n) {
		successor = n;
		return Response.status(200).entity(n).build();
	}
	
	
	private String hashIPPortToID(String ip, int port) {
		String res = DigestUtils.sha1Hex(ip + ":" + port); //Sha1 hash using Apache commons library
		return res;
	}

	//Sets up the HTTP Server to listen for incoming requests, using the NodeServer class. Creating the server automatically starts it
	public void listenToRequests() {
		System.out.println("Starting Server\n");		
		try {
			requestHandler = nodeServer.createHttpServer(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopListening() {
		if (!(requestHandler == null)){
			requestHandler.stop(0);
		}
	}
	
	@GET
	@Path("/index")
	@Produces(MediaType.TEXT_HTML)
	public String showHTML() {
		
		String linkAddr = "http://" + thisNode.getIP() + ":" + thisNode.getPort() + "/index";
		String succ = "http://" + successor.getIP() + ":" + successor.getPort() + "/index";
		String pred = "http://" + predecessor.getIP() + ":" + predecessor.getPort() + "/index";
		String res = "";
		
		res += "<html>"
				+ "<body>"
				+ "This node: <a href=" + linkAddr + ">" + "http://" + thisNode.getIP() + ":" + thisNode.getPort() + "</a> " + thisNode.getID()  + "<br>"
				+ "Successor: <a href=" + succ + ">" + "http://" + successor.getIP() + ":" + successor.getPort() + "</a> " + successor.getID()  + " <br>"
				+ "Predecessor: <a href=" + pred + ">" + "http://" + predecessor.getIP() + ":" + predecessor.getPort() + "</a> " + predecessor.getID()  + " <br>"
				+ "</body>"
				+ "</html>";
		
		return res;
	}
	
	//Probably unnecessary?
	@GET
	@Path("/returnNode")
	@Produces(MediaType.APPLICATION_JSON)
	public NodeInfo returnNode() { //Returns NodeInfo requests
		NodeInfo n = new NodeInfo(ip, port, id);
		return n;
	}
	
	//Probably unnecessary?
	@GET
	@Path("/showNode")
	@Produces(MediaType.APPLICATION_JSON)
	public List<NodeInfo> showNode() { //Returns NodeInfo requests
		ArrayList<NodeInfo> list = new ArrayList<NodeInfo>();
		NodeInfo n = new NodeInfo(ip, port, id);
		list.add(n);
		list.add(predecessor);
		list.add(successor);
		return list;
	}


}

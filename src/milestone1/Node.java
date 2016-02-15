package milestone1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.sun.net.httpserver.HttpServer;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.map.ObjectMapper;


@SuppressWarnings("restriction")

@Path("/node")
public class Node {
	private NodeInfo thisNode;
	private NodeInfo predecessor;
	private NodeInfo successor;
	private RequestSender requestSender;
	private String id;
	
	private String ip;
	private int port;
	private NodeServer nodeServer;
	
	
	public Node(String ip, int port) {
		this.ip = ip;
		this.port = port;
		nodeServer = new NodeServer(port);
		requestSender = new RequestSender();
		id = hashIPPortToID(ip, port);
		thisNode = new NodeInfo(ip, port, id);
	}

	public void join(String ip, int port) { //n is existing known node to bootstrap into the network

		System.out.println(this.port + ": Joining");
		
		//Initialize own successor/predecessors
		successor = requestSender.findSuccessor(ip, port, id);
		System.out.println(this.port + " found successor: " + successor.getPort());
		
		predecessor = requestSender.getPredecessor(successor.getIP(), successor.getPort());
		System.out.println(this.port + " found predecessor: " + predecessor.getPort());
		
		//Update successors and predecessor with this node
		System.out.println("Updating other peers");
		updateOthers(); 
		listenToRequests();
		System.out.println(this.port + ": Listening");
	}
	
	
	public void join() { //No known node, this node starts new network with just this node in it
		predecessor = new NodeInfo(ip, port, id); //Itself
		successor = new NodeInfo(ip, port, id); //Itself
		System.out.println("New network created");
		listenToRequests();
	}
	
	public void leave() {
		//TODO
//		predecessor.setSuccessor(successor);
//		successor.setPredecessor(predecessor);
	}
	
	@GET
	@Path("/findSuc/{param}")
	public NodeInfo findSuccessor(@PathParam("param") String id) {
		NodeInfo n = findPredecessor(id);
		NodeInfo nSuc = requestSender.getSuccessor(n.getIP(), n.getPort());
		return nSuc;
	}
	
	@GET
	@Path("/findPred/{param}")
	public NodeInfo findPredecessor(@PathParam("param") String id) {
		
		
		//Checks if the id searched for is greater/equal to this nodes ID, and smaller than successors nodes ID, in which case this node should be returned
		//if(id >= this.id && id < successorID()). 
		if(id.compareTo(this.id) >= 0 && id.compareTo(successor.getID()) < 0) {
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
		
		return requestSender.findPredecessor(successor.getIP(), successor.getPort(), id);

	}
	
	private void updateOthers() {
		requestSender.setPredecessor(predecessor.getIP(), predecessor.getPort(), thisNode);
		requestSender.setSuccessor(successor.getIP(), successor.getPort(), thisNode);
	}
	
	public String getID() {
		return id;
	}
	
	public int getPort() {
		return port;
	}
	
	@GET
	@Path("/getPred")
	public NodeInfo getPredecessor() {
		return predecessor;
	}
	
	@GET
	@Path("/getSuc")
	public NodeInfo getSuccessor() {
		return successor;
	}
	
	@POST
	@Path("/setPred")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setPredecessor(NodeInfo n) {
		predecessor = n;
		return Response.status(200).entity(n).build();
	}
	
	@POST
	@Path("/setSuc")
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
			HttpServer requestHandler = nodeServer.createHttpServer(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String msg) {
		String output = "Jersey say : " + id;
		return Response.status(200).entity(output).build();
	}
	
	@GET
	@Path("/returnNode")
	@Produces(MediaType.APPLICATION_JSON)
	public NodeInfo returnNode() { //Returns NodeInfo requests
		NodeInfo n = new NodeInfo(ip, port, id);
		return n;
	}
	
	
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
	
	@GET
	@Path("/showHTML")
	@Produces(MediaType.TEXT_HTML)
	public String showHTML() {
		
		String linkAddr = "http://" + thisNode.getIP() + ":" + thisNode.getPort() + "/node/showHTML";
		String succ = "http://" + successor.getIP() + ":" + successor.getPort() + "/node/showHTML";
		String pred = "http://" + predecessor.getIP() + ":" + predecessor.getPort() + "/node/showHTML";
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
}

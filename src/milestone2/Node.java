package milestone2;

import com.sun.net.httpserver.HttpServer;
import milestone2.chord.Key;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings("restriction")

@Path("/")
public class Node {
	private NodeInfo thisNode;
	private NodeInfo predecessor;
	private NodeInfo successor;
	private RequestSender requestSender;
	private HttpServer requestHandler;
	private int id;
	private NodeInfo[] fingers;
	
	private String ip;
	private int port;
	private NodeServer nodeServer;
	private Timer timer;
	private int updateTime;

	public Node(String ip, int port) {
		this.ip = ip;
		this.port = port;
		updateTime = 10;
		nodeServer = new NodeServer(port);
		id = Key.generate16BitsKey(ip, port);
		thisNode = new NodeInfo(ip, port, id);
		requestSender = new RequestSender(thisNode);
		fingers = new NodeInfo[16];
	}

	public void join() { //No known node, this node starts new network with just this node in it
		predecessor = new NodeInfo(ip, port, id); //Itself
		successor = new NodeInfo(ip, port, id); //Itself
		
		//Fill finger table with just this
		for(int i=0; i<16; i++) {
			fingers[0] = new NodeInfo(ip, port, id);
		}
		
		System.out.println("New network created");
		listenToRequests();
		createTimer();
	}

	public void join(String ip, int port) { //n is existing known node to bootstrap into the network

		System.out.println(this.port + ": Joining");
		
		//Initialize own successor/predecessors
		successor = requestSender.findIdSuccessor(ip, port, thisNode.getID());
		System.out.println(this.port + " found successor: " + successor.getPort());
		
		//No ID needed for path
		predecessor = requestSender.getNodePredecessor(successor);
		System.out.println(this.port + " found predecessor: " + predecessor.getPort());
		
		initFingerTable(ip, port);
		
		//Update successors and predecessor with this node
		System.out.println("Updating other peers");
		updateOthers(); 
		listenToRequests();
		createTimer();
		System.out.println(this.port + ": Listening");
	}
	
	
	public void initFingerTable(String ip, int port) {
		//Set first finger, which is just immediate successor
		fingers[0] = successor;
		
		//Fill finger table
		NodeInfo recentFinger = fingers[0];
		int nextFingerID = 0;
		
		//This is awful, will fix later... 
		for(int i=1; i<16; i++) {
			nextFingerID = (thisNode.getID() + (int) Math.pow(2, i)) % (int) Math.pow(2, 16);
			
			//If the next fingerID is lower than our previous finger, it is the same and we don't need to ask for it
			if(nextFingerID > thisNode.getID() && nextFingerID <= recentFinger.getID()) {
				fingers[i] = recentFinger;
			}
			
			//This node is the highest ID node and searched finger is higher, so same finger is set
			else if(nextFingerID > thisNode.getID() && recentFinger.getID() < thisNode.getID()) {
				fingers[i] = recentFinger;
			}
			
			//Next finger searched crosses 0, but is still smaller than recently set finger
			else if(nextFingerID < thisNode.getID() && recentFinger.getID() >= nextFingerID) {
				fingers[i] = recentFinger;
			}
			
			//We've come full circle and found ourself as most preceeding finger, so we will be as well for all following IDs
			else if (recentFinger.getID() == thisNode.getID()) {
				fingers[i] = recentFinger;
			}
			
			//ID searched is higher than our current finger, so we ask it to find the next one for us.
			else {
				//System.out.println("Asking " + port + " to find" + nextFingerID);
				recentFinger = requestSender.findIdSuccessor(ip, port, nextFingerID);
				fingers[i] = recentFinger;
			}
		}
	}
	
	//Creates a timer that updates the UpdateTime in secs. 
	public void createTimer() {
		timer = new Timer();

		timer.schedule( new TimerTask() {
			public void run() {
				initFingerTable(ip, port);
			}
		}, 0, updateTime * 1000);
	}
	
	public void printTable() {
		for (NodeInfo n : fingers) {
			System.out.println(n.getIP() + ":" + n.getPort() + "/" + n.getID());
		}
	}

	private void updateOthers() {
		requestSender.updateNodeSuccessor(predecessor, thisNode);
		requestSender.updateNodePredecessor(successor, thisNode);
	}

	public void leave() {
		requestSender.updateNodeSuccessor(predecessor, successor);
		requestSender.updateNodePredecessor(successor, predecessor);
		timer.cancel();
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
	public NodeInfo findSuccessor(@PathParam("param") int id) {
		NodeInfo n = findPredecessor(id);
		//No id needed for path
		NodeInfo nSuc = requestSender.getNodeSuccessor(n);
		return nSuc;
	}
	
	@GET
	@Path("/predecessor-of/{param}")
	public NodeInfo findPredecessor(@PathParam("param") int id) {
		
		//Checks if the id searched for is greater/equal to this nodes ID, and smaller/equal than successors nodes ID, in which case this node should be returned
		//if(id >= this.id && id < successorID()).
		if( this.id < id &&  successor.getID() >= id){
			return thisNode;
		}
		//Check if successor is smaller than this node ID, and if searched ID is in between. If so we're crossing the '0' line, and this node is predecessor
		//if(succID < this.id)
		  //if(id > this.id || id < succID
		else if( successor.getID() < this.id ) { // we are the highest id in the hood
			if (this.id < id || successor.getID() >= id) return thisNode;
		}
		//Special case, only this node in network
		else if(successor.getID() == this.id) {
			return thisNode;
		}
		
		//Using closesPreceedingFinger doesn't work atm. I suspect we just need to update finger tables
		//Immediately upon new joining node
		return requestSender.findIdPredecessor(successor, id);
		//return requestSender.findIdPredecessor(closestPreceedingFinger(id), id);
	}
	
	
	public NodeInfo closestPreceedingFinger(int id) {
		for(int i=15; i>=0; i--) {
			int fID = fingers[i].getID();
			if(isInRange(fID, thisNode.getID(), id)) {
				System.out.println("Returned : " + fingers[i].getID());
				return fingers[i];
			}
		}
		//Returning this node here means something went wrong with the node routing, and it will loop forever
		System.out.println("Shouldn't happen");
		return thisNode;
	}
	
	//Doesn't check for equalities properly yet. Not sure which would be appropriate to use for all our comparisons
	private boolean isInRange(int id, int from, int to) {
		if(id > from && id <= to){return true;}
		else if (id > from && to < from) {return true;}
		else if (id < from && id < to) {return true;}
		else if (from == to) {return true;}
		else return false;
	}
	
	public int getID() {
		return id;
	}
	
	public int getPort() {
		return port;
	}
	public String getIp() {return ip; }
	
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
		
		String res = "";
		
		res += "<html>"
				+ "<body>"
				+ "This node: " + buildNodeLink(thisNode)
				+ "Successor: " + buildNodeLink(successor)
				+ "Predecessor: " + buildNodeLink(predecessor)
				+ printFingerTable(fingers)
				+ "</body>"
				+ "</html>";
		
		return res;
	}
	
	private String buildNodeLink(NodeInfo n) {
		if (n == null) {return null;}
		String linkAddr = "http://" + n.getIP() + ":" + n.getPort() + "/index";
		return "<a href=" + linkAddr + ">" + "http://" + n.getIP() + ":" + n.getPort() + "</a> " + n.getID()  + "<br>";
	}
	
	private String printFingerTable(NodeInfo[] fingers) {
		String res = "Fingers: <br>";
		for (NodeInfo n : fingers) {
			if(n != null) {res += n.getID() + "<br>";}
		}
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

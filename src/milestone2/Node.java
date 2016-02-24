package milestone2;

import com.sun.net.httpserver.HttpServer;
import milestone2.chord.Key;

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
		updateTime = 30;
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
			fingers[i] = new NodeInfo(ip, port, id);
		}
		
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
		
		initFingerTable(ip, port);
		
		listenToRequests();
		System.out.println(this.port + ": Listening");
		
		//Update successors and predecessor with this node
		System.out.println("Updating other peers");
		updateOthers(); 
		System.out.println("Join complete!");
	}
	
	
	public void initFingerTable(String ip, int port) {
		//Set first finger, which is just immediate successor
		fingers[0] = successor;
		
		//Fill finger table
		NodeInfo recentFinger = fingers[0];
		int nextFingerID = 0;
		
		for(int i=1; i<16; i++) {
			nextFingerID = (thisNode.getID() + (int) Math.pow(2, i)) % (int) Math.pow(2, 16);
			
			if(isInRange(nextFingerID, thisNode.getID(), recentFinger.getID())) {
				fingers[i] = recentFinger;
			}
			
			//ID searched is higher than our current finger, so we ask it to find the next one for us.
			else {
				recentFinger = requestSender.findIdSuccessor(ip, port, nextFingerID);
				fingers[i] = recentFinger;
			}
			
		}
	}
	
	@PUT
	@Path("/update/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateFingerTable(NodeInfo n, @PathParam("param") int fingerNr) {
		
		if(isInRange(n.getID(), thisNode.getID(), fingers[fingerNr].getID())){
			fingers[fingerNr] = n;
			requestSender.updateFingerTable(predecessor, n, fingerNr);
		}
		
		return Response.status(200).entity(n).build();
	}
	
	//Creates a timer that updates the figners UpdateTime in secs. 
	public void createTimerUpdateFingers() {
		timer = new Timer();

		timer.schedule( new TimerTask() {
			public void run() {
				initFingerTable(ip, port);
			}
		}, 0, updateTime * 1000);
	}
	
	//Checks the predecessor every UpdateTime in secs.
	public void createTimerCheckPredecessor() {
		timer = new Timer();

		timer.schedule( new TimerTask() {
			public void run() {
				NodeInfo n = requestSender.getNodePredecessor(thisNode);
				if (n == null) {
					predecessor = requestSender.findIdPredecessor(thisNode, thisNode.getID());
				}
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
		
		int preceedingNodeID;
		
		for(int i=0; i<16; i++) {
			preceedingNodeID = ((int)Math.pow(2, 16) + thisNode.getID() - (int)Math.pow(2, i)) % (int)Math.pow(2, 16);
			
			NodeInfo p = findPredecessor(preceedingNodeID);
			requestSender.updateFingerTable(p, thisNode, i);
		}
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
			System.out.println(this.id + ": Routing done");
			return thisNode;
		}
		//Check if successor is smaller than this node ID, and if searched ID is in between. If so we're crossing the '0' line, and this node is predecessor
		//if(succID < this.id)
		  //if(id > this.id || id < succID
		else if( successor.getID() < this.id ) { // we are the highest id in the hood
			if (this.id < id || successor.getID() >= id) {
				System.out.println(this.id + ": Routing done");
				return thisNode;
			}
		}
		//Special case, only this node in network
		else if(successor.getID() == this.id) {
			System.out.println(this.id + ": Routing done");
			return thisNode;
		}
		
		//Using closesPreceedingFinger doesn't work atm. I suspect we just need to update finger tables
		//Immediately upon new joining node
		
		System.out.println(this.id + ": Routed to node " + successor.getID());
		//return requestSender.findIdPredecessor(successor, id);
		//System.out.println(this.id + ": Routed to node " + closestPreceedingFinger(id).getID());
		return requestSender.findIdPredecessor(closestPreceedingFinger(id), id);
	}
	
	
	public NodeInfo closestPreceedingFinger(int id) {
		for(int i=15; i>=0; i--) {
			int fID = fingers[i].getID();
			if(isInRange(fID, thisNode.getID(), id)) {
				return fingers[i];
			}
		}
		//Returning this node here means something went wrong with the node routing, and it will loop forever
		System.out.println("Shouldn't happen");
		return thisNode;
	}
	
	//Doesn't check for equalities properly yet. Not sure which would be appropriate to use for all our comparisons
	private boolean isInRange(int id, int from, int to) {
		if(id >= from && id < to){return true;}
		else if (to < from) {
			if (id > from || id < to) {return true;}}
		else if (from == to) {return true;}
		return false;
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
	
	@GET
	@Path("/successor-of/{param}")
	@Produces(MediaType.TEXT_HTML)
	public String showSuccesorOf(@PathParam("param") int id) {
		NodeInfo n = findSuccessor(id);
		return(requestSender.displayIndexPage(n));
		//return standardJsonToHtml(n);
	}
	
	@GET
	@Path("/predecessor-of/{param}")
	@Produces(MediaType.TEXT_HTML)
	public String showPredecessorOf(@PathParam("param") int id) {
		NodeInfo n = findPredecessor(id);
		return(requestSender.displayIndexPage(n));
		//return standardJsonToHtml(n);
	}
	
	private String standardJsonToHtml(NodeInfo n) {
		String res = "";	
		res += "<html>"
				+ "<body>"
				+ "This node information: <br>" 
				+ "ID: " + n.getID() + "<br>"
				+ "IP: " + n.getIP() + "<br>"
				+ "Port: " + n.getPort() + "<br>"
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
		String res = "<br><h3>Fingers: </h3>";
		res += "<table> <tr> <th>Id</th> <th>Belongs to</th> </tr>";
		int nextFingerID = 0;
		for (int i = 0; i < fingers.length; i++) {
			if(fingers[i] != null) {
				nextFingerID = (thisNode.getID() + (int) Math.pow(2, i)) % (int) Math.pow(2, 16);
				
				res += "<tr> <td> " + nextFingerID + " </td> <td>" + fingers[i].getID() + "</td> </tr>";
			}
		}
		res += "</table>";
		return res;
	}
}

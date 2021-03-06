package milestone4;

import com.sun.net.httpserver.HttpServer;

import milestone2.chord.Key;
import milestone4.views.HtmlParser.Templates;
import milestone4.views.HtmlParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



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
	private ArrayList<PhotonData> photonData;
	private NodeInfo[] succList;

	private String ip;
	private int port;
	private NodeServer nodeServer;
	private Timer timer;
	private Timer shutdownTimer;
	private int updateTime;
	private int updatePhotonTime  = 5000;
	private JSONObject photon;
	private boolean isPhotonActive;
	private int photonId;
	private HtmlParser parser;
	private int replicas;
	private int replicatedValue;


	public Node(String ip, int port) {
		this.ip = ip;
		this.port = port;
		updateTime = 30;
		photon = new JSONObject();
		isPhotonActive = false;
		nodeServer = new NodeServer(port);
		id = Key.generate16BitsKey(ip, port);
		thisNode = new NodeInfo(ip, port, id);
		requestSender = new RequestSender(thisNode);
		fingers = new NodeInfo[16];
		parser  = new HtmlParser();
		timer = new Timer();
		photonData = new ArrayList<PhotonData>();
		succList = new NodeInfo[2];
		replicas = 3;
		replicatedValue = 0;

	}

	public void join() { //No known node, this node starts new network with just this node in it
		predecessor = new NodeInfo(ip, port, id); //Itself
		successor = new NodeInfo(ip, port, id); //Itself

		//Fill finger table with just this
		for(int i=0; i<16; i++) {
			fingers[i] = new NodeInfo(ip, port, id);
		}

		//createTimerCheckPredecessor();
		
		System.out.println("New network created");
		System.out.println("Check the node at status at " + thisNode.getIndex() );
		listenToRequests();
	}

	public void join(String ip, int port) { //n is existing known node to bootstrap into the network

		System.out.println(this.port + ": Joining");

		//Initialize own successor/predecessors
		successor = requestSender.findIdSuccessor(ip, port, thisNode.getID());

		//No ID needed for path
		predecessor = requestSender.getNodePredecessor(successor);

		takeResponsibilities();

		//createTimerCheckPredecessor();
		
		initFingerTable(ip, port);
		listenToRequests();
		System.out.println(this.port + ": Listening");

		//Update successors and predecessor with this node
		updateOthers();
		System.out.println("Join complete!");
		System.out.println("Check the node at status at " + thisNode.getIndex() );
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

	public void printPhotonInfo() {
		//System.out.println(photon.toString());
	}

	//Finds the responsible node for the photon and updates it and the successor of it
	public void findResponsibleNode() {
		String photonJson = requestSender.findSpark();
		try {
			JSONObject temp = new JSONObject(photonJson);
			JSONObject temp2 = temp.getJSONObject("coreInfo");
			String deviceID = temp2.getString("deviceID");
			int convertedID = Key.generate16BitsKey(deviceID);
			NodeInfo responsible = findSuccessor(convertedID);
			//NodeInfo resposibleSuccessor = requestSender.getNodeSuccessor(responsible);
			requestSender.updatePhoton(responsible);
			//requestSender.updatePhoton(resposibleSuccessor);
			System.out.println("PhotonID: " + convertedID);
			System.out.println("Responsible is:\n" + responsible.getID());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void takeResponsibilities() {
		String responsible = requestSender.takePhotonResponsibility(successor, thisNode.getID());
		
		if (responsible.equals("true")) {
			System.out.println("Becoming new responsible node");
			photonData = requestSender.getPhotonData(successor).getList();
			updatePhoton(thisNode); //Begin being the responsible node
		}
	}
	
	@GET
	@Path("/getPhotonData")
	public DataListWrapper getPhotonData() {
		
		DataListWrapper wrapper = new DataListWrapper();
		wrapper.setList(photonData);
		
		return wrapper;
	}
	
	@GET
	@Path("/takePhotonResponsibility/{param}")
	public String takePhotonResponsibility(@PathParam("param") int nodeID) {
		if(isPhotonActive && (photonId < nodeID)) {
			isPhotonActive = false;
			timer.cancel();
			return "true";
		}
		else return "false";
	}

	//Updates the photon of this node
	@PUT
	@Path("/update-photon/")
	public Response updatePhoton(NodeInfo target) {
		String photonJson = requestSender.findSpark();
		try {
			photon = new JSONObject(photonJson);
			JSONObject coreInfo = photon.getJSONObject("coreInfo");
			String deviceID = coreInfo.getString("deviceID");
			photonId = Key.generate16BitsKey(deviceID);
			System.out.println("deviceID returned: " + deviceID);
			System.out.println("deviceID int returned: " + photonId);
			isPhotonActive = true;
			createPhotonUpdateTimer();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//System.out.println("Spark returned:\n" + photonJson);
		return Response.status(200).entity(photon).build();
	}

	public void refreshPhotonInfo() {
		if(isPhotonActive) {
			System.out.println(this.port + ": Updating photon");
			String photonJson = requestSender.findSpark();
			try {
				photon = new JSONObject(photonJson);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	//Requests new photon result from cloud, and stores the result(light) + time in the PhotonData list
	public void createPhotonUpdateTimer() {
		timer = new Timer();
		timer.schedule( new TimerTask() {
			public void run() {
				refreshPhotonInfo();
				
				String time = "";
				int light = 0;
				try {
					time = photon.getJSONObject("coreInfo").getString("last_heard");
					light = photon.getInt("result");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				PhotonData data = new PhotonData(time, light);
				data.replica = replicas;
				photonData.add(data);
				
				if(successor.getID() != thisNode.getID()) {
					requestSender.sendPhotonData(successor, data);
				}
				
				//System.out.println(thisNode.getPort() + ": " + photonData.toString());
			}
		}, 0, updatePhotonTime);
	}
	
	//Receive data from node responsible for data, for replication purposes
	@POST
	@Path("/sendPhotonData")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response replicatePhotonData(PhotonData pd) {

		if(photonData.isEmpty()) {
			photonData = requestSender.getPhotonData(predecessor).getList();
		}
		
		else {
			photonData.add(pd);
		}
		replicatedValue = pd.replica--;
		if(pd.replica > 0){

			if(successor.getID() != thisNode.getID()) {
				requestSender.sendPhotonData(successor, pd);
			}
		}
		//System.out.println(thisNode.getPort() + " recieved bunch of data: " + photonData.toString());
		
		return Response.status(200).entity(pd).build();
	}
	
	@GET
	@Path("/photonData")
	@Produces(MediaType.TEXT_PLAIN)
	public String showPhotonData() {
		String res = "";
		for(PhotonData pd : photonData) {
			res += pd.toString();
		}
		return res;
	}
	

	//Updates the finger table with the {param} ID node as potential finger
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

	//Checks the predecessor every UpdateTime in secs.
	public void createTimerCheckPredecessor() {
		timer.schedule( new TimerTask() {
			public void run() {
				NodeInfo n = requestSender.getNodePredecessor(thisNode);
				if (n == null) {
					takeResponsibilities();
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

	@POST
	@Path("/kill")
	public Response leave() {		
		if (isPhotonActive) {
			requestSender.updatePhoton(successor);
		}
		requestSender.updateNodeSuccessor(predecessor, successor);
		requestSender.updateNodePredecessor(successor, predecessor);
		timer.cancel();

		killCommuncations();
		return Response.status(200).build();
	}

	public void killCommuncations() {
		shutdownTimer = new Timer();
		shutdownTimer.
				schedule( new TimerTask() {
			public void run() {
				System.out.println(thisNode.getPort() + ": left network");
				System.out.println(thisNode.getPort() + ": Stops listening");
				stopListening();

				//shutdownTimer.cancel();
			}
		}, 500);
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
			if (this.id < id || successor.getID() >= id) {
				return thisNode;
			}
		}
		//Special case, only this node in network
		else if(successor.getID() == this.id) {
			return thisNode;
		}

		//System.out.println(this.id + ": Routed to node " + successor.getID());
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
	@Path("/photon2")
	@Produces(MediaType.TEXT_HTML)
	public String showPhoton() {
		String res = "";
		res += "<html>"
				+ "<body>"
				+ "Photon:\n" + photon
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


	public void loadCommonContext(Map<String, Object> context){
		context.put("node", thisNode);
		context.put("predecessor", predecessor);
		context.put("successor", successor);
		context.put("enable_leave_network", true);
		if(isPhotonActive){
			Map<String, Object> photon_map = new HashMap<String, Object>();
			photon_map.put("id", photonId);
			photon_map.put("link", "http://" + ip + ":" + port + "/photon");
			photon_map.put("data", photon);
			photon_map.put("replicas", replicas);
			context.put("photon", photon_map);
		}else if(photonData.size() > 0) {
			context.put("photon_data", true);
			context.put("replicated", replicatedValue);
		}
	}

	@GET
	@Path("/index")
	@Produces(MediaType.TEXT_HTML)
	public String foo() {
		Map<String, Object> context = new HashMap<String, Object>();
		loadCommonContext(context);
		context.put("fingers", fingers);


		return  parser.parse(Templates.INDEX, context);
	}

	@GET
	@Path("/photon")
	@Produces(MediaType.TEXT_HTML)
	public String foo2() {
		Map<String, Object> context = new HashMap<String, Object>();
		loadCommonContext(context);

		return  parser.parse(Templates.PHOTON, context);
	}

	@GET
	@Path("/photon/light-data")
	@Produces(MediaType.APPLICATION_JSON)
	public String light() {
		return light2(photonData.size());
	}


	@GET
	@Path("/photon/light-data-last/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public String light2(@PathParam("param") int total) {
		if (photonData.size() < total) total = photonData.size();
		ArrayList<String> time = new ArrayList<String>();
		ArrayList<Integer> light = new ArrayList<Integer>();

		for(int i = 0; i < total; i++){
			time.add(photonData.get(photonData.size() - total + i).getTime());
			light.add(photonData.get(photonData.size() - total + i).getLight());
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("time", new JSONArray(time));
		result.put("light", new JSONArray(light));
		return new JSONObject(result).toString();
	}

}

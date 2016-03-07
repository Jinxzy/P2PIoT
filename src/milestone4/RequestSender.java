package milestone4;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

public class RequestSender {
	
	private Client client;
	private WebTarget resource;
	private Builder request;
	private NodeInfo thisNode;

	public RequestSender( NodeInfo n) {
		thisNode = n;
		client = ClientBuilder.newClient();
	}

	public NodeInfo findIdSuccessor(String ip, int port, int id)
	{
		resource = client.target("http://" + ip + ":" + port + "/successor-of/" + id);
		return get(resource);
	}

	public NodeInfo getNodePredecessor(NodeInfo node )
	{
		resource = client.target("http://" + node.getIP() + ":" +  node.getPort() + "/predecessor");
		return get(resource);
	}

	public NodeInfo getNodeSuccessor(NodeInfo node )
	{
		resource = client.target("http://" + node.getIP() + ":" +  node.getPort() + "/successor");
		return get(resource);
	}

	public NodeInfo findIdSuccessor(NodeInfo node, int id)
	{
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/successor-of/" + id);
		return get(resource);
	}

	public NodeInfo findIdPredecessor(NodeInfo node, int id)
	{
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/predecessor-of/" + id);
		return get(resource);
	}
	
	public String takePhotonResponsibility(NodeInfo node, int nodeID) {
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/takePhotonResponsibility/" + nodeID);
		return getString(resource);
	}
	
	public DataListWrapper getPhotonData(NodeInfo node) {
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/getPhotonData");
		return getList(resource);
	}
	
	public DataListWrapper getList(WebTarget resource){
		request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();

		if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {

			String jsonRes = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			DataListWrapper list = null;
			try {
				list = mapper.readValue(jsonRes, DataListWrapper.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;

		} else {
			System.out.println("Error requesting node! " + response.getStatus());
			return null;
		}
	}

	public NodeInfo get(WebTarget resource){
		request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();

		if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {

			String jsonRes = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			NodeInfo node = null;
			try {
				node = mapper.readValue(jsonRes, NodeInfo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return node;

		} else {
			System.out.println("Error requesting node! " + response.getStatus());
			return null;
		}
	}

	public String getString(WebTarget resource){
		request = resource.request();
		request.accept(MediaType.TEXT_HTML);
		Response response = request.get();

		if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {

			String res = response.readEntity(String.class);
			return res;

		} else {
			System.out.println("Error requesting node! " + response.getStatus());
			return null;
		}
	}
	

	public void updateNodeSuccessor(NodeInfo node, NodeInfo successor){
		client = ClientBuilder.newClient();
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/successor");
		this.put(resource, successor);
	}

	public void updateNodePredecessor(NodeInfo node, NodeInfo predecessor){
		client = ClientBuilder.newClient();
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/predecessor");
		this.put(resource, predecessor);
	}

	public void updateFingerTable(NodeInfo target, NodeInfo sender, int fingerNr) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + target.getIP() + ":" + target.getPort() + "/update" + "/" + fingerNr);
		this.put(resource, sender);
	}

	public void updatePhoton(NodeInfo target) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + target.getIP() + ":" + target.getPort() + "/update-photon/");
		this.put(resource, target);
	}
	
	public void sendPhotonData(NodeInfo target, PhotonData data) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + target.getIP() + ":" + target.getPort() + "/sendPhotonData");
		this.post(resource, data);
	}
	
	

	public String displayIndexPage(NodeInfo node) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/index");
		return getString(resource);
	}

	public void post(WebTarget resource, PhotonData data)
	{
		Builder ib = resource.request(MediaType.APPLICATION_JSON);
		Response res = ib.post(Entity.entity(data, MediaType.APPLICATION_JSON));
		System.out.println(res.getStatus());
	}

	public void post(WebTarget resource, NodeInfo n)
	{
		Builder ib = resource.request(MediaType.APPLICATION_JSON);
		Response res = ib.post(Entity.entity(n, MediaType.APPLICATION_JSON));
	}

	public void put(WebTarget resource, NodeInfo n)
	{
		Builder ib = resource.request(MediaType.APPLICATION_JSON);
		Response res = ib.put(Entity.entity(n, MediaType.APPLICATION_JSON));
	}

	public String findSpark()
	{
		resource = client.target("https://api.spark.io/v1/devices/2b0023000247343138333038/analogvalue?access_token=c2f1f7a26afd51a45e7ad921058164cbf08d1708");
		request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();
		response.getStatusInfo().getFamily();

		if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {

			String jsonRes = response.readEntity(String.class);
			return jsonRes;

		} else {
			System.out.println("Error requesting node! " + response.getStatus());
			return null;
		}
	}

}

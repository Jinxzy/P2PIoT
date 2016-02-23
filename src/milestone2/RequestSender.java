package milestone2;

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
	
	public String displayIndexPage(NodeInfo node) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/index");
		return getString(resource);
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

}

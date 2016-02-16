package milestone1;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.codehaus.jackson.map.ObjectMapper;

public class RequestSender {
	
	private Client client;
	private WebTarget resource;
	private Builder request;
	private NodeInfo thisNode;
	private NodeInfo predecessor;
	private NodeInfo successor;
	
	public RequestSender( NodeInfo n) {
		thisNode = n;
		client = ClientBuilder.newClient();
	}

	public NodeInfo findIdSuccessor(String ip, int port, String id)
	{
		resource = client.target("http://" + ip + ":" + port + "/successor-of/" + id);
		return get(resource);
	}

	public NodeInfo getNodePredecessor( NodeInfo node )
	{
		resource = client.target("http://" + node.getIP() + ":" +  node.getPort() + "/predecessor");
		return get(resource);
	}

	public NodeInfo getNodeSuccessor( NodeInfo node )
	{
		resource = client.target("http://" + node.getIP() + ":" +  node.getPort() + "/successor");
		return get(resource);
	}

	public NodeInfo findIdSuccessor(NodeInfo node, String id)
	{
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/successor-of/" + id);
		return get(resource);
	}

	public NodeInfo findIdPredecessor(NodeInfo node, String id)
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

	public void updateNodeSuccessor(NodeInfo node, NodeInfo successor){
		client = ClientBuilder.newClient();
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/successor");
		this.post(resource, successor);
	}

	public void updateNodePredecessor(NodeInfo node, NodeInfo predecessor){
		client = ClientBuilder.newClient();
		resource = client.target("http://" + node.getIP() + ":" + node.getPort() + "/predecessor");
		this.post(resource, predecessor);
	}


	public void post(WebTarget resource, NodeInfo n)
	{
		Invocation.Builder ib = resource.request(MediaType.APPLICATION_JSON);
		Response res = ib.post(Entity.entity(n, MediaType.APPLICATION_JSON));
	}


}

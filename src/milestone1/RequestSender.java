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
	
	public RequestSender() {
		client = ClientBuilder.newClient();
	}
	
	public void postMethod(String ip, int port, NodeInfo n, String method) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + ip + ":" + port + "/node/" + method);
		Invocation.Builder ib = resource.request(MediaType.APPLICATION_JSON);
		Response res = ib.post(Entity.entity(n, MediaType.APPLICATION_JSON));
	}
	
	public NodeInfo getMethod(String ip, int port, String id, String method) {
		resource = client.target("http://" + ip + ":" + port + "/node/" + method + "/" + id);
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
}

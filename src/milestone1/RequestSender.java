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
	
	/*
	//Sets the predecessor of the node on ip:port to 'n'
	public void setPredecessor(String ip, int port, NodeInfo n) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + ip + ":" + port + "/node/setPred");
		Invocation.Builder ib = resource.request(MediaType.APPLICATION_JSON);
		Response res = ib.post(Entity.entity(n, MediaType.APPLICATION_JSON));
	}
	
	//Sets the successor of the node on ip:port to 'n'
	public void setSuccessor(String ip, int port, NodeInfo n) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + ip + ":" + port + "/node/setSuc");
		Invocation.Builder ib = resource.request(MediaType.APPLICATION_JSON);
		Response res = ib.post(Entity.entity(n, MediaType.APPLICATION_JSON));
	}
	*/
/*
	public NodeInfo findSuccessor(String ip, int port, String id) {
		resource = client.target("http://" + ip + ":" + port + "/node/findSuc/" + id);
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

	public NodeInfo findPredecessor(String ip, int port, String id) {
		resource = client.target("http://" + ip + ":" + port + "/node/findPred/" + id);
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
	*/

	/*
	public NodeInfo getNode(String ip, int port) {

		resource = client.target("http://" + ip + ":" + port + "/node/returnNode");
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

	public NodeInfo getSuccessor(String ip, int port) {
		resource = client.target("http://" + ip + ":" + port + "/node/getSuc");
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
	
	public NodeInfo getPredecessor (String ip, int port) {
		resource = client.target("http://" + ip + ":" + port + "/node/getPred");
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
	*/
	
	public void postMethod(String ip, int port, NodeInfo n, String method) {
		client = ClientBuilder.newClient();
		resource = client.target("http://" + ip + ":" + port + "/node/" + method);
		Invocation.Builder ib = resource.request(MediaType.APPLICATION_JSON);
		Response res = ib.post(Entity.entity(n, MediaType.APPLICATION_JSON));
	}
	
	public NodeInfo findMethod(String ip, int port, String id, String method) {
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
	
	public NodeInfo getMethod (String ip, int port, String method) {
		resource = client.target("http://" + ip + ":" + port + "/node/" + method);
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

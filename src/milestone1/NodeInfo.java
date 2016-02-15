package milestone1;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

//Basic POJO that gets sent over network as JSON
public class NodeInfo {

	private String ip;
	private int port;
	private String id;
	
	
	public NodeInfo (String ip, int port, String id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
	}
	
	public NodeInfo() {
	}

	@JsonProperty("IP")
	public String getIP() {
		return ip;
	}
	
	@JsonProperty("port")
	public int getPort() {
		return port;
	}
	
	@JsonProperty("ID")
	public String getID() {
		return id;
	}
	
	@JsonIgnore
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	@JsonIgnore
	public void setPort(int port) {
		this.port = port;
	}
	
	@JsonIgnore
	public void setID(String id) {
		this.id = id;
	}
}

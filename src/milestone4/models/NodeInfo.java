package milestone4.models;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

//Basic POJO that gets sent over network as JSON
public class NodeInfo {

	private String ip;
	private int port;
	private int id;
	
	
	public NodeInfo (String ip, int port, int id) {
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
	public int getID() {
		return id;
	}

	@JsonProperty("link")
	public String getLink() {
		return "http://" + ip + ":" + port + "/";
	}

	@JsonProperty("kill")
	public String getKill() {
		return "http://" + ip + ":" + port + "/kill";
	}

	@JsonProperty("index")
	public String getIndex() {
		return "http://" + ip + ":" + port + "/index";
	}

	@JsonProperty("repr")
	public String getRepresentation() {
		return "http://" + ip + ":" + port + "/" + id;
	}

	public int generateFingerId(int index){
		return (id + (int) Math.pow(2, index)) % (int) Math.pow(2, 16);
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
	public void setID(int id) {
		this.id = id;
	}
}

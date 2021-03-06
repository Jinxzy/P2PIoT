package milestone4;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class PhotonData {
	
	private String time;
	private int light;
	public int replica;
	
	public PhotonData(String time, int light) {
		this.light = light;
		this.time = time;
		this.replica = 0;
	}
	
	public PhotonData() {
	}
	
	@JsonProperty("time")
	public String getTime() {
		return time;
	}
	
	@JsonProperty("light")
	public int getLight() {
		return light;
	}

	@JsonProperty("replica")
	public int getReplica() {
		return replica;
	}
	
	@JsonIgnore
	public void setTime(String time) {
		this.time = time;
	}
	
	@JsonIgnore
	public void setLight(int light) {
		this.light = light;
	}
	
	@JsonIgnore
	public String toString() {
		return "Time: " + time + "\n" +
				"Light: " + light + "\n";
	}
}

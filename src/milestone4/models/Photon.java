package milestone4.models;

import milestone2.chord.Key;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Photon {

	private JSONObject description ;
	private ArrayList<Integer> light;
	private ArrayList<String> time;
	private int ID;
	private Boolean active;

	
	public Photon() {
		active = false;
		ID = 0;
		description = new JSONObject();
		light = new ArrayList<Integer>();
		time = new ArrayList<String>();
	}

	@JsonProperty("time")
	public ArrayList<String> getTime() {
		return time;
	}
	
	@JsonProperty("light")
	public ArrayList<Integer> getLight() {
		return light;
	}

	@JsonProperty("id")
	public int getID(){return ID;}

	@JsonProperty("active")
	public Boolean getActive(){ return active;}

	@JsonProperty("description")
	public JSONObject getDescription(){
		try {
			return new JSONObject(description.toString());
		} catch (JSONException e) {
			System.out.println("No description available");
			return null;
		}
	}

	@JsonProperty("light_data")
	public JSONObject getData(){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("time", new JSONArray(time));
		result.put("light", new JSONArray(light));
		return new JSONObject(result);
	}

	/**
	 * Returns the last x values
	 * @param total
	 * @return
     */
	@JsonProperty("light_data")
	public JSONObject getData(int total){
		if(total > this.time.size()) total = time.size();

		ArrayList<String> time = new ArrayList<String>();
		ArrayList<Integer> light = new ArrayList<Integer>();
		System.out.println("Parsing " + total);
		for(int i = 0; i < total; i++ ){
			time.add(this.time.get(this.time.size() - total + i));
			light.add(this.light.get(this.light.size() - total + i));
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("time", new JSONArray(time));
		result.put("light", new JSONArray(light));
		return new JSONObject(result);
	}

	@JsonProperty("light_data_last")
	public JSONObject getLastData(){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("time", time.get(time.size()-1));
		result.put("light", light.get(light.size()-1));
		return new JSONObject(result);
	}


	@JsonIgnore
	public Boolean update(String json){
		//updates the description of the photon, which is the json from the spark
		try {
			description = new JSONObject(json);
			JSONObject coreInfo = description.getJSONObject("coreInfo");
			String deviceID = coreInfo.getString("deviceID");
			ID = Key.generate16BitsKey(deviceID);
			active = true;
			return true;
		} catch (JSONException e) {
			// e.printStackTrace();
			System.out.println("Error retrieving photon info");
			return false;
		}
	}

	@JsonIgnore
	public void addData(String time, int value){
		this.time.add(time);
		this.light.add(value);
	}

	@JsonIgnore
	public String toString() {
		return "Time: " + time + "\n" +
				"Light: " + light + "\n";
	}
	@JsonIgnore
	public void setActive(Boolean active){
		this.active = active;
		if(!active){
			//empty the photon
			//TODO

		}
	}
}

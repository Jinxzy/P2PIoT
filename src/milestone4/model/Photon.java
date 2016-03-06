package milestone4.model;


import milestone2.chord.Key;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.List;

public class Photon {
    private List<Integer> light;
    private JSONObject description ;
    private Boolean active;
    private int ID;

    public Photon(){
        description = new JSONObject();
        ID = 0;
        active = false;

    }


    public int getID(){return ID;}
    public Boolean active(){ return active;}
    public String toString(){return description.toString();}

    public JSONObject json(){
        try {
            return new JSONObject(description.toString());
        } catch (JSONException e) {
            System.out.println("No description available");
            return null;
        }
    }

    public Boolean update(String json){
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
}

package Treatment;

import java.net.Socket;
import org.json.JSONObject;

public class EventAdpater {

	private Socket client;
	private String instruction = "";
	private JSONObject jObj = null;
	private String sRET ="";
	
	public EventAdpater(JSONObject JObj, Socket client) {
		this.client = client;										// Récupération des valeurs pour cette instance de classe
		this.jObj = JObj;	
		instruction = (String) this.jObj.get("instruction");
	}

	public String ChooseEvent() {
		if (instruction.equals("connect")) {								// Choix du traitement a faire en fonction de la demande client
			sRET = new Connect(this.jObj, this.client).NewConnection();
		} else if (instruction.equals("list_all_members")) {
			sRET = new ListMember(this.jObj).GetList();
		} else if (instruction.equals("list_channels")) {
			sRET = new CanalList(this.jObj).GetCanalList();
		} else if (instruction.equals("list_channel_members")) {
			sRET = new CanalMember(this.jObj).GetCanalMmenber();
		} else if (instruction.equals("subscribe_channel")) {
			sRET = new CanalConnect(this.jObj, this.client).ConnectToChannel();
		} else if (instruction.equals("disconnect")) {
			sRET = new Disconnect(this.jObj).Disconnection();
		} else if (instruction.equals("send_message")) {
			sRET = new Messages(this.jObj).checkMessage();	
		} else {															// Si le client demande qui n'est pas définie
			this.jObj.put("code", 002);
			this.jObj.put("message", "Unknow instruction");
			sRET = this.jObj.toString();
		}
		return sRET;
	}
}

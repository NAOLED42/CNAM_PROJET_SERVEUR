package Treatment;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import Serv.ServMain;

public class EventAdpater {

	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	private String instruction = "";
	private JSONObject jObj = null;
	private String sRET ="";
	
	public EventAdpater(JSONObject JObj) {
		jObj = JObj;
		instruction = (String) jObj.get("instruction");
	}

	public String ChooseEvent() {
		if (instruction.equals("connect")) {
			sRET = new Connect(jObj).NewConnection();
		} else if (instruction.equals("list_all_members")) {
			sRET = new ListMember(jObj).GetList();
		} else if (instruction.equals("list_channels")) {
			sRET = new CanalList(jObj).GetCanalList();
		} else if (instruction.equals("list_canal_menbers")) {
			sRET = new CanalMember(jObj).GetCanalMmenber();
		} else if (instruction.equals("subscribe_channel")) {
			sRET = new CanalConnect(jObj).ConnectToChannel();
		} else if (instruction.equals("disconnect")) {
			sRET = new Disconnect(jObj).Disconnection();
		}
		LOG.info(sRET);
		return sRET;
	}
}

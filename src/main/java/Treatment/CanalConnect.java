package Treatment;

import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import Interface.Main;
import Model.dao.DAOException;
import Model.dao.DAOFactory;
import Model.dao.IDAOCanal;
import Model.dao.IDAOUser;
import Model.dao.bean.Canal;
import Model.dao.bean.User;

public class CanalConnect {
	
	private static final Logger LOG = Logger.getLogger(Main.class.getName());
	public static final List <Canal> CANAL_LIST = new LinkedList <> ();
	private String login = "";
	private String password = "";
	private String channel = "";
	private String target = "";
	private String instruction = "";
	private JSONObject jObj;
	private String sRET;

	public CanalConnect(JSONObject JObj) {
		this.jObj = JObj;
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
		instruction = (String) this.jObj.get("instruction");
		channel = (String) this.jObj.get("channel");
		target = (String) this.jObj.get("target_channel"); 
	}
	
	public String ConnectToChannel () {
		
		Connection c = null;
		User u = null;
		IDAOCanal canalDAO = null;
		IDAOUser userDAO = null;
		Canal cn1, cn2 = null;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date dt = new Date(System.currentTimeMillis());
		String date = formatter.format(dt);
		
		try {
			c = DAOFactory.getConnection();				
			userDAO = DAOFactory.getDAOUser(c);
			canalDAO = DAOFactory.getDAOCanal(c);
			
			u = userDAO.getUserFromLoginAndPassword(login, password);
			if (u.getEtatConnexion() == false) {
				JSONObject jObj = new JSONObject();
				jObj.put("code", "312");
				jObj.put("message", "Erreur de Connexion : User not connected");
				sRET = jObj.toString();
				return sRET;
			}
			
			cn1 = canalDAO.getCanalFromNom(channel);
			cn2 = canalDAO.getCanalFromNom(target);
			if (cn2.getEtat() == true) {
				
			}	
			
		} catch (DAOException e) {
			LOG.error("Error in taking the canal", e);
			if (e.getMessage() == "Canal not found, name wrong or unknow.") {
				Canal cn = null;
				cn.setNom(target);
				cn.setDateCreation(dt);
				cn.setEtat(true);
				try {
					canalDAO.create(cn);
					JSONObject jObj = new JSONObject();
					jObj.put("code", "200");
					jObj.put("message", "Unknow error");
					sRET = jObj.toString();
					return sRET;
				} catch (DAOException e1) {
					JSONObject jObj = new JSONObject();
					jObj.put("code", "000");
					jObj.put("message", "Unknow error");
					sRET = jObj.toString();
					return sRET;
				}
			}
		} finally {
			
		}
		JSONObject jObj = new JSONObject();
		jObj.put("code", "000");
		jObj.put("message", "Unknow error");
		sRET = jObj.toString();
		return sRET;
	}
}

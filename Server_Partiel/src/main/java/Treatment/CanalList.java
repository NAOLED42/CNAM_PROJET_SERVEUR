package Treatment;

import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import Model.dao.DAOException;
import Model.dao.DAOFactory;
import Model.dao.IDAOUser;
import Model.dao.bean.User;
import Serv.ServMain;

public class CanalList {
	
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	private static String ch = "";
	private String login = "";
	private String password = "";
	private JSONObject jObj;
	

	public CanalList(JSONObject JObj) {				// Récupération des valeurs pour cette instance de classe
		this.jObj = JObj;
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
	}
	
	public String GetCanalList() {
		
		Connection c = null;
		User u = null;
		IDAOUser userDAO = null;
		JSONObject jSObj = new JSONObject();
		HashMap <String, HashMap <String, Socket>> listUtiCanal = new HashMap <String, HashMap <String, Socket>> ();
		
		try {
			c = DAOFactory.getConnection();				
			userDAO = DAOFactory.getDAOUser(c); 
			
			u = userDAO.getUserFromLoginAndPassword(login, password);	
			if (u.getEtatConnexion() == false) {					// Vérification que l'utilisateur qui fait la demande est connecté
				jSObj.put("code", 312);
				jSObj.put("message", "User not Connected");
			}
			
			listUtiCanal = ServMain.UTI_CANAL;
			ch = "";
			listUtiCanal.forEach((k, v) -> ch = k + ";" + ch);		// Récuparation de la liste des différent canaux existant depuis la Map qui contient 
																	// Canal, Login et Socket
			String tLst[] = ch.split(";");							// Envoie de la liste sous forme de tableau
			jSObj.put("code", 120);	
			jSObj.put("all_channel", tLst);
			
		} catch (DAOException e) {
			LOG.error("Error loading canal", e);
			if (e.getMessage() == "Error during loading canal from the database") {
				jSObj.put("code", 000);
				jSObj.put("message", "Unknow error");
			}
		} finally {
			try {
				c.close();
			} catch (SQLException e) {								// Erreur inconnu renvoiée par SQL
				LOG.error("Error closing SQL stream", e);
				JSONObject jSObj2 = new JSONObject();
				jSObj2.put("code", 000);
				jSObj2.put("message", "Unknow error");
				return jSObj2.toString();
			}
		}
		return jSObj.toString();
	}
}

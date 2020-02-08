package Treatment;

import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import Model.dao.DAOException;
import Model.dao.DAOFactory;
import Model.dao.IDAOCanal;
import Model.dao.IDAOUser;
import Model.dao.bean.Canal;
import Model.dao.bean.User;
import Serv.ServMain;

public class CanalMember {
	
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	private static String utis = "";
	private String login = "";
	private String password = "";
	private String channel = "";
	private JSONObject jObj;

	public CanalMember(JSONObject JObj) {				// Récupération des valeurs pour cette instance de classe
		this.jObj = JObj;
		login = (String) this.jObj.get("login"); 
		password = (String) this.jObj.get("password");
		channel = (String) this.jObj.get("channel");
	}
	
	public String GetCanalMmenber () {
		
		Connection c = null;
		User u = null;
		Canal cn = null;
		IDAOCanal canalDAO = null;
		IDAOUser userDAO = null;
		JSONObject jSObj = new JSONObject();
		HashMap <String, HashMap <String, Socket>> listUtiCanal = new HashMap <String, HashMap <String, Socket>> ();
		HashMap <String, Socket> listUti = new HashMap <String, Socket> ();
		
		
		try {
			c = DAOFactory.getConnection();				
			userDAO = DAOFactory.getDAOUser(c);
			canalDAO = DAOFactory.getDAOCanal(c);
			
			u = userDAO.getUserFromLoginAndPassword(login, password);	// Vérification que l'utilisateur qui fait la demande est toujouts connecté
			if (u.getEtatConnexion() == false) {
				jSObj.put("code", 312);
				jSObj.put("message", "User not connected");
				return jSObj.toString();
			}
			
			cn = canalDAO.getCanalFromNom(this.jObj.getString("channel"));	// Vérification que le canal ciblé existe
			if (cn == null) {
				jSObj.put("code", 422);
				jSObj.put("message", "Nonexistent Canal");
				return jSObj.toString();
			}
			
			listUtiCanal = ServMain.UTI_CANAL;
			listUti = listUtiCanal.get(channel);
			utis = "";
			listUti.forEach((k,v) -> utis = k + ";" + utis);				// Récupération de chaque utilisateur présent sur le canal cible présent dans la Map 
			
			String sLst[] = utis.split(";");
			
			jSObj.put("code", 110);											// Envoie de la liste des utilisateurs sous forme de tableau
			jSObj.put("all_members", sLst);
			
		} catch (DAOException e) {
			LOG.error("Error fecthing user or canal", e);
			jSObj.put("code", 000);
			jSObj.put("message", "Unknow error");
		} finally {
			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {										// Erreur inconnu renvoiée par SQL
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

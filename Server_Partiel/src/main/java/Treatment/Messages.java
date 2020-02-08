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
import Model.dao.IDAOMessage;
import Model.dao.IDAOUser;
import Model.dao.bean.Canal;
import Model.dao.bean.Message;
import Model.dao.bean.User;
import Serv.ServMain;

public class Messages {
	
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	private String login = "";
	private String password = "";
	private String channel = "";
	private String message = "";
	private JSONObject jObj;

	public Messages(JSONObject jObj) {
		this.jObj = jObj;										//Récupération des valeurs du jSON pour cette instance de classe
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
		message = (String) this.jObj.get("message");
		channel = (String) this.jObj.get("channel");
	}
	
	public String checkMessage() {
		
		HashMap <String, HashMap <String, Socket>> listUtiCanal = new HashMap <String, HashMap <String, Socket>> ();
		Connection c = null;
		User u = new User();
		Canal cn = new Canal();;
		Message msg = new Message();;
		IDAOCanal canalDAO = null;
		IDAOUser userDAO = null;
		IDAOMessage msgDAO = null;
		JSONObject jSObj = new JSONObject();
		
		try {
			c = DAOFactory.getConnection();				
			userDAO = DAOFactory.getDAOUser(c);
			canalDAO = DAOFactory.getDAOCanal(c);
			msgDAO = DAOFactory.getDAOMessage(c);
			
			u = userDAO.getUserFromLoginAndPassword(login, password);	//Vérification que l'user qui fait la demande est bien connecté
			if (u.getEtatConnexion() == false) {
				jSObj.put("code", 312);
				jSObj.put("message", "User not Connected");
				return jSObj.toString();
			}
			
			if (message.equals("")) {									//Vérification que le message est différente de chaine vides
				jSObj.put("code", 431);
				jSObj.put("message", "Empty message");
				return jSObj.toString();
			} else if (message.length() > 500) {						// Vérification que le message ne fait plus de 500 caractères
				jSObj.put("code", 430);
				jSObj.put("message", "Message too long");
				return jSObj.toString();
			}
			cn = canalDAO.getCanalFromNom(channel);						// Récupération des données du canal depuis la base de donnée
			listUtiCanal = ServMain.UTI_CANAL;							// Attribution de ce données dans la Map qui contient tous les canaux, logins et socket
			if (cn.getEtat() == true) {									// Si le canal est up
				msg.setId_Canal(cn.getId());
				msg.setId_User(u.getId());
				msg.setMessage(message);
				
				if (listUtiCanal.containsKey(channel)) {
					msgDAO.create(msg);
					
					jSObj.put("code", 130);
					jSObj.put("user", login);
					jSObj.put("channel", channel);
					jSObj.put("message", message);
				} else {
					jSObj.put("code", 420);
					jSObj.put("message", "Wrong canal");
				}
			}
			return jSObj.toString();			
		} catch (DAOException e) {
			LOG.error("Error during message's treatement", e);
			if (e.getMessage().equals("Canal not found, name wrong or unknow.")) {	// On revoie une trame qui signal une erreur inconnu 
				jSObj.put("code", 000);												// entre le serveur et la BDD via la DAO
				jSObj.put("message", "Unknow error");
			}
			
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				JSONObject jSObj2 = new JSONObject();
				jSObj2.put("code", 000);												
				jSObj2.put("message", "Unknow error");
				return jSObj2.toString();
			}
		}
		return jSObj.toString();
	}
}

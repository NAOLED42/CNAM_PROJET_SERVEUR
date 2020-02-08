package Treatment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import Model.dao.DAOException;
import Model.dao.DAOFactory;
import Model.dao.IDAOUser;
import Model.dao.bean.User;
import Serv.ServMain;

public class ListMember {
	
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	private String login = "";						
	private String password = "";
	private JSONObject jObj;
	
	public ListMember(JSONObject jObj) {
		this.jObj = jObj;													//Récupération des valeurs du jSON pour cette instance de classe
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
	}
	
	public String GetList() {
		Connection c = null;
		IDAOUser userDAO = null;
		String ch = "";
		User u = null;
		JSONObject jSObj = new JSONObject();
		
		try {
			c = DAOFactory.getConnection();
			userDAO = DAOFactory.getDAOUser(c);
			List <User> ulst = new ArrayList<User>();

			u = userDAO.getUserFromLoginAndPassword(login, password);					
			if (u != null) {															
				if (u.getEtatConnexion() == false) {					//Vérification que l'user qui fait la demande est bien connecté 
					jSObj.put("code", 312);
					jSObj.put("message", "User not connected");
					return jSObj.toString();
				}
			}
			ulst =  userDAO.list();										// Récuparation de la liste de tous les Utilisateurs présent dans la BDD		
			if (ulst != null) {
				for (User user : ulst) {
					if (!(ch.contains(user.getLogin()))) {
						ch = user.getLogin() + ";" + ch ;				// Concaténation de chaque login 
					}
				}
				String tLst[] = ch.split(";");							
				jSObj.put("code", 110);
				jSObj.put("all_members", tLst);
			} else {
				jSObj.put("code", 000);
				jSObj.put("message", "Unknow error");
			}
		} catch (DAOException e) {										// Erreur inconnu renvoie par le DAO
			LOG.error("Error during taking List member", e);
			jSObj.put("code", 000);
			jSObj.put("message", "Unknow error");
		} finally {
			try {
				c.close();
			} catch (SQLException e) {									// Erreur inconnu renvoiée par SQL
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

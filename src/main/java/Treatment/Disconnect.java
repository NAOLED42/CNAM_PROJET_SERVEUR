package Treatment;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.JSONObject;
import org.json.JSONTokener;

import Model.dao.DAOException;
import Model.dao.DAOFactory;
import Model.dao.IDAOUser;
import Model.dao.bean.User;

public class Disconnect {
	
	private Connection c = null;
	private String instruction = "";
	private String login = "";
	private String password = "";
	private JSONObject jObj = null;
	private String sRET;

	public Disconnect(JSONObject jObj) {
		// TODO Auto-generated constructor stub
		this.jObj = jObj;
		instruction = (String) jObj.get("instruction");
		login = (String) jObj.get("login");
		password = (String) jObj.get("password");
	}
	
	public String Disconnection() {
		
		Connection c = null;
		User u = null;
		IDAOUser userDAO = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date dt = new Date(System.currentTimeMillis());
		String date = formatter.format(dt);
		
		try {
			c = DAOFactory.getConnection();
			userDAO = DAOFactory.getDAOUser(c);

			u = userDAO.getUserFromLoginAndPassword(login, password);
			
			if (u.getEtatConnexion() == false) {
				JSONObject jSObj = new JSONObject();
				jSObj.put("code", "312");
				jSObj.put("message", "Erreur de Connexion : User not connected");
				sRET = jSObj.toString();
				return sRET;
			}
			
			u.setLogin(login);
			u.setMDP(password);
			u.setEtatConnexion(false);
			u.setDateCreation("");
			u.setDerniereConnexion(date);
			
			userDAO.update(u);
			
			JSONObject jSObj = new JSONObject();
			jSObj.put("code", "200");
			jSObj.put("message", "Succes");
			sRET = jSObj.toString();
			return sRET;
							
		} catch (DAOException e) {
			if (e.getMessage() == "User not found.") {
				JSONObject jSObj = new JSONObject();
				jSObj.put("code", "311");
				jSObj.put("message", "Invalide Password");
				sRET = jSObj.toString();
				return sRET;
				
			}
			e.printStackTrace();
		} finally {
			
		}
		sRET = "";
		return sRET;
	}
}

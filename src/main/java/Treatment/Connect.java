package Treatment;

import Model.dao.*;
import Model.dao.bean.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import Interface.Main;

public class Connect {
	
	private static final Logger LOG = Logger.getLogger(Main.class.getName());
	private String login = "";
	private String password = "";
	private JSONObject jObj;

	public Connect(JSONObject JObj) {
		
		this.jObj = JObj;
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
	}
	
	public String NewConnection() {
		
		Connection c = null;
		User u = null;
		IDAOUser userDAO = null;	
		JSONObject jSObj = new JSONObject();
		
		try {
			c = DAOFactory.getConnection();
			userDAO = DAOFactory.getDAOUser(c);
			
			u = userDAO.getUserFromLogin(login);
			u = userDAO.getUserFromLoginAndPassword(login, password);
			
			if (u.getEtatConnexion() == true) {
				jSObj.put("code", 310);
				jSObj.put("message", "Erreur de Connexion : User in use");
				LOG.info("Utilisateur " + login + " = Tentative de connexion - Utilisateur déja connecté");
				
			}
			else {
			
			u.setLogin(login);
			u.setMDP(password);
			u.setEtatConnexion(true);
			
			LOG.info("Update USER");
			
			userDAO.update(u);
			
			LOG.info("Utilisateur" + login + " = Nouvel Connexion");
			
			System.out.println("Conection réussi !!!!!!!");
			
			jSObj.put("code", 200);
			jSObj.put("message", "Succes");
			LOG.info("Valeur return = " + jSObj.toString());
			}
			
		} catch (DAOException e) {
			
			LOG.info("ErrMsg " + e.getMessage());
			//LOG.error("Error during the connection", e);
			if (e.getMessage().equals("User not found.")) {
				LOG.info("Utilisateur" + login + " = Tentative de connexion - Mauvais mot de passe");
				jSObj.put("code", "311");
				jSObj.put("message", "Erreur de Connexion : Invalid Password");
				
			} else if (e.getMessage().equals("User doesn't exist.")) {
				try {
					u = new User();
					
					u.setLogin(login);
					u.setMDP(password);
					u.setEtatConnexion(true);

					LOG.info("Uti " + login);
					u = userDAO.create(u);
					LOG.info("YOUPI");
					
					jSObj.put("code", "200");
					jSObj.put("message", "Succes");
					
				} catch (DAOException e1) {
					LOG.info("Erreur unknow1");
					jSObj.put("code", "000");
					jSObj.put("message", "Unknow error");
				} 
			} else {
				jSObj.put("code", "000");
				jSObj.put("message", "Unknow error");
			}
			
		} finally {
			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return jSObj.toString();
	}
} 

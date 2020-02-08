package Treatment;

import Model.dao.*;
import Model.dao.bean.*;
import Serv.ServMain;

import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class Connect {
	
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	private  Socket client;
	private String login = "";
	private String password = "";
	private JSONObject jObj;

	public Connect(JSONObject JObj, Socket client) {				// Récupération des valeurs pour cette instance de classe
		this.client = client;
		this.jObj = JObj;
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
	}
	
	public String NewConnection() {
		
		Connection c = null;
		User u = null;
		Canal cn = null;
		IDAOUser userDAO = null;	
		IDAOCanal canalDAO = null;
		HashMap <String, HashMap <String, Socket>> listUtiCanal = new HashMap <String, HashMap <String, Socket>> ();
		HashMap <String, Socket> listUti = new HashMap <String, Socket> ();
		JSONObject jSObj = new JSONObject();
		
		try {
			c = DAOFactory.getConnection();
			userDAO = DAOFactory.getDAOUser(c);
			canalDAO = DAOFactory.getDAOCanal(c);
			
			u = userDAO.getUserFromLogin(login);						// On vérifie si le Login qui se connect existe déja ou non
			u = userDAO.getUserFromLoginAndPassword(login, password);	// Si oui alores on vérifie le password
			cn = canalDAO.getCanalFromNom("default");					// Permet de récupérer les infos du canal 'default' sur lequel on est connecté pas défaut
			
			if (u.getEtatConnexion() == true) {							// Permet de vérifier si le login existe déja ou non et s'il est connecté actuellement
				jSObj.put("code", 310);
				jSObj.put("message", "Login already in use");
			} else {
			
				u.setLogin(login);										// Met a jour les infos de l'utilisateur 
				u.setMDP(password);
				u.setEtatConnexion(true);
				
				userDAO.update(u);
					
				listUtiCanal = ServMain.UTI_CANAL;						// Met a jour la Map qui contient Canal, login et socket
				if (listUtiCanal.containsKey(cn.getNom())) {
					listUti = listUtiCanal.get(cn.getNom());
					listUti.put(login, this.client);
					listUtiCanal.put(cn.getNom(), listUti);
					ServMain.UTI_CANAL = listUtiCanal;
				} else {
					listUti.put(login, this.client);
					listUtiCanal.put(cn.getNom(), listUti);
					ServMain.UTI_CANAL = listUtiCanal;
				}
				jSObj.put("code", 200);									// Renvoie confirmation du succées du traitement des données
				jSObj.put("message", "Succes");
			}
		} catch (DAOException e) {
			LOG.error("Error fetching user", e);
			if (e.getMessage().equals("User not found.")) {				// Si le password fournit ne correspond a celui stockée and BDD
				jSObj.put("code", 311);
				jSObj.put("message", "Invalid Password");
				
			} else if (e.getMessage().equals("User doesn't exist.")) {	// Si l'utilisateur n'existe pas, on en crée un
				try {
					u = new User();
								
					u.setLogin(login);									// Création de l'utilisateur
					u.setMDP(password);
					u.setEtatConnexion(true);
					
					u = userDAO.create(u);
					cn = canalDAO.getCanalFromNom("default");
					
					if (listUtiCanal.containsKey(cn.getNom())) {		// Mise a jourde la map qui contient Canal, login et socket
						listUti = listUtiCanal.get(cn.getNom());
						listUti.put(login, this.client);
						listUtiCanal.put(cn.getNom(), listUti);
						ServMain.UTI_CANAL = listUtiCanal;
					} else {
						listUti.put(login, this.client);
						listUtiCanal.put(cn.getNom(), listUti);
						ServMain.UTI_CANAL = listUtiCanal;
					}
					
					jSObj.put("code", 200);
					jSObj.put("message", "Succes");
					
				} catch (DAOException e1) {								//Erreur inconnu renvoié par la DAO
					LOG.error("Error creating user");
					jSObj.put("code", 000);
					jSObj.put("message", "Unknow error");
				} 
			} else {
				jSObj.put("code", 000);									//Erreur inconnu renvoié par la DAO
				jSObj.put("message", "Unknow error");
			}
			
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

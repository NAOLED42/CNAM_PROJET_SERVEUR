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

public class CanalConnect {
	
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	private Socket client;
	private String login = "";
	private String password = "";
	private String channel = "";
	private String target = "";
	private JSONObject jObj;

	public CanalConnect(JSONObject JObj, Socket client) {				// Récupération des valeurs pour cette instance de classe
		this.client = client;
		this.jObj = JObj;
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
		channel = (String) this.jObj.get("channel");
		target = (String) this.jObj.get("target_channel"); 
	}
	
	public String ConnectToChannel () {
		
		Connection c = null;
		User u = null;
		IDAOCanal canalDAO = null;
		IDAOUser userDAO = null;
		Canal cn1, cn2 = null;
		JSONObject jSObj = new JSONObject();
		HashMap <String, HashMap <String, Socket>> listUtiCanal = new HashMap <String, HashMap <String, Socket>> ();
		HashMap <String, Socket> listUti = new HashMap <String, Socket> ();
		
		try {
			c = DAOFactory.getConnection();				
			userDAO = DAOFactory.getDAOUser(c);
			canalDAO = DAOFactory.getDAOCanal(c);
			
			u = userDAO.getUserFromLoginAndPassword(login, password);		// Vérification que l'utilisateur qui fait la demande est connecté
			if (u.getEtatConnexion() == false) {
				jSObj.put("code", 312);
				jSObj.put("message", "User not connected");
				return jSObj.toString();
			}			
			cn1 = canalDAO.getCanalFromNom(channel);						// On récupère le canal sur lequel l'utilisateur est actuellement connecté	
			cn2 = canalDAO.getCanalFromNom(target);							// On récupère le canal sur lequel l'utilisateur veut se connecter
			
			if (cn2.getEtat() == false) {									// Si le canal ciblé a existé mais est actuellement innocupé, le met comme existant
				cn2.setEtat(true);
				cn2.equals(canalDAO.update(cn2));		
			}
					
			listUtiCanal = ServMain.UTI_CANAL;								// Ont enlève le login de l'utilisateur de la liste de login associé au canala dans la Map
			listUti = listUtiCanal.get(cn1.getNom());						// qui contient canal, login et socket
			listUti.remove(login, this.client);			
			listUtiCanal.put(channel, listUti);
			
			if (listUtiCanal.containsKey(target)) {							// On rajoute le login de l'utilisateur a la liste de login associé au canal visée dans la
				listUti = listUtiCanal.get(target);							// Map qui contient canal, login et socket
				listUti.put(login, this.client);
				listUtiCanal.put(target, listUti);
			} else {
				listUti = new HashMap <String, Socket> ();					// S'il n'y a pas de liste existantes, on la crée
				listUti.put(login, this.client);
				listUtiCanal.put(target, listUti);
			}
			
			listUti = listUtiCanal.get(channel);											// Si le canal sur lequel l'utilisateur était est différent du canal 'default'
			if ((listUti.isEmpty() == true) && (!(channel.equals("default")))) {			// et qu'il n'y plus aucun utilisateur connecté a ce canal, on le passe en 
				cn1.setEtat(false);															// inactif et on le retire de la Map
				cn1 = canalDAO.update(cn1);
				listUtiCanal.remove(channel);
			}
			ServMain.UTI_CANAL = listUtiCanal;								// On met la map général a jour
			
			jSObj.put("code", 200);
			jSObj.put("message", "Success");
						
		} catch (DAOException e) {
			LOG.error("Error fetching canal", e);
			if ((e.getMessage() == "Canal not found, name wrong or unknow.") || (e.getMessage() == "Nom is missing")){
				if ((target.equals("null")) || (target.equals(""))) {				
					jSObj.put("code", 421);
					jSObj.put("message", "No Canal");						// Si le canal ciblé n'existe pas, on le crée
					return jSObj.toString();
				}
				Canal cn = new Canal();
				try {
					cn1 = canalDAO.getCanalFromNom(channel);
				} catch (DAOException e1) {
					jSObj.put("code", 000);
					jSObj.put("message", "Unknow error");
					return jSObj.toString();
				}
				cn.setNom(target);
				cn.setEtat(true);
				
				try {
					cn2 = canalDAO.create(cn);
					
					listUtiCanal = ServMain.UTI_CANAL;							// Ont enlève le login de l'utilisateur de la liste de login associé au canala dans la Map
					listUti = listUtiCanal.get(channel);						// qui contient canal, login et socket
					listUti.remove(login, this.client);			
					listUtiCanal.put(channel, listUti);
					
					if (listUtiCanal.containsKey(target)) {						// On rajoute le login de l'utilisateur a la liste de login associé au canal visée dans la
						listUti = listUtiCanal.get(target);						// Map qui contient canal, login et socket
						listUti.put(login, this.client);
						listUtiCanal.put(target, listUti);
					} else {
						listUti = new HashMap <String, Socket> ();				// S'il n'y a pas de liste existantes, on la crée
						listUti.put(login, this.client);
						listUtiCanal.put(target, listUti);
					}
					
					listUti = listUtiCanal.get(channel);						// Si le canal sur lequel l'utilisateur était est différent du canal 'default'
					if (listUti.isEmpty() && (!(channel.equals("default")))) {	// et qu'il n'y plus aucun utilisateur connecté a ce canal, on le passe en 
						cn1.setEtat(false);										// inactif et on le retire de la Map
						cn1 = canalDAO.update(cn1);
						listUtiCanal.remove(channel);
					}
					
					ServMain.UTI_CANAL = listUtiCanal;							// On met la map général a jour
					
					jSObj.put("code", 200);
					jSObj.put("message", "Inscription Réussi");					// On renvoie que l'inscription au canal c'est bien passé
				} catch (DAOException e1) {
					jSObj.put("code", 421);
					jSObj.put("message", "No Canal");
				}
			}
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				LOG.error("Error closing SQL stream", e);
				JSONObject jSObj2 = new JSONObject();
				jSObj2.put("code",  000);
				jSObj2.put("message", "Unknow error");
				return jSObj2.toString();
			}
		}
		return jSObj.toString();
	}	
}

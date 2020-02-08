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

public class Disconnect {
	
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	private Integer i;
	private String cn[] = new String [25];
	private String login = "";
	private String password = "";

	public Disconnect(JSONObject jObj) {
		login = (String) jObj.get("login");				// Récupération des valeurs pour cette instance de classe
		password = (String) jObj.get("password");
	}
	
	public String Disconnection() {
		
		Connection c = null;
		User u = null;
		IDAOUser userDAO = null;
		JSONObject jSObj = new JSONObject();
		HashMap <String, HashMap <String, Socket>> listUtiCanal = new HashMap <String, HashMap <String, Socket>> ();
		HashMap <String, Socket> listUti = new HashMap <String, Socket> ();
		
		try {
			c = DAOFactory.getConnection();
			userDAO = DAOFactory.getDAOUser(c);

			u = userDAO.getUserFromLoginAndPassword(login, password);
			if (u.getEtatConnexion() == false) {							// Vérification que l'utilsateur qui fait la demande est connecté
				jSObj.put("code", "312");
				jSObj.put("message", "User not connected");
				return jSObj.toString();
			}
			
			u.setLogin(login);									// Modificaition des valeurs de l'instance 'u' pour la classe USer
			u.setMDP(password);
			u.setEtatConnexion(false);
			u.setDateCreation("");
			userDAO.update(u);									// Mise a tour de ces valeurs sur la table `USERS` sur la BDD
			
			listUtiCanal = ServMain.UTI_CANAL;
			i = 0;
			listUtiCanal.forEach((k, v) -> cn[i++] = k);		// On parcours la map qui contient canal, login et socket afin de recupérer la liste des canaux
			for (i =0; i < 25; i++) {							
				if (cn[i] != null) {							
					listUti = listUtiCanal.get(cn[i]);			// Puis pour chaque canal, on vérifie si le login de l'utilisteur qui se déconnect est présent
					if (listUti.containsKey(login)) {			// Si oui, alors on supprime son login et son socket associé au canal en question
						listUti.remove(login);
						listUtiCanal.put(cn[i], listUti);
						ServMain.UTI_CANAL = listUtiCanal;		// On actualise la Map
					}
				}
			}
			jSObj.put("code", "200");							// Envoie de la trame qui confirme le bon traitement des données
			jSObj.put("message", "Sucess");
			return jSObj.toString();
			
		} catch (DAOException e) {
			LOG.error("Error during fetching user", e);
			if (e.getMessage() == "User not found.") {			
				JSONObject jSObj2 = new JSONObject();
				jSObj2.put("code", "311");
				jSObj2.put("message", "Invalide Password");
				return jSObj2.toString();			
			}
		} finally {
			try {
				c.close();	
			} catch (SQLException e) {							// Erreur inconnu renvoiée par SQL
				LOG.error("Error during closing SQL stream", e);
				JSONObject jSObj2 = new JSONObject();	
				jSObj2.put("code", 000);
				jSObj2.put("message", "Unknow error");
			}	
		}
		return jSObj.toString();
	}
}

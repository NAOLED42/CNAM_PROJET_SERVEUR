package Treatment;

import java.sql.Connection;
import java.sql.SQLException;
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

public class CanalMember {
	
	private static final Logger LOG = Logger.getLogger(Main.class.getName());
	private String login = "";
	private String password = "";
	private JSONObject jObj;
	private String sRET;

	public CanalMember(JSONObject JObj) {
		this.jObj = JObj;
		login = (String) this.jObj.get("login"); 
		password = (String) this.jObj.get("password");
	}
	
	public String GetCanalMmenber () {
		
		Connection c = null;
		User u = null;
		Canal cn = null;
		IDAOCanal canalDAO = null;
		IDAOUser userDAO = null;
		
		
		try {
			c = DAOFactory.getConnection();				
			userDAO = DAOFactory.getDAOUser(c);
			canalDAO = DAOFactory.getDAOCanal(c);
			
			if (u.getEtatConnexion() == false) {
				JSONObject jSObj = new JSONObject();
				jSObj.put("code", "312");
				jSObj.put("message", "Erreur de Connexion : User not connected");
				sRET = jSObj.toString();
				return sRET;
			}
			
			
		} catch (DAOException e) {
			LOG.error("Error in taking the canal's member list", e);
		} finally {
			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		JSONObject jSObj = new JSONObject();
		jSObj.put("code", "0");
		jSObj.put("message", "Unknow error");
		sRET = jSObj.toString();
		return sRET;
	}
}

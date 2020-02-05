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

public class ListMember {
	
	private static final Logger LOG = Logger.getLogger(Main.class.getName());
	private String login = "";
	private String password = "";
	private JSONObject jObj;
	private String sRET;
	
	public ListMember(JSONObject jObj) {
		// TODO Auto-generated constructor stub
		this.jObj = jObj;
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
	}
	
	public String GetList() {
		
		LOG.info("LstMb 1");
		Connection c = null;
		IDAOUser userDAO = null;
		String ch = "";
		User u = null;
		
		try {
			LOG.info("LstMb 2");
			c = DAOFactory.getConnection();
			userDAO = DAOFactory.getDAOUser(c);
			List <User> ulst = null;
			
			LOG.info("LstMb 3");
			u = userDAO.getUserFromLogin(login);
			if (u != null) {
				LOG.info("LstMb 4");
				if (u.getEtatConnexion() == false) {
					LOG.info("LstMb 1");
					JSONObject jSObj = new JSONObject();
					jSObj.put("code", "312");
					jSObj.put("message", "Erreur de Connexion : User not connected");
					sRET = jSObj.toString();
					return sRET;
				}
			}
			
			LOG.info("LstMb 5");
			ulst =  userDAO.list();
			if (ulst != null) {
				
				LOG.info("LstMb 6");
				for (User user : ulst) {
					ch = ch + ";" + user.getLogin();
				}
				
				LOG.info("LstMb 7");
				JSONObject jSObj = new JSONObject();
				jSObj.put("code", "110");
				jSObj.put("message", ch);
				sRET = jSObj.toString();
				return sRET;
			} else {
				LOG.info("LstMb 8");
				JSONObject jSObj = new JSONObject();
				jSObj.put("code", "000");
				jSObj.put("message", "Unknow error");
				sRET = jSObj.toString();
				return sRET;
			}
			 
		} catch (DAOException e) {
			LOG.error("Error in taking the member's list", e);
			JSONObject jSObj = new JSONObject();
			jSObj.put("code", "000");
			jSObj.put("message", "Unknow error");
			sRET = jSObj.toString();
			return sRET;
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOG.error("Error in taking the menber's list", e);
				return null;
			}
		}
	}
}

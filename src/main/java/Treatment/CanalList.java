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

public class CanalList {
	
	private static final Logger LOG = Logger.getLogger(Main.class.getName());
	private String login = "";
	private String password = "";
	private JSONObject jObj;
	private String sRET;

	public CanalList(JSONObject JObj) {
		this.jObj = JObj;
		login = (String) this.jObj.get("login");
		password = (String) this.jObj.get("password");
	}
	
	public String GetCanalList() {
		
		Connection c = null;
		User u = null;
		String ch = "";
		IDAOCanal canalDAO = null;
		IDAOUser userDAO = null;
		List <Canal> cnl = null;
		JSONObject jSObj = new JSONObject();
		
		try {
			c = DAOFactory.getConnection();				
			userDAO = DAOFactory.getDAOUser(c); 
			canalDAO = DAOFactory.getDAOCanal(c);
			
			u = userDAO.getUserFromLogin(login);
			
			if (u.getEtatConnexion() == false) {
				jSObj.put("code", "312");
				jSObj.put("message", "Erreur de Connexion : User not connected");
			}
			

			cnl =  canalDAO.list();
			LOG.info(cnl);
			if (cnl != null) {
				for (Canal canal : cnl) {
					ch = ch + ";" + canal.getNom();
				}
				
				jSObj.put("code", "110");
				jSObj.put("message", ch);

			} else {
				Canal cl = new Canal();
				
				cl.setNom("default");
				cl.setEtat(true);
				
				cl = canalDAO.create(cl);
			}
			
		} catch (DAOException e) {
			if (e.getMessage() == "Error during loading canal from the database") {
				LOG.info(e.getMessage());
				jSObj.put("code", "000");
				jSObj.put("message", "Unknow error");
			}
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jSObj.toString();
	}
}

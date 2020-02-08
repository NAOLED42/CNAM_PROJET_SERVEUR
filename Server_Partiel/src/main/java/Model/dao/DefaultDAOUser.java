package Model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import Interface.Main;
import Model.dao.bean.User;

public class DefaultDAOUser extends AbstractDAO implements IDAOUser {
	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	protected DefaultDAOUser(Connection connect) {
		super(connect);
	}

	@Override
	public User find(Object id) throws DAOException {
		final String sql = "SELECT * FROM `USER` WHERE `Id`= ? ";
		if (!(id instanceof Integer)) {
			throw new DAOException("The ID isn't an Integer.");
		}

		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			st.setInt(1, (int) id);
			r = st.executeQuery();

			if (r.next()) {
				User u = new User();
				u.setId(r.getInt("Id"));
				u.setLogin(r.getString("Login"));
				u.setMDP(r.getString("Password"));
				u.setDateCreation(r.getString("DateCreation"));
				u.setDerniereConnexion(r.getString("DerniereConnexion"));
				u.setEtatConnexion(r.getBoolean("EtatConnexion"));

				return u;
			}

			throw new DAOException("User not found.");

		} catch (SQLException e) {
			throw new DAOException("Error during loading user from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}

	}

	@Override
	public User create(User obj) throws DAOException {		
		final String sql = "INSERT INTO `USER`(Login, Password, DateCreation, DerniereConnexion, EtatConnexion) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)";

		PreparedStatement st = null;
		ResultSet rs = null;
		int r;

		try {
			st = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getLogin());
			st.setString(2, obj.getMDP());
			if (obj.getEtatConnexion() == true) {
				st.setInt(3, 1);
			} else {
				st.setInt(3, 0);
			}
			
			r = st.executeUpdate();
			rs = st.getGeneratedKeys();
			if(r > 0 && rs.next()) {
				obj.setId(rs.getInt(1));
				return obj;
			}			
			throw new DAOException("Error during user insert in the database");
		} catch (SQLException e) {
			throw new DAOException("Error during creating user in the database", e);
		} finally {
			DAOUtils.close(st);
		}
	}

	@Override
	public User update(User obj) throws DAOException {
		final String sql = "UPDATE `USER` "
				+ "SET `Login` = ? ,"
				+ "`Password` = ? ,"
				+ "`DerniereConnexion` = CURRENT_TIMESTAMP ,"
				+ "`EtatConnexion` = ? "
				+ "WHERE Id = ?";

		PreparedStatement st = null;
		int r;

		try {
			st = connect.prepareStatement(sql);
			st.setString(1, obj.getLogin());
			st.setString(2, obj.getMDP());
			st.setBoolean(3, obj.getEtatConnexion());
			st.setInt(4, obj.getId());
			r = st.executeUpdate();
			
			if(r > 0) {
				return obj;
			}
			
			throw new DAOException("Error during user insert in the database");			
		} catch (SQLException e) {
			LOG.info(e);
			throw new DAOException("Error during creating user in the database", e);
		} finally {
			DAOUtils.close(st);
		}	
	}

	@Override
	public void delete(User obj) throws DAOException {
		final String sql = "DELETE FROM `USER` WHERE `Id`= ? ";

		PreparedStatement st = null;
		int r;

		try {
			st = connect.prepareStatement(sql);
			st.setInt(1, obj.getId());
			r = st.executeUpdate();

			if (r == 0) {
				throw new DAOException("User not found.");
			}

		} catch (SQLException e) {
			throw new DAOException("Error during deleting user from the database", e);
		} finally {
			DAOUtils.close(st);
		}
	}

	@Override
	public List<User> list() throws DAOException {
		final String sql = "SELECT * FROM `USER`";
		final List<User> usersList = new LinkedList<>();
		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			r = st.executeQuery();

			while (r.next()) {
				final User u = new User();
				u.setId(r.getInt("Id"));
				u.setLogin(r.getString("Login"));
				u.setMDP(r.getString("Password"));
				u.setDateCreation(r.getString("DateCreation"));
				u.setDerniereConnexion(r.getString("DerniereConnexion"));
				u.setEtatConnexion(r.getBoolean("EtatConnexion"));

				usersList.add(u);
			}			
			return usersList;
			
		} catch (SQLException e) {
			throw new DAOException("Error during loading user from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}
	
	

	@Override
	public User getUserFromLoginAndPassword(String login, String password) throws DAOException {
		final String sql = "SELECT * FROM `USER` WHERE `Login` = ? AND `Password` = ? ";
		if (password.isEmpty() || login.isEmpty()){
			throw new DAOException("Login or password are missing.");
		}

		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			st.setString(1, login);
			st.setString(2, password);
			r = st.executeQuery();

			if (r.next()) {
				User u = new User();
				u.setId(r.getInt("Id"));
				u.setLogin(r.getString("Login"));
				u.setMDP(r.getString("Password"));
				u.setDateCreation(r.getString("DateCreation"));
				u.setDerniereConnexion(r.getString("DerniereConnexion"));
				u.setEtatConnexion(r.getBoolean("EtatConnexion"));

				return u;
			}

			throw new DAOException("User not found.");
		} catch (SQLException e) {
			throw new DAOException("Error during loading user (Lg/MDP) from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}

	@Override
	public User getUserFromLogin(String login) throws DAOException {
		final String sql = "SELECT Id, Login, Password, DateCreation, DerniereConnexion, EtatConnexion FROM `USER` WHERE `Login` = ?";
		if (login.isEmpty()){
			throw new DAOException("Login is missing.");
		}

		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			st.setString(1, login);
			r = st.executeQuery();

			if (r.next()) {
				User u = new User();
				u.setId(r.getInt("Id")); 
				u.setLogin(r.getString("Login"));
				u.setMDP(r.getString("Password"));
				u.setDateCreation(r.getString("DateCreation"));
				u.setDerniereConnexion(r.getString("DerniereConnexion"));
				u.setEtatConnexion(r.getBoolean("EtatConnexion"));

				return u;
			} 
			
			throw new DAOException("User doesn't exist.");
		} catch (SQLException e) {
			throw new DAOException("Error during loading user (Lg) from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}
}


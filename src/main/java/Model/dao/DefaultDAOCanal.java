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
import Model.dao.bean.Canal;

public class DefaultDAOCanal extends AbstractDAO implements IDAOCanal{
	
	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	protected DefaultDAOCanal(Connection connect) {
		super(connect);
	}

	@Override
	public Canal find(Object id) throws DAOException {
		final String sql = "SELECT * FROM `CANAL` WHERE `Id`= ? ";
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
				Canal u = new Canal();
				u.setId(r.getInt("Id"));
				u.setNom(r.getString("Nom"));
				u.setDateCreation(r.getDate("DateCreation"));
				return u;
			}

			throw new DAOException("Canal not found.");

		} catch (SQLException e) {
			throw new DAOException("Error during loading canal from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}

	}

	@Override
	public Canal create(Canal obj) throws DAOException {
		final String sql = "INSERT INTO `CANAL` VALUES (NULL, ?, CURRENT_TIMESTAMP, ?)";

		PreparedStatement st = null;
		ResultSet rs = null;
		int r;

		try {
			st = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getNom());
			st.setBoolean(2, obj.getEtat());
			r = st.executeUpdate();
			
			rs = st.getGeneratedKeys();
									
			if(r > 0 && rs.next()) {
				obj.setId(rs.getInt(1));
				return obj;
			}			
			throw new DAOException("Error during canal insert in the database");
		} catch (SQLException e) {
			throw new DAOException("Error during creating canal in the database", e);
		} finally {
			DAOUtils.close(st);
		}
	}

	@Override
	public Canal update(Canal obj) throws DAOException {
		final String sql = "UPDATE `CANAL` "
				+ "SET `Nom` = ? ,"
				+ "`DateCreation` = ? ,"
				+ "`Etat` = ? "
				+ "WHERE Id = ?";

		PreparedStatement st = null;
		int r;

		try {
			st = connect.prepareStatement(sql);
			st.setString(1, obj.getNom());
			st.setDate(2, obj.getDateCreation());
			st.setBoolean(3, obj.getEtat());
			r = st.executeUpdate();
			
			if(r > 0) {
				return obj;
			}
			
			throw new DAOException("Error during canal insert in the database");			
		} catch (SQLException e) {
			throw new DAOException("Error during creating canal in the database", e);
		} finally {
			DAOUtils.close(st);
		}	
	}

	@Override
	public void delete(Canal obj) throws DAOException {
		final String sql = "DELETE FROM `CANAL` WHERE `Id`= ? ";

		PreparedStatement st = null;
		int r;

		try {
			st = connect.prepareStatement(sql);
			st.setInt(1, obj.getId());
			r = st.executeUpdate();

			if (r == 0) {
				throw new DAOException("Canal not found.");
			}

		} catch (SQLException e) {
			throw new DAOException("Error during deleting canal from the database", e);
		} finally {
			DAOUtils.close(st);
		}
	}

	@Override
	public List<Canal> list() throws DAOException {
		final String sql = "SELECT * FROM `CANAL`";
		final List<Canal> usersList = new LinkedList<>();
		PreparedStatement st = null;
		ResultSet r = null;

		try {
			LOG.info("Tt1");
			st = connect.prepareStatement(sql);
			LOG.info("Tt2");
			r = st.executeQuery();
			LOG.info("Tt3");

			while (r.next()) {
				final Canal u = new Canal();
				u.setId(r.getInt("Id"));
				u.setNom(r.getString("Nom"));
				u.setDateCreation(r.getDate("DateCreation"));


				usersList.add(u);
			}			
			return usersList;
			
		} catch (SQLException e) {
			throw new DAOException("Error during loading canal from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}

	@Override
	public Canal getCanalFromNomEtat(String nom, Boolean etat) throws DAOException {
		final String sql = "SELECT * FROM `CANAL` WHERE `Nom` = ? AND `Etat` = ? ";
		if (nom.isEmpty() || etat == false){
			throw new DAOException("Nom is missing or etat is disconnect.");
		}
		
		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			st.setString(1, nom);
			st.setBoolean(2, etat);
			r = st.executeQuery();

			if (r.next()) {
				Canal u = new Canal();
				u.setId(r.getInt("Id"));
				u.setNom(r.getString("Nom"));
				u.setDateCreation(r.getDate("DateCreation"));
				u.setEtat(r.getBoolean("Etat"));

				return u;
			}

			throw new DAOException("Canal not found.");
		} catch (SQLException e) {
			throw new DAOException("Error during loading canal from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}
	
	@Override
	public Canal getCanalFromNom(String nom) throws DAOException {
		final String sql = "SELECT * FROM `Canal` WHERE `Nom` = ? ";
		if (nom.isEmpty()){
			throw new DAOException("Nom is missing.");
		}
		
		PreparedStatement st = null;
		ResultSet r = null;

		try {
			LOG.info("Test1");
			st = connect.prepareStatement(sql);
			LOG.info("Test2");
			st.setString(1, nom);
			LOG.info("Test3");
			r = st.executeQuery();
			LOG.info("Test4");
			
			if (r.next()) {
				Canal u = new Canal();
				u.setId(r.getInt("Id"));
				u.setNom(r.getString("Nom"));
				u.setDateCreation(r.getDate("DateCreation"));
				u.setEtat(r.getBoolean("Etat"));

				return u;
			}

			throw new DAOException("Canal not found, name wrong or unknow.");
		} catch (SQLException e) {
			throw new DAOException("Error during loading canal from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}
}

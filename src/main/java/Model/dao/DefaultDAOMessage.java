package Model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import Model.dao.bean.Message;

public class DefaultDAOMessage extends AbstractDAO implements IDAOMessage {

	protected DefaultDAOMessage(Connection connect) {
		super(connect);
	}

	@Override
	public Message find(Object id) throws DAOException {
		final String sql = "SELECT * FROM `MESSAGE` WHERE `Id`= ? ";
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
				Message u = new Message();
				u.setId(r.getInt("Id"));
				u.setId_User(r.getInt("IdUser"));
				u.setId_Canal(r.getInt("IdCanal"));
				u.setMessage(r.getString("Message"));
				u.setDateEnvoie(r.getDate("DateEnvoie"));

				return u;
			}

			throw new DAOException("Message not found.");

		} catch (SQLException e) {
			throw new DAOException("Error during loading message from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}

	}

	@Override
	public Message create(Message obj) throws DAOException {
		final String sql = "INSERT INTO `MESSAGE` VALUES (NULL, ?, ?, ?, ?)";

		PreparedStatement st = null;
		ResultSet rs = null;
		int r;

		try {
			st = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, obj.getId_User());
			st.setInt(2, obj.getId_Canal());
			st.setString(3, obj.getMessage());
			st.setDate(4, obj.getDateEnvoie());
			r = st.executeUpdate();

			rs = st.getGeneratedKeys();

			if (r > 0 && rs.next()) {
				obj.setId(rs.getInt(1));
				return obj;
			}
			throw new DAOException("Error during message insert in the database");
		} catch (SQLException e) {
			throw new DAOException("Error during creating message in the database", e);
		} finally {
			DAOUtils.close(st);
		}
	}

	@Override
	public Message update(Message obj) throws DAOException {
		final String sql = "UPDATE `MESSAGE` " + "SET `IdUser` = ? ," + "`IdCanal` = ? ," + "`Message` = ? ,"
				+ "`DateEnvoie` = ? " + "WHERE Id = ?";

		PreparedStatement st = null;
		int r;

		try {
			st = connect.prepareStatement(sql);
			st.setInt(1, obj.getId_User());
			st.setInt(2, obj.getId_Canal());
			st.setString(3, obj.getMessage());
			st.setDate(4, obj.getDateEnvoie());
			r = st.executeUpdate();

			if (r > 0) {
				return obj;
			}

			throw new DAOException("Error during message insert in the database");
		} catch (SQLException e) {
			throw new DAOException("Error during creating message in the database", e);
		} finally {
			DAOUtils.close(st);
		}
	}

	@Override
	public void delete(Message obj) throws DAOException {
		final String sql = "DELETE FROM `MESSAGE` WHERE `Id`= ? ";

		PreparedStatement st = null;
		int r;

		try {
			st = connect.prepareStatement(sql);
			st.setInt(1, obj.getId());
			r = st.executeUpdate();

			if (r == 0) {
				throw new DAOException("Message not found.");
			}

		} catch (SQLException e) {
			throw new DAOException("Error during deleting message from the database", e);
		} finally {
			DAOUtils.close(st);
		}
	}

	@Override
	public List<Message> list() throws DAOException {
		final String sql = "SELECT * FROM `MESSAGE`";
		final List<Message> usersList = new LinkedList<>();
		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			r = st.executeQuery();

			while (r.next()) {
				final Message u = new Message();
				u.setId(r.getInt("Id"));
				u.setId_User(r.getInt("IdUser"));
				u.setId_Canal(r.getInt("IdCanal"));
				u.setMessage(r.getString("Message"));
				u.setDateEnvoie(r.getDate("DateEnvoie"));

				usersList.add(u);
			}
			return usersList;

		} catch (SQLException e) {
			throw new DAOException("Error during loading message from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}

	@Override
	public Message getMessageByUserAndCanal(int id_User, int id_Canal) throws DAOException {
		final String sql = "SELECT * FROM `MESSAGE` WHERE `IdUser` = ? AND `IdCanal` = ? ";

		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			st.setInt(1, id_User);
			st.setInt(2, id_Canal);
			r = st.executeQuery();

			if (r.next()) {
				Message u = new Message();
				u.setId(r.getInt("Id"));
				u.setId_User(r.getInt("IdUser"));
				u.setId_Canal(r.getInt("IdCanal"));
				u.setMessage(r.getString("Message"));
				u.setDateEnvoie(r.getDate("DateEnvoie"));

				return u;
			}

			throw new DAOException("Message not found.");
		} catch (SQLException e) {
			throw new DAOException("Error during loading message from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}

	public List<Message> getMessageByCanal(int id_Canal) throws DAOException {
		final String sql = "SELECT * FROM `MESSAGE` WHERE `IdCanal` = ? ";

		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			st.setInt(1, id_Canal);
			r = st.executeQuery();
			List<Message> msg = new LinkedList<>();

			if (r.next()) {
				Message u = new Message();
				u.setId(r.getInt("Id"));
				u.setId_User(r.getInt("IdUser"));
				u.setId_Canal(r.getInt("IdCanal"));
				u.setMessage(r.getString("Message"));
				u.setDateEnvoie(r.getDate("DateEnvoie"));

				msg.add(u);
			}

			return msg;

		} catch (SQLException e) {
			throw new DAOException("Error during loading message from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}

	public List<Message> getMessageByCanalNom(String nom_canal) throws DAOException {
		final String sql = "SELECT MESSAGE.message FROM CANAL, MESSAGE WHERE CANAL.nom = ? LIMIT 5";

		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			st.setString(1, nom_canal);
			r = st.executeQuery();
			List<Message> msg = new LinkedList<>();

			while (r.next()) {
				Message u = new Message();
				u.setMessage(r.getString("Message"));

				msg.add(u);
			}

			return msg;

		} catch (SQLException e) {
			throw new DAOException("Error during loading message from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}

	public List<Message> getMessageByUser(int id_User) throws DAOException {
		final String sql = "SELECT * FROM `MESSAGE` WHERE `IdUser` = ? LIMIT 5";

		PreparedStatement st = null;
		ResultSet r = null;

		try {
			st = connect.prepareStatement(sql);
			st.setInt(1, id_User);
			r = st.executeQuery();
			List<Message> msg = new LinkedList<>();

			while(r.next()) {
				Message u = new Message();
				u.setId(r.getInt("Id"));
				u.setId_User(r.getInt("IdUser"));
				u.setId_Canal(r.getInt("IdCanal"));
				u.setMessage(r.getString("Message"));
				u.setDateEnvoie(r.getDate("DateEnvoie"));

				msg.add(u);
			}

			return msg;

		} catch (SQLException e) {
			throw new DAOException("Error during loading message from the database", e);
		} finally {
			DAOUtils.close(r, st);
		}
	}
}

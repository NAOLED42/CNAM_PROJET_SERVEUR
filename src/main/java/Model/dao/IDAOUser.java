package Model.dao;

import Model.dao.bean.User;

public interface IDAOUser extends IDAO<User> {
	public User getUserFromLoginAndPassword(String login, String password) throws DAOException;
	
	public User getUserFromLogin (String login) throws DAOException;
}

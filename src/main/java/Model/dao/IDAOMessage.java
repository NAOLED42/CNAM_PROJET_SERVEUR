package Model.dao;

import Model.dao.bean.Message;

public interface IDAOMessage extends IDAO<Message> {
	public Message getMessageByUserAndCanal(int id_User, int id_Canal) throws DAOException;
}

package Model.dao;

import Model.dao.bean.Canal;

public interface IDAOCanal extends IDAO<Canal> {
	public Canal getCanalFromNomEtat(String nom, Boolean etat) throws DAOException;
	
	public Canal getCanalFromNom(String nom) throws DAOException;

}

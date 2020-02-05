package Model.dao;

import java.sql.Connection;

public class AbstractDAO {
	protected Connection connect;

	protected AbstractDAO(Connection connect) {
		this.connect = connect;
	}
}

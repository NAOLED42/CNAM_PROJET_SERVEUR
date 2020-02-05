package Model.dao.bean;

import java.sql.Date;

public class Message {
	private int Id;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}

	//Correspond � l'ID de l'utilisateur qui a envoy� le message
	private int Id_User;
	public int getId_User() {
		return Id_User;
	}
	public void setId_User(int id_User) {
		Id_User = id_User;
	}

	//Correspond � l'ID du canal sur lequel le message a �t� envoy�
	private int Id_Canal;
	public int getId_Canal() {
		return Id_Canal;
	}
	public void setId_Canal(int id_Canal) {
		Id_Canal = id_Canal;
	}

	private String Message;
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}

	private Date DateEnvoie;
	public Date getDateEnvoie() {
		return DateEnvoie;
	}
	public void setDateEnvoie(Date dateEnvoie) {
		DateEnvoie = dateEnvoie;
	}
	
}

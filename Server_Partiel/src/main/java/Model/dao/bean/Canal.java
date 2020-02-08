package Model.dao.bean;

import java.sql.Date;
import java.util.List;

public class Canal {
	private int Id;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}

	private String Nom;
	public String getNom() {
		return Nom;
	}
	public void setNom(String nom) {
		Nom = nom;
	}

	private Date DateCreation;
	public Date getDateCreation() {
		return DateCreation;
	}
	public void setDateCreation(Date dateCreation) {
		DateCreation = dateCreation;
	}

	//Indique l'�tat du canal
	private boolean Etat;
	public boolean getEtat() {
		return Etat;
	}
	public void setEtat(boolean etat) {
		Etat = etat;
	}
	
	private List <User> ListUser;
	public List <User> getListUser() {
		return ListUser;
	}
	public void setListUser(List<User> lu) {
		ListUser = lu;
	}
}

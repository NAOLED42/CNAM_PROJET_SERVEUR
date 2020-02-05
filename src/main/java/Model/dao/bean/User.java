package Model.dao.bean;

public class User {
	private int Id;
	public int getId() {
		return Id;
	} 
	public void setId(int id) {
		Id = id;
	}
	
	private String Login;
	public String getLogin() {
		return Login;
	}
	public void setLogin(String login) {
		Login = login;
	}
	
	private String MDP;
	public String getMDP() {
		return MDP;
	}
	public void setMDP(String mDP) {
		MDP = mDP;
	}
	
	private String DateCreation;
	public String getDateCreation() {
		return DateCreation;
	}
	public void setDateCreation(String dateCreation) {
		DateCreation = dateCreation;
	}
	
	private String DerniereConnexion;
	public String getDerniereConnexion() {
		return DerniereConnexion;
	}
	public void setDerniereConnexion(String date) {
		DerniereConnexion = date;
	}
	
	private boolean EtatConnexion;
	public boolean getEtatConnexion() {
		return EtatConnexion;
	}
	public void setEtatConnexion(boolean etatConnexion) {
		EtatConnexion = etatConnexion;
	}
}

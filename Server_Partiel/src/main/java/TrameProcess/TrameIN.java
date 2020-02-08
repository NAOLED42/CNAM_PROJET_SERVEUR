package TrameProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Model.dao.DAOException;
import Model.dao.DAOFactory;
import Model.dao.IDAOUser;
import Model.dao.bean.Canal;
import Model.dao.bean.User;
import Serv.ServMain;
import Treatment.*;

public class TrameIN implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(TrameIN.class.getName());
	private User u = new User();
	private IDAOUser userDAO;
	private Connection c;
	protected Socket client;
	protected Canal canal;
	private String login;
	
	public JSONObject jObj = new JSONObject();

	public TrameIN(Socket client) {
		this.client = client;
		this.canal = null;
		
	}

	@Override
	public void run() {
		
		HashMap <String, HashMap <String, Socket>> listUtiCanal = new HashMap <String, HashMap <String, Socket>> ();
		HashMap <String, Socket> listUti = new HashMap <String, Socket> ();
		InputStream in = null;
		InputStreamReader isr = null; 
		BufferedReader br = null;
				
		String sRET = "";
		
		try {
			in = client.getInputStream();
			isr = new InputStreamReader(in, "UTF-8");
			br = new BufferedReader(isr);

			// Read the first line of the network stream
			String line = br.readLine();
			while (line != null) {
				final String ip = client.getInetAddress().getHostAddress();
				LOG.info(ip + " : " + line);

				// Read the next line present on the network stream.
				
				JSONObject jSObj = new JSONObject(new JSONTokener(line));
				
				if (jSObj != null) {
					
					EventAdpater EA = new EventAdpater(jSObj, this.client);						// Récupération de la trame a envoyée
					sRET = EA.ChooseEvent();
					this.jObj = new JSONObject(new JSONTokener(sRET));							// Traduction de la chaine de caractère au format JSON
					this.login = jSObj.getString("login");
					
					c = DAOFactory.getConnection();
					userDAO = DAOFactory.getDAOUser(c);
					u = userDAO.getUserFromLogin(this.login);
				
					if (jSObj.get("instruction").equals("disconnect")) {						// Si le client souhaiter se deconnecter, on ferme son socket
						sendTrame (this.client, this.jObj);
						this.client.close();
						this.client.shutdownInput();
					} else {
						if (((Integer)this.jObj.get("code") < 300) && ((Integer)this.jObj.get("code") != 000) && ((Integer)this.jObj.get("code") != 200)) {
							JSONObject obj = new JSONObject();									// Si le message renvoyé n'est pas une erreur ou une confirmation
							obj.put("code", 200);												// On notifie le client que tout c'est bien passé
							obj.put("message", "Sucess");
							sendTrame(this.client, obj);
						}
						if (this.client.isClosed() == false) {									// Si le socket du client est ouvert, on envoie alors les sinon
							if ((Integer)this.jObj.get("code") == 130) {						// on mit a jour l(état du client
								listUtiCanal = ServMain.UTI_CANAL;
								listUti = listUtiCanal.get(this.jObj.get("channel"));
								listUti.forEach((k, v) -> sendTrame(v, this.jObj));				// Lorsque le client envoie un message sur un canal, on envoie le message
							} else {															// a chaque utilisateur du canal
								sendTrame(this.client, this.jObj);
							}
						} else {
							c = DAOFactory.getConnection();
							userDAO = DAOFactory.getDAOUser(c);
							u = userDAO.getUserFromLogin(jSObj.getString("login"));
							u.setEtatConnexion(false);
							u = userDAO.update(u);
						}
					}
					line = br.readLine();
				}
			}

		} catch (IOException e) {													// Récupération des erreur SQL, DAO et IO
			LOG.error("Error during reading message from the client.", e);
		} catch (JSONException je) {
			LOG.error("Error during json", je);
		} catch (DAOException de) {
			LOG.error("Error during update User", de);
		} finally {																// Fermeture des connexion/flux
			try {
				if (this.client != null) {
					this.client.close();
					if (this.client.isClosed() && (login != null)) {
						u = userDAO.getUserFromLogin(this.login);
						u.setEtatConnexion(false);
						u = userDAO.update(u);
					}
				}
				if(br != null) {
					br.close();
				}
				if(isr != null) {
					isr.close();
				}
				if(in != null) {
					in.close();
				}
				if (c != null) {
					c.close();
				}
			} catch (IOException | SQLException | DAOException e) {
				LOG.error("Error during stream closing.", e);
			}
		}
	}
	
	private void sendTrame(Socket client, JSONObject jObj) {					// Envoie des trames aux clients
		OutputStream out = null;
		OutputStreamWriter osw = null;
		PrintWriter pw = null;
		
		try {
			out = client.getOutputStream();
			osw = new OutputStreamWriter(out);
			pw = new PrintWriter(osw);
			
			pw.print(jObj + "\n");
			pw.flush();

		} catch (IOException e) {
			LOG.error("Error during message sending.", e);
		} finally {
			try {																// Fermeture des connections/flux
					if (this.client.isClosed()) {
					if (out != null) {
						out.close();
					}
					if (osw != null) {
						osw.close();
					}
					if (pw != null) {
						pw.close();
					}
				}
			} catch (IOException e) {
				LOG.error("Error during stream closing", e);
			}
		}
	}
}
	

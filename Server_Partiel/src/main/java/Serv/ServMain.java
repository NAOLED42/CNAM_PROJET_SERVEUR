package Serv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.log4j.Logger;

import TrameProcess.*;

public class ServMain {
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	public static HashMap<String, HashMap<String, Socket>> UTI_CANAL = new HashMap <String, HashMap<String, Socket>> ();

	public static void main(String[] args) {
		ServerSocket server = null;

		LOG.info("Socket server started on the port : 4567");

		try {
			server = new ServerSocket(4567);									// Associe le serveur au port 4567

			while (true) {
				final Socket client = server.accept(); 							// Demarre la socket Server
				
				new Thread(new TrameIN(client)).start();						// Demarre une thread pour la relier à un client
			}
		} catch (IOException e) {
			LOG.error("Error during socket server creation.", e);
		} finally {
			try {																// Fermeture dur socket server
				if (server != null) {
					server.close();
				}
			} catch (IOException e) {
				LOG.error("Error during stream closing.", e);
			}
		}
	}
}

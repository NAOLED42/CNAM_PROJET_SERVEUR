package Serv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import TrameProcess.*;
import Interface.MenuPrincipal;

public class ServMain {
	private static final Logger LOG = Logger.getLogger(ServMain.class.getName());
	public static final List <Socket> CLIENT_LIST = new LinkedList <> ();

	public static void main(String[] args) {
		ServerSocket server = null;

		LOG.info("Socket server started on the port : 4567");

		try {
			// Bind server socket on the port 4567
			server = new ServerSocket(4567);
			
			MenuPrincipal test = new MenuPrincipal();

			while (true) {
				final Socket client = server.accept(); 
				
				//Add socket in all client list
				CLIENT_LIST.add(client);
				LOG.info("New client");

				// Lauch thread to process the client
				new Thread(new TrameIN(client)).start();
			}
		} catch (IOException e) {
			LOG.error("Error during socket server creation.", e);
		} finally {
			try {
				if (server != null) {
					server.close();
				}
			} catch (IOException e) {
				LOG.error("Error during stream closing.", e);
			}
		}
	}
}

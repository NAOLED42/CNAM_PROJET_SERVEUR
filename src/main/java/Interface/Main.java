package Interface;

import  TrameProcess.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
	
public class Main { 

		private static final Logger LOG = Logger.getLogger(Main.class.getName());

		public static void main(String[] args) {
			ServerSocket server = null;

			LOG.info("Socket server started on the port : 4567");

			try {
				// Bind server socket on the port 4567
				server = new ServerSocket(4567);

				while (true) {
					final Socket client = server.accept();

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



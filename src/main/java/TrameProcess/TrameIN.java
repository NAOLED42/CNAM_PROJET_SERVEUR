package TrameProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Model.dao.bean.Canal;
import Treatment.*;

public class TrameIN implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(TrameIN.class.getName());
	protected Socket client;
	protected Canal canal;
	
	public JSONObject jObj = new JSONObject();

	public TrameIN(Socket client) {
		this.client = client;
		this.canal = null;
	}

	@Override
	public void run() {
		InputStream in = null;
		InputStreamReader isr = null; 
		BufferedReader br = null;

		String sRET = "HELLO";
		
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
					EventAdpater EA = new EventAdpater(jSObj);
					sRET = EA.ChooseEvent();
					if ((sRET == "") || (sRET == null)) {
						sRET = "TU TE FOUT DE MA GUEUELE";
					}
					this.jObj = new JSONObject(new JSONTokener(sRET));
				
					if (jSObj.get("instruction").equals("disconnect")) {
						sendTrame (this.client, this.jObj);
						this.client.close();
					} else {
						sendTrame(this.client, this.jObj);
						line = br.readLine();
					}
				}
			} 

		} catch (IOException e) {
			LOG.error("Error during reading message from the client.", e);
		} catch (JSONException je) {
			LOG.error("Error during json", je);
		} finally {
			try {
				if(br != null) {
					br.close();
				}
				if(isr != null) {
					isr.close();
				}
				if(in != null) {
					in.close();
				}
			} catch (IOException e) {
				LOG.error("Error during stream closing.", e);
			}
		}
	}
	
	private void sendTrame(Socket client, JSONObject jObj) {
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
			LOG.error("Error durign message sending.", e);
		}
	}
}
	

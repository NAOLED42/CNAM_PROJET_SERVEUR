package view;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class MenuPrincipal extends JFrame implements ActionListener {
	
	final JButton buttonLog = new JButton("Consulter les logs");
	final JButton buttonRunServer = new JButton("Lancer le serveur IRC");
	final JButton buttonStopServer = new JButton("Arreter le serveur IRC");
	final JTextArea informationTextArea = new JTextArea(">> Informations bientot disponible...");
	private boolean serverIsLaunched = false;

	private static final long serialVersionUID = 1L;

	public MenuPrincipal() {
		super("T'Chat IRC (v0.1 alpha) - Menu principal");
		JFrame content = new JFrame();
		// Setting up the preferred size for the main window.
		content.setPreferredSize(new Dimension(800, 600));
		content.setLayout(new GridLayout(3, 0));

		// Adding all JPanels for main menu.
		content.add(getContentPanel());
		content.add(getButtonPanel());
		content.add(getInformationPanel());

		content.pack();
		content.setVisible(true);
	}

	// This function returns the text label of the main menu :
	private JPanel getContentPanel() {

		// Initialisation of vairables :
		final JPanel labelPanel = new JPanel(new GridLayout(1, 1));
		final JLabel descriptionText = new JLabel("Interface d'administration du serveur IRC : ");

		labelPanel.setBorder(new EmptyBorder(0, 20, 0, 0));
		labelPanel.add(descriptionText);

		return labelPanel;
	}

	// This function returns the panel for the buttons in the main menu :
	private JPanel getButtonPanel() {
		// Initialisation of vairables :

		final JPanel btnLogPanel = new JPanel(new GridLayout(1, 0));
		final JPanel btnRunPanel = new JPanel(new GridLayout(1, 0));
		final JPanel btnStopPanel = new JPanel(new GridLayout(1, 0));
		btnLogPanel.setBorder(new EmptyBorder(20, 250, 0, 250));
		btnRunPanel.setBorder(new EmptyBorder(20, 250, 0, 250));
		btnStopPanel.setBorder(new EmptyBorder(20, 250, 0, 250));

		btnLogPanel.add(buttonLog);
		btnRunPanel.add(buttonRunServer);
		btnStopPanel.add(buttonStopServer);

		JPanel buttonPanel = new JPanel(new GridLayout(3, 0));
		buttonPanel.add(btnLogPanel);
		buttonPanel.add(btnRunPanel);
		buttonPanel.add(btnStopPanel);

		//Adding all actionListeners for buttons.

		buttonRunServer.setActionCommand("runServer");
		buttonRunServer.addActionListener(this);
		
		buttonLog.setActionCommand("viewLog");
		buttonLog.addActionListener(this);
		
		buttonStopServer.setActionCommand("stopServer");
		buttonStopServer.addActionListener(this);

		return buttonPanel;
	}

	// This function returns the last information about the application e. g. the
	// last time server ran and on wich port.
	private JPanel getInformationPanel() {

		final JPanel informationPanel = new JPanel(new GridLayout(1, 1));
		informationPanel.setBorder(new EmptyBorder(40, 20, 50, 20));
		
		informationTextArea.setEditable(false);
		informationTextArea.setBorder(new TitledBorder(new EtchedBorder(), "Informations"));
		informationPanel.add(informationTextArea);

		return informationPanel;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(action.equals("runServer")) {
			if(serverIsLaunched) {
				JOptionPane.showMessageDialog(null,"Le serveur est déjà lancé !");
				
			}
			else {
				informationTextArea.setText(">> Lancement du serveur...");
				serverIsLaunched = true;
			}
		}
		else if(action.equals("viewLog")) {
			ExploitationLog test = new ExploitationLog();
		}
		else if(action.equals("stopServer")) {
			informationTextArea.setText(">> Arret du serveur...");
			serverIsLaunched = false;
		}
		
		
	}

}

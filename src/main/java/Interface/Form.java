package Interface;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Form extends JFrame{
		
	private static final long serialVersionUID = 1L;

	public Form() {
		super ("Panneau de connexion");
		final Container content = getContentPanel();
		
		content.add(null, BorderLayout.CENTER);
		content.add(null, BorderLayout.SOUTH);
		
		pack();
		setSize(800, 800);
		setVisible(true);
	}
	
	private JPanel getContentPanel() {
		final JTextField LoginField = new JTextField();
		final JPasswordField PassField = new JPasswordField() ;
		final JPanel FieldPanel = new JPanel();
		
		final JLabel login = new JLabel("Login");
		final JLabel password = new JLabel("Password");
		final JPanel labelPanel = new JPanel(new GridLayout());
		
		labelPanel.add(login);
		labelPanel.add(password);
		
		FieldPanel.add(LoginField);
		FieldPanel.add(PassField);
		
		final JPanel contentPanel = new JPanel(new BorderLayout());
		
		contentPanel.add(labelPanel, BorderLayout.WEST);
		contentPanel.add(FieldPanel, BorderLayout.CENTER);
		
		setResizable(false);
		return contentPanel;
	}

}

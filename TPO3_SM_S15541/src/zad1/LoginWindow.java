package zad1;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame {

	private JPanel contentPane;
	private JTextField loginField;
	LoginWindow frame;
	Client client;

	/**
	 * Launch the application.
	 */
	public void start(Client client) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new LoginWindow();
					frame.dupa(client);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @param client
	 */
	public void dupa(Client client) {
		this.client = client;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel lblPodajLogin = new JLabel("Podaj login:");
		GridBagConstraints gbc_lblPodajLogin = new GridBagConstraints();
		gbc_lblPodajLogin.insets = new Insets(0, 0, 5, 5);
		gbc_lblPodajLogin.anchor = GridBagConstraints.EAST;
		gbc_lblPodajLogin.gridx = 2;
		gbc_lblPodajLogin.gridy = 0;
		contentPane.add(lblPodajLogin, gbc_lblPodajLogin);

		loginField = new JTextField();
		GridBagConstraints gbc_loginField = new GridBagConstraints();
		gbc_loginField.insets = new Insets(0, 0, 5, 0);
		gbc_loginField.fill = GridBagConstraints.HORIZONTAL;
		gbc_loginField.gridx = 3;
		gbc_loginField.gridy = 0;
		contentPane.add(loginField, gbc_loginField);
		loginField.setColumns(10);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.setLogin(loginField.getText());
				bye();
			}
		});

		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.gridx = 3;
		gbc_btnOk.gridy = 1;
		contentPane.add(btnOk, gbc_btnOk);
	}

	private void bye() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

}

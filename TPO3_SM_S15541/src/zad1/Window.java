package zad1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class Window extends JFrame {

	private JPanel contentPane;
	JTextArea textArea;
	Window frameTo;
	String name;
	Client client;

	/**
	 * Launch the application.
	 */
	public void start(String name, Client client) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frameTo = new Window();
					frameTo.dupa(name, client);
					frameTo.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @param name
	 * @param client
	 */
	public void dupa(String name, Client client) {
		setTitle(name);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				textArea.select(textArea.getHeight() + 1000, 0);
			}
		});

		JTextArea writeArea = new JTextArea();
		GridBagConstraints gbc_writeArea = new GridBagConstraints();
		gbc_writeArea.insets = new Insets(0, 0, 0, 5);
		gbc_writeArea.fill = GridBagConstraints.BOTH;
		gbc_writeArea.gridx = 0;
		gbc_writeArea.gridy = 7;
		contentPane.add(writeArea, gbc_writeArea);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.writeToServer(writeArea.getText());
				writeArea.setText(null);
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 7;
		contentPane.add(btnSend, gbc_btnSend);
	}

	public void addText(String text) {
		frameTo.textArea.append(text);
	}
}

package zad1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ControlPanel extends JFrame {

	private JPanel contentPane;
	private JTextField setCountry;
	private JTextField setCity;
	private JLabel weather;
	private JLabel temperature;
	private JLabel pressure;
	private JLabel humidity;
	private JLabel exchangeTo;
	private JLabel exchangeToPLN;
	private JLabel rateTo;
	private JLabel rateToPLN;
	private JTextField setCurrencyToExchange;
	private JLabel lblPodajKraj;
	private JLabel lblPodajMiasto;
	private JLabel lblPodajWalute;

	/**
	 * Launch the application.
	 */
	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlPanel frame = new ControlPanel();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the frame.
	 */
	public ControlPanel() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 30, 33, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		setCountry = new JTextField();
		setCountry.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				setCountry.setText("");
			}
		});

		lblPodajKraj = new JLabel("Podaj kraj po angielsku:");
		GridBagConstraints gbc_lblPodajKraj = new GridBagConstraints();
		gbc_lblPodajKraj.insets = new Insets(0, 0, 5, 5);
		gbc_lblPodajKraj.gridx = 0;
		gbc_lblPodajKraj.gridy = 0;
		contentPane.add(lblPodajKraj, gbc_lblPodajKraj);
		
				lblPodajMiasto = new JLabel("Podaj miasto po angielsku:");
				GridBagConstraints gbc_lblPodajMiasto = new GridBagConstraints();
				gbc_lblPodajMiasto.insets = new Insets(0, 0, 5, 5);
				gbc_lblPodajMiasto.gridx = 1;
				gbc_lblPodajMiasto.gridy = 0;
				contentPane.add(lblPodajMiasto, gbc_lblPodajMiasto);
		setCountry.setText("Podaj kraj");
		GridBagConstraints gbc_setCountry = new GridBagConstraints();
		gbc_setCountry.fill = GridBagConstraints.HORIZONTAL;
		gbc_setCountry.insets = new Insets(0, 0, 5, 5);
		gbc_setCountry.gridx = 0;
		gbc_setCountry.gridy = 1;
		contentPane.add(setCountry, gbc_setCountry);
		setCountry.setColumns(10);

		JButton execute = new JButton("Wykonaj");
		execute.addActionListener(new ActionListener() {
			private boolean clicked = false;

			public void actionPerformed(ActionEvent e) {
				if (clicked) {
					System.exit(0);
				}
				clicked = true;
				JButton buttonPressed = (JButton) e.getSource();
				Service s = new Service(setCountry.getText());
				String city = setCity.getText();

				String weather = s.getWeather(city);
				HashMap<String, HashMap<String, Object>> result = null;
				try {
					result = new ObjectMapper().readValue(weather, HashMap.class);
				} catch (JsonParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JsonMappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				temperature.setText("Temperatura " + new DecimalFormat("0.#").
						format(((Double) result.get("main").get("temp")-273.15)) + " C");
				pressure.setText("Cisnienie " + result.get("main").get("pressure").toString());
				humidity.setText("Wilgotnosc " + result.get("main").get("humidity").toString());

				Double rateToResult = s.getRateFor(setCurrencyToExchange.getText().toUpperCase());
				Double rateToPLNResult = s.getNBPRate();

				if (rateToResult == null) {
					rateTo.setText("Nie udalo sie pobrac danych");
				}else {
					rateTo.setText(new DecimalFormat("0.####").format(rateToResult) + " " + s.currencyOfSearchedCountry);
				}
				rateToPLN.setText(rateToPLNResult.toString() + " PLN");

				Runnable task = () -> {
					PageView pv = new PageView();
					pv.getCity(city);
					pv.go();
				};
				new Thread(task).start();
				execute.setText("Zakoñcz");
			}
		});

		execute.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
				setCity = new JTextField();
				setCity.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent arg0) {
						setCity.setText("");
					}
				});
				setCity.setText("Podaj miasto");
				GridBagConstraints gbc_setCity = new GridBagConstraints();
				gbc_setCity.fill = GridBagConstraints.HORIZONTAL;
				gbc_setCity.insets = new Insets(0, 0, 5, 5);
				gbc_setCity.anchor = GridBagConstraints.NORTH;
				gbc_setCity.gridx = 1;
				gbc_setCity.gridy = 1;
				contentPane.add(setCity, gbc_setCity);
				setCity.setColumns(10);
		
				setCurrencyToExchange = new JTextField();
				setCurrencyToExchange.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent arg0) {
						setCurrencyToExchange.setText("");
					}
				});
						
								lblPodajWalute = new JLabel("Podaj kod waluty:");
								GridBagConstraints gbc_lblPodajWalute = new GridBagConstraints();
								gbc_lblPodajWalute.insets = new Insets(0, 0, 5, 5);
								gbc_lblPodajWalute.gridx = 0;
								gbc_lblPodajWalute.gridy = 2;
								contentPane.add(lblPodajWalute, gbc_lblPodajWalute);
				setCurrencyToExchange.setText("Podaj kod waluty");
				GridBagConstraints gbc_setCurrencyToExchange = new GridBagConstraints();
				gbc_setCurrencyToExchange.fill = GridBagConstraints.HORIZONTAL;
				gbc_setCurrencyToExchange.insets = new Insets(0, 0, 5, 5);
				gbc_setCurrencyToExchange.gridx = 0;
				gbc_setCurrencyToExchange.gridy = 3;
				contentPane.add(setCurrencyToExchange, gbc_setCurrencyToExchange);
				setCurrencyToExchange.setColumns(10);
		GridBagConstraints gbc_execute = new GridBagConstraints();
		gbc_execute.insets = new Insets(0, 0, 5, 5);
		gbc_execute.gridx = 0;
		gbc_execute.gridy = 4;
		contentPane.add(execute, gbc_execute);

		weather = new JLabel("POGODA:");
		GridBagConstraints gbc_weather = new GridBagConstraints();
		gbc_weather.insets = new Insets(0, 0, 5, 5);
		gbc_weather.gridx = 0;
		gbc_weather.gridy = 5;
		contentPane.add(weather, gbc_weather);

		temperature = new JLabel();
		GridBagConstraints gbc_temperature = new GridBagConstraints();
		gbc_temperature.gridwidth = 2;
		gbc_temperature.insets = new Insets(0, 0, 5, 0);
		gbc_temperature.gridx = 1;
		gbc_temperature.gridy = 5;
		contentPane.add(temperature, gbc_temperature);

		pressure = new JLabel();
		GridBagConstraints gbc_pressure = new GridBagConstraints();
		gbc_pressure.insets = new Insets(0, 0, 5, 5);
		gbc_pressure.gridx = 1;
		gbc_pressure.gridy = 6;
		contentPane.add(pressure, gbc_pressure);

		humidity = new JLabel();
		GridBagConstraints gbc_humidity = new GridBagConstraints();
		gbc_humidity.insets = new Insets(0, 0, 5, 5);
		gbc_humidity.gridx = 1;
		gbc_humidity.gridy = 7;
		contentPane.add(humidity, gbc_humidity);

		exchangeTo = new JLabel("KURS DO ZADANEJ WALUTY:");
		GridBagConstraints gbc_exchangeTo = new GridBagConstraints();
		gbc_exchangeTo.insets = new Insets(0, 0, 5, 5);
		gbc_exchangeTo.gridx = 0;
		gbc_exchangeTo.gridy = 8;
		contentPane.add(exchangeTo, gbc_exchangeTo);

		rateTo = new JLabel();
		GridBagConstraints gbc_rateTo = new GridBagConstraints();
		gbc_rateTo.insets = new Insets(0, 0, 5, 5);
		gbc_rateTo.gridx = 1;
		gbc_rateTo.gridy = 8;
		contentPane.add(rateTo, gbc_rateTo);

		exchangeToPLN = new JLabel("KURS DO PLN:");
		GridBagConstraints gbc_exchangeToPLN = new GridBagConstraints();
		gbc_exchangeToPLN.insets = new Insets(0, 0, 0, 5);
		gbc_exchangeToPLN.gridx = 0;
		gbc_exchangeToPLN.gridy = 9;
		contentPane.add(exchangeToPLN, gbc_exchangeToPLN);

		rateToPLN = new JLabel();
		GridBagConstraints gbc_rateToPLN = new GridBagConstraints();
		gbc_rateToPLN.insets = new Insets(0, 0, 0, 5);
		gbc_rateToPLN.gridx = 1;
		gbc_rateToPLN.gridy = 9;
		contentPane.add(rateToPLN, gbc_rateToPLN);
	}

}

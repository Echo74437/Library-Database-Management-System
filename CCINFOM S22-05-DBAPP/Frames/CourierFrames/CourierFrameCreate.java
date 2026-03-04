package Frames.CourierFrames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import Frames.*;

public class CourierFrameCreate extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private String[] Vehicles = {"Select Vehicle Type", "Motorcycle", "Van", "4-Wheeler Truck"};



	
	public CourierFrameCreate() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 380, 500);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

		JTextField lastNameField = new JTextField();
		lastNameField.setBounds(226, 36, 130, 20);
		
		contentPane.add(lastNameField);
		lastNameField.setColumns(10);

		JTextField firstNameField = new JTextField();
		firstNameField.setBounds(226, 66, 130, 18);
		contentPane.add(firstNameField);
		firstNameField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Last Name\r\n");
		lblNewLabel.setBounds(10, 36, 77, 20);
		contentPane.add(lblNewLabel);
		
		JLabel lblFirstName = new JLabel("First Name\r\n");
		lblFirstName.setBounds(10, 66, 77, 20);
		contentPane.add(lblFirstName);

		JLabel lblContact = new JLabel("Contact Number\r\n");
		lblContact.setBounds(10, 96, 177, 20);
		contentPane.add(lblContact);

		JTextField contactField = new JTextField();
		contactField.setColumns(10);
		contactField.setBounds(226, 96, 130, 18);
		contentPane.add(contactField);

		JTextField cityField = new JTextField();
		cityField.setColumns(10);
		cityField.setBounds(226, 124, 130, 18);
		contentPane.add(cityField);
		
		JLabel lblCity = new JLabel("City (Do not include \"city\")\r\n");
		lblCity.setBounds(10, 124, 206, 20);
		contentPane.add(lblCity);

		JComboBox<String> vehicleBox = new JComboBox<String>(Vehicles);
		vehicleBox.setBounds(226, 160, 130, 20);
		contentPane.add(vehicleBox);

		String[] Status = {"Available", "Unavailable"};
		JComboBox<String> statusBox = new JComboBox<String>(Status);
		statusBox.setBounds(226, 190, 130, 20);
		
		
		contentPane.add(vehicleBox);
		contentPane.add(statusBox);

		JLabel lblSelectDeliveryVehicle = new JLabel("Select Delivery Vehicle Type");
		lblSelectDeliveryVehicle.setBounds(10, 160, 198, 20);
		contentPane.add(lblSelectDeliveryVehicle);

		JLabel lblAvailability = new JLabel("Select Courier Availability");
		lblAvailability.setBounds(10, 190, 198, 20);
		contentPane.add(lblAvailability);


		JButton btnNewButton = new JButton("Add Courier");
		btnNewButton.setBounds(60, 279, 246, 20);
		contentPane.add(btnNewButton);
		
		
		btnNewButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {



//
				if (!Objects.equals(vehicleBox.getSelectedItem().toString(), "Select Vehicle Type") &&
						!lastNameField.getText().trim().isEmpty() &&
						!firstNameField.getText().trim().isEmpty() &&
						!cityField.getText().trim().isEmpty() &&
						contactField.getText().trim().length() == 11)  {
					String last = lastNameField.getText();
					String first = firstNameField.getText();
					String city = cityField.getText();
					String vehicle = vehicleBox.getSelectedItem().toString();
					String status = statusBox.getSelectedItem().toString();
					String contact = contactField.getText();

					//connection and query here


                    try {
						DBManager dbManager = DBManager.getInstance();
                        Connection conn = dbManager.getConnection();

						PreparedStatement stmt = conn.prepareStatement(
								"INSERT INTO Courier (LastName, FirstName, ContactNumber, City, VehicleType, Status) VALUES (?, ?, ?, ?, ?, ?);");

						stmt.setString(1, last);
						stmt.setString(2, first);
						stmt.setString(3, contact);
						stmt.setString(4, city);
						stmt.setString(5, vehicle);
						stmt.setString(6, status);

						stmt.execute();
						stmt.close();
                    } catch (SQLException ex) {
						System.err.println("Failed to establish connection: " + ex.getMessage());
                    }


					dispose();
				}
				else {
					JLabel lblerror = new JLabel("Please Fill in all Spaces Properly");
					lblerror.setBounds(60, 309, 200, 50);
					lblerror.setVisible(true);
					contentPane.add(lblerror);
					contentPane.revalidate();
					contentPane.repaint();
				}
		    }
		});
		
		

	}
}

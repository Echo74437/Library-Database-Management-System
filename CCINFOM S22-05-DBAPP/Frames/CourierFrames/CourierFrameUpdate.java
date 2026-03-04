package Frames.CourierFrames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import Frames.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class CourierFrameUpdate extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private String[] Vehicles = {"Select Vehicle Type", "Motorcycle", "Van", "4-Wheeler Truck"};
	private JTextField textField_3;
	

	public CourierFrameUpdate() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 380, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);
		setVisible(true);

		JLabel lblCourierID = new JLabel("Courier ID to be changed");
		lblCourierID.setBounds(10, 20, 185, 20);
		contentPane.add(lblCourierID);

		JTextField courierIDField = new JTextField();
		courierIDField.setColumns(10);
		courierIDField.setBounds(226, 20, 130, 20);
		contentPane.add(courierIDField);

		JLabel lblLastname = new JLabel("Last Name");
		lblLastname.setBounds(10, 50, 77, 20);
		contentPane.add(lblLastname);

		JTextField lastNameField = new JTextField();
		lastNameField.setColumns(10);
		lastNameField.setBounds(226, 50, 130, 20);
		contentPane.add(lastNameField);

		JLabel lblFirstName = new JLabel("First Name");
		lblFirstName.setBounds(10, 80, 77, 20);
		contentPane.add(lblFirstName);

		JTextField firstNameField = new JTextField();
		firstNameField.setColumns(10);
		firstNameField.setBounds(226, 80, 130, 20);
		contentPane.add(firstNameField);

		JLabel lblContact = new JLabel("Contact Number");
		lblContact.setBounds(10, 110, 177, 20);
		contentPane.add(lblContact);

		JTextField contactField = new JTextField();
		contactField.setColumns(10);
		contactField.setBounds(226, 110, 130, 20);
		contentPane.add(contactField);

		JLabel lblCity = new JLabel("City (Do not include \"city\")");
		lblCity.setBounds(10, 140, 206, 20);
		contentPane.add(lblCity);

		JTextField cityField = new JTextField();
		cityField.setColumns(10);
		cityField.setBounds(226, 140, 130, 20);
		contentPane.add(cityField);

		JLabel lblSelectDeliveryVehicle = new JLabel("Select Delivery Vehicle Type");
		lblSelectDeliveryVehicle.setBounds(10, 170, 198, 20);
		contentPane.add(lblSelectDeliveryVehicle);

		JComboBox<String> vehicleBox = new JComboBox<String>(Vehicles);
		vehicleBox.setBounds(226, 170, 130, 20);
		contentPane.add(vehicleBox);

		JLabel lblAvailability = new JLabel("Select Courier Availability");
		lblAvailability.setBounds(10, 200, 198, 20);
		contentPane.add(lblAvailability);

		String[] Status = {"Available", "Unavailable"};
		JComboBox<String> statusBox = new JComboBox<String>(Status);
		statusBox.setBounds(226, 200, 130, 20);
		contentPane.add(statusBox);

		JButton btnNewButton = new JButton("Update Courier");
		btnNewButton.setBounds(60, 240, 296, 25);
		contentPane.add(btnNewButton);
		
		
		btnNewButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {

				if (!Objects.equals(vehicleBox.getSelectedItem().toString(), "Select Vehicle Type") &&
						!lastNameField.getText().trim().isEmpty() &&
						!firstNameField.getText().trim().isEmpty() &&
						!cityField.getText().trim().isEmpty() &&
						contactField.getText().trim().length() == 11 &&
						!courierIDField.getText().trim().isEmpty())  {
					String last = lastNameField.getText();
					String first = firstNameField.getText();
					String city = cityField.getText();
					String vehicle = vehicleBox.getSelectedItem().toString();
					String status = statusBox.getSelectedItem().toString();
					String contact = contactField.getText();
					String ID = courierIDField.getText();

					//connection and query here


					try {
						DBManager dbManager = DBManager.getInstance();
						Connection conn = dbManager.getConnection();

						PreparedStatement stmt = conn.prepareStatement(
								"UPDATE Courier SET LastName = ?, FirstName = ?, ContactNumber = ?," +
										" City = ?, VehicleType = ?, Status = ? WHERE Cour_ID = ?");

						stmt.setString(1, last);
						stmt.setString(2, first);
						stmt.setString(3, contact);
						stmt.setString(4, city);
						stmt.setString(5, vehicle);
						stmt.setString(6, status);
						stmt.setString(7, ID);

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

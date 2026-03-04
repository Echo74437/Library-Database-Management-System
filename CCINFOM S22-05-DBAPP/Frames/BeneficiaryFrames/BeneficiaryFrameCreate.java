package Frames.BeneficiaryFrames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import Frames.*;

public class BeneficiaryFrameCreate extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public BeneficiaryFrameCreate() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 580);
		setTitle("Add New Beneficiary");
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

		// Beneficiary ID
		JLabel lblBeneficiaryID = new JLabel("Beneficiary ID (100000-999999):");
		lblBeneficiaryID.setBounds(20, 30, 250, 25);
		contentPane.add(lblBeneficiaryID);

		JTextField beneficiaryIDField = new JTextField();
		beneficiaryIDField.setBounds(20, 60, 430, 25);
		contentPane.add(beneficiaryIDField);
		beneficiaryIDField.setColumns(10);

		// Beneficiary Name
		JLabel lblName = new JLabel("Beneficiary Name:");
		lblName.setBounds(20, 100, 250, 25);
		contentPane.add(lblName);

		JTextField nameField = new JTextField();
		nameField.setBounds(20, 130, 430, 25);
		contentPane.add(nameField);
		nameField.setColumns(10);

		// Address
		JLabel lblAddress = new JLabel("Address (Street, Barangay, Province):");
		lblAddress.setBounds(20, 170, 350, 25);
		contentPane.add(lblAddress);

		JTextField addressField = new JTextField();
		addressField.setBounds(20, 200, 430, 25);
		contentPane.add(addressField);
		addressField.setColumns(10);

		// City
		JLabel lblCity = new JLabel("City:");
		lblCity.setBounds(20, 240, 250, 25);
		contentPane.add(lblCity);

		JTextField cityField = new JTextField();
		cityField.setBounds(20, 270, 430, 25);
		contentPane.add(cityField);
		cityField.setColumns(10);

		// Contact Number
		JLabel lblContact = new JLabel("Contact Number (11 digits):");
		lblContact.setBounds(20, 310, 250, 25);
		contentPane.add(lblContact);

		JTextField contactField = new JTextField();
		contactField.setBounds(20, 340, 430, 25);
		contentPane.add(contactField);
		contactField.setColumns(10);

		// Add Button
		JButton btnAdd = new JButton("Add Beneficiary");
		btnAdd.setBounds(150, 390, 200, 35);
		btnAdd.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(btnAdd);

		// Error Label
		JLabel lblError = new JLabel("");
		lblError.setBounds(20, 450, 430, 60);
		lblError.setForeground(java.awt.Color.RED);
		contentPane.add(lblError);

		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblError.setText("");
				lblError.setVisible(false);

				String beneficiaryID = beneficiaryIDField.getText().trim();
				String name = nameField.getText().trim();
				String address = addressField.getText().trim();
				String city = cityField.getText().trim();
				String contact = contactField.getText().trim();
				String status = "Active";

				if (beneficiaryID.isEmpty() || name.isEmpty() || address.isEmpty() || 
					city.isEmpty() || contact.isEmpty()) {
					lblError.setText("Please fill in all fields");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				try {
					int id = Integer.parseInt(beneficiaryID);
					if (id < 100000 || id > 999999) {
						lblError.setText("Beneficiary ID must be between 100000-999999");
						lblError.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}
				} catch (NumberFormatException ex) {
					lblError.setText("Beneficiary ID must be a valid 6-digit number");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				if (!name.matches("^[A-Za-z\\-\\s]+$")) {
					lblError.setText("Name can only contain letters, hyphens, and spaces");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

			long commaCount = address.chars().filter(ch -> ch == ',').count();
			if (commaCount != 2) {
				lblError.setText("Address must be in format: Street, Barangay, Province (2 commas)");
				lblError.setVisible(true);
				contentPane.revalidate();
				contentPane.repaint();
				return;
			}
				if (!contact.matches("^[0-9]{11}$")) {
					lblError.setText("Contact must be exactly 11 digits");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String sql = "INSERT INTO Beneficiaries (BeneficiaryID, BeneficiaryName, Address, City, ContactNumber, Status) VALUES (?, ?, ?, ?, ?, ?)";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(beneficiaryID));
					pstmt.setString(2, name);
					pstmt.setString(3, address);
					pstmt.setString(4, city);
					pstmt.setString(5, contact);
					pstmt.setString(6, status);

					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Beneficiary added successfully: " + name);
						dispose();
					} else {
						lblError.setText("Failed to add beneficiary");
						lblError.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
					}

					pstmt.close();

				} catch (SQLException ex) {
					System.err.println("Database error: " + ex.getMessage());
					lblError.setText("Error: " + ex.getMessage());
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
				}
			}
		});
	}
}
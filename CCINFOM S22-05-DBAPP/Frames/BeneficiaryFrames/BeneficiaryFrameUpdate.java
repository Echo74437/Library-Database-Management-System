package Frames.BeneficiaryFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Frames.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BeneficiaryFrameUpdate extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String[] statusOptions = {"Select Status", "Active", "Inactive"};

	public BeneficiaryFrameUpdate() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 750);
		setTitle("Update Beneficiary Information");
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

		// Beneficiary List Section
		JLabel lblBeneficiaryList = new JLabel("Existing Beneficiaries:");
		lblBeneficiaryList.setBounds(20, 20, 200, 25);
		lblBeneficiaryList.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblBeneficiaryList);

		JTextArea beneficiaryListArea = new JTextArea();
		beneficiaryListArea.setEditable(false);
		beneficiaryListArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(beneficiaryListArea);
		scrollPane.setBounds(20, 50, 740, 150);
		contentPane.add(scrollPane);

		// Load beneficiaries on initialization
		loadBeneficiaryList(beneficiaryListArea);

		JLabel lblSeparator = new JLabel("Update Form:");
		lblSeparator.setBounds(20, 220, 200, 25);
		lblSeparator.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblSeparator);

		JLabel lblBeneficiaryID = new JLabel("Beneficiary ID to Update:");
		lblBeneficiaryID.setBounds(20, 260, 250, 25);
		contentPane.add(lblBeneficiaryID);

		JTextField beneficiaryIDField = new JTextField();
		beneficiaryIDField.setBounds(20, 290, 740, 25);
		contentPane.add(beneficiaryIDField);
		beneficiaryIDField.setColumns(10);

		// Beneficiary Name
		JLabel lblName = new JLabel("New Beneficiary Name:");
		lblName.setBounds(20, 330, 250, 25);
		contentPane.add(lblName);

		JTextField nameField = new JTextField();
		nameField.setBounds(20, 360, 740, 25);
		contentPane.add(nameField);
		nameField.setColumns(10);

		// Address
		JLabel lblAddress = new JLabel("New Address (Street, Barangay, Province):");
		lblAddress.setBounds(20, 400, 350, 25);
		contentPane.add(lblAddress);

		JTextField addressField = new JTextField();
		addressField.setBounds(20, 430, 740, 25);
		contentPane.add(addressField);
		addressField.setColumns(10);

		// City
		JLabel lblCity = new JLabel("New City:");
		lblCity.setBounds(20, 470, 250, 25);
		contentPane.add(lblCity);

		JTextField cityField = new JTextField();
		cityField.setBounds(20, 500, 740, 25);
		contentPane.add(cityField);
		cityField.setColumns(10);

		// Contact Number
		JLabel lblContact = new JLabel("New Contact Number (11 digits):");
		lblContact.setBounds(20, 540, 250, 25);
		contentPane.add(lblContact);

		JTextField contactField = new JTextField();
		contactField.setBounds(20, 570, 740, 25);
		contentPane.add(contactField);
		contactField.setColumns(10);

		// Status
		JLabel lblStatus = new JLabel("New Status:");
		lblStatus.setBounds(20, 610, 250, 25);
		contentPane.add(lblStatus);

		JComboBox<String> statusBox = new JComboBox<String>(statusOptions);
		statusBox.setBounds(20, 640, 200, 25);
		contentPane.add(statusBox);

		// Update Button
		JButton btnUpdate = new JButton("Update Beneficiary");
		btnUpdate.setBounds(280, 680, 200, 35);
		btnUpdate.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(btnUpdate);

		// Error Label
		JLabel lblError = new JLabel("");
		lblError.setBounds(20, 720, 740, 30);
		lblError.setForeground(java.awt.Color.RED);
		contentPane.add(lblError);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblError.setText("");
				lblError.setVisible(false);

				String beneficiaryID = beneficiaryIDField.getText().trim();
				String name = nameField.getText().trim();
				String address = addressField.getText().trim();
				String city = cityField.getText().trim();
				String contact = contactField.getText().trim();
				String status = statusBox.getSelectedItem().toString();

				if (beneficiaryID.isEmpty() || name.isEmpty() || address.isEmpty() || 
					city.isEmpty() || contact.isEmpty() || status.equals("Select Status")) {
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
					lblError.setText("Beneficiary ID must be a valid number");
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
				lblError.setText("Address must be in format: Street, Barangay, Province");
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

					String sql = "UPDATE Beneficiaries SET BeneficiaryName = ?, Address = ?, City = ?, ContactNumber = ?, Status = ? WHERE BeneficiaryID = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, name);
					pstmt.setString(2, address);
					pstmt.setString(3, city);
					pstmt.setString(4, contact);
					pstmt.setString(5, status);
					pstmt.setInt(6, Integer.parseInt(beneficiaryID));

					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Beneficiary updated successfully: ID " + beneficiaryID);
						lblError.setText("Beneficiary updated successfully!");
						lblError.setForeground(java.awt.Color.GREEN);
						lblError.setVisible(true);
						// Refresh the beneficiary list
						loadBeneficiaryList(beneficiaryListArea);
						// Clear form fields
						beneficiaryIDField.setText("");
						nameField.setText("");
						addressField.setText("");
						cityField.setText("");
						contactField.setText("");
						statusBox.setSelectedIndex(0);
					} else {
						lblError.setText("Beneficiary ID not found");
						lblError.setVisible(true);
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

	private void loadBeneficiaryList(JTextArea textArea) {
		try {
			DBManager dbManager = DBManager.getInstance();
			Connection conn = dbManager.getConnection();

			String sql = "SELECT BeneficiaryID, BeneficiaryName, City, Status FROM Beneficiaries ORDER BY BeneficiaryID";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			StringBuilder listText = new StringBuilder();
			listText.append(String.format("%-8s %-30s %-20s %-10s\n", "ID", "Name", "City", "Status"));
			listText.append("─────────────────────────────────────────────────────────────────────────────────────\n");

			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				listText.append(String.format("%-8d %-30s %-20s %-10s\n",
					rs.getInt("BeneficiaryID"),
					rs.getString("BeneficiaryName"),
					rs.getString("City"),
					rs.getString("Status")));
			}

			if (!hasData) {
				listText.append("No beneficiaries found in database.\n");
			}

			textArea.setText(listText.toString());

			rs.close();
			pstmt.close();

		} catch (SQLException ex) {
			System.err.println("Database error: " + ex.getMessage());
			textArea.setText("Error loading beneficiaries: " + ex.getMessage());
		}
	}
}
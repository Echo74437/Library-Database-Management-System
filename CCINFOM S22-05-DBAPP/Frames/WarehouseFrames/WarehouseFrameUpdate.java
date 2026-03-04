package Frames.WarehouseFrames;

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
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class WarehouseFrameUpdate extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public WarehouseFrameUpdate() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 750);
		setTitle("Update Warehouse Information");
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

		// Warehouse List Section
		JLabel lblWarehouseList = new JLabel("Existing Warehouse:");
		lblWarehouseList.setBounds(20, 20, 200, 25);
		lblWarehouseList.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblWarehouseList);

		JTextArea warehouseListArea = new JTextArea();
		warehouseListArea.setEditable(false);
		warehouseListArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(warehouseListArea);
		scrollPane.setBounds(20, 50, 740, 150);
		contentPane.add(scrollPane);

		// Load warehouses on initialization
		loadWarehouseList(warehouseListArea);

		// Separator
		JLabel lblSeparator = new JLabel("Update Form:");
		lblSeparator.setBounds(20, 220, 200, 25);
		lblSeparator.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblSeparator);

		// Warehouse ID to Update
		JLabel lblWarehouseID = new JLabel("Warehouse ID to Update:");
		lblWarehouseID.setBounds(20, 260, 250, 25);
		contentPane.add(lblWarehouseID);

		JTextField warehouseIDField = new JTextField();
		warehouseIDField.setBounds(20, 290, 740, 25);
		contentPane.add(warehouseIDField);
		warehouseIDField.setColumns(10);

		// Warehouse Name
		JLabel lblName = new JLabel("New Warehouse Name:");
		lblName.setBounds(20, 330, 250, 25);
		contentPane.add(lblName);

		JTextField nameField = new JTextField();
		nameField.setBounds(20, 360, 740, 25);
		contentPane.add(nameField);
		nameField.setColumns(10);

		// Address
		JLabel lblAddress = new JLabel("New Address (Building Number, Street, Barangay):");
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

		// Update Button
		JButton btnUpdate = new JButton("Update Warehouse");
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

				String warehouseID = warehouseIDField.getText().trim();
				String name = nameField.getText().trim();
				String address = addressField.getText().trim();
				String city = cityField.getText().trim();

				// Validate Warehouse ID
				try {
					int id = Integer.parseInt(warehouseID);
				if (!warehouseID.isEmpty() && (id < 1000 || id > 9999)) {
					lblError.setText("Warehouse ID must be between 1000 - 9999");
						lblError.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}
				} catch (NumberFormatException ex) {
					lblError.setText("Warehouse ID must be a valid number");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Validate Address
				long commaCount = address.chars().filter(ch -> ch == ',').count();
				if (!address.isEmpty() && commaCount != 2) {
					lblError.setText("Address must be in format: Building Number, Street, Barangay (2 commas)");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Database Update
				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String sql = "UPDATE Warehouse SET WarehouseName = ?, Address = ?, City = ? WHERE WarehouseID = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, name);
					pstmt.setString(2, address);
					pstmt.setString(3, city);
					pstmt.setInt(4, Integer.parseInt(warehouseID));

					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Warehouse updated successfully: ID " + warehouseID);
						lblError.setText("Warehouse updated successfully!");
						lblError.setForeground(java.awt.Color.GREEN);
						lblError.setVisible(true);

						loadWarehouseList(warehouseListArea);

						warehouseIDField.setText("");
						nameField.setText("");
						addressField.setText("");
						cityField.setText("");
					} else {
						lblError.setText("Warehouse ID not found");
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

	// Method to load and display all warehouses
	private void loadWarehouseList(JTextArea textArea) {
		try {
			DBManager dbManager = DBManager.getInstance();
			Connection conn = dbManager.getConnection();

			String sql = "SELECT WarehouseID, WarehouseName, Address, City FROM Warehouse ORDER BY WarehouseID";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			StringBuilder listText = new StringBuilder();
			listText.append(String.format("%-8s %-25s %-40s %-10s\n", "ID", "Name", "Address", "City"));
			listText.append("────────────────────────────────────────────────────────────────────────────────────────────────────\n");

			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				listText.append(String.format("%-8d %-25s %-40s %-10s\n",
					rs.getInt("WarehouseID"),
					rs.getString("WarehouseName"),
					rs.getString("Address"),
					rs.getString("City")));
			}

			if (!hasData) {
				listText.append("No warehouse data found in database.\n");
			}

			textArea.setText(listText.toString());

			rs.close();
			pstmt.close();

		} catch (SQLException ex) {
			System.err.println("Database error: " + ex.getMessage());
			textArea.setText("Error loading warehouse data: " + ex.getMessage());
		}
	}
}

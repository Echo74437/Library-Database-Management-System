package Frames.WarehouseFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import Frames.*;

public class WarehouseFrameCreate extends JFrame {

    private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public WarehouseFrameCreate() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 580);
		setTitle("Add New Warehouse");
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

        // Get Warehouse ID
		JLabel lblWarehouseID = new JLabel("Warehouse ID (1000-9999):");
		lblWarehouseID.setBounds(20, 30, 250, 25);
		contentPane.add(lblWarehouseID);

		JTextField warehouseIDField = new JTextField();
		warehouseIDField.setBounds(20, 60, 430, 25);
		contentPane.add(warehouseIDField);
		warehouseIDField.setColumns(10);

        // Get Warehouse Name
		JLabel lblName = new JLabel("Warehouse Name:");
		lblName.setBounds(20, 100, 250, 25);
		contentPane.add(lblName);

		JTextField nameField = new JTextField();
		nameField.setBounds(20, 130, 430, 25);
		contentPane.add(nameField);
		nameField.setColumns(10);

        // Get Warehouse Address
		JLabel lblAddress = new JLabel("Address (Building Number, Street, Barangay):");
		lblAddress.setBounds(20, 170, 350, 25);
		contentPane.add(lblAddress);

		JTextField addressField = new JTextField();
		addressField.setBounds(20, 200, 430, 25);
		contentPane.add(addressField);
		addressField.setColumns(10);

        // Get Warehouse City
		JLabel lblCity = new JLabel("City:");
		lblCity.setBounds(20, 240, 250, 25);
		contentPane.add(lblCity);

		JTextField cityField = new JTextField();
		cityField.setBounds(20, 270, 430, 25);
		contentPane.add(cityField);
		cityField.setColumns(10);

        // Add to Database button
		JButton btnAdd = new JButton("Add Warehouse");
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

				String warehouseID = warehouseIDField.getText().trim();
				String name = nameField.getText().trim();
				String address = addressField.getText().trim();
				String city = cityField.getText().trim();

				// Validation
				if (warehouseID.isEmpty() || name.isEmpty() || address.isEmpty() || 
					city.isEmpty()) {
					lblError.setText("Please fill in all fields");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Validate ID
				try {
					int id = Integer.parseInt(warehouseID);
					if (id < 1000 || id > 9999) {
						lblError.setText("Warehouse ID must be between 1000-9999");
						lblError.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}
				} catch (NumberFormatException ex) {
					lblError.setText("Warehouse ID must be a valid 4-digit number");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Validate Address
				long commaCount = address.chars().filter(ch -> ch == ',').count();
				if (commaCount != 2) {
					lblError.setText("Address must be in format: Building, Street, Barangay (2 commas)");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Database Insert
				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String sql = "INSERT INTO Warehouse (WarehouseID, WarehouseName, Address, City) VALUES (?, ?, ?, ?)";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(warehouseID));
					pstmt.setString(2, name);
					pstmt.setString(3, address);
					pstmt.setString(4, city);

					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Warehouse added successfully: " + name);
						dispose();
					} else {
						lblError.setText("Failed to add Warehouse");
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
package Frames.MonitorFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import Frames.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;

public class MonitorProductFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public MonitorProductFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 400);
		setTitle("Product Monitoring");
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

		// Warehouse List Section
		JLabel lblWarehouseList = new JLabel("Existing Warehouses:");
		lblWarehouseList.setBounds(20, 20, 200, 25);
		lblWarehouseList.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblWarehouseList);

		JTextArea warehouseListArea = new JTextArea();
		warehouseListArea.setEditable(false);
		warehouseListArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
		JScrollPane listScrollPane = new JScrollPane(warehouseListArea);
		listScrollPane.setBounds(20, 50, 645, 150);
		contentPane.add(listScrollPane);

		// Load warehouses on initialization
		loadWarehouseList(warehouseListArea);

		// Instructions
		JLabel lblInstructions = new JLabel("Fill in the required fields:");
		lblInstructions.setBounds(20, 220, 200, 25);
		lblInstructions.setFont(new java.awt.Font("Tahoma", java.awt.Font.ITALIC, 11));
		contentPane.add(lblInstructions);

		// Warehouse ID
		JLabel lblWarehouseID = new JLabel("Warehouse ID");
		lblWarehouseID.setBounds(20, 260, 250, 25);
		contentPane.add(lblWarehouseID);

		JTextField warehouseIDField = new JTextField();
		warehouseIDField.setBounds(120, 260, 250, 25);
		contentPane.add(warehouseIDField);
		warehouseIDField.setColumns(10);

		// Product ID
		JLabel lblProductID = new JLabel("Product ID");
		lblProductID.setBounds(20, 300, 250, 25);
		contentPane.add(lblProductID);

		JTextField productIDField = new JTextField();
		productIDField.setBounds(120, 300, 250, 25);
		contentPane.add(productIDField);
		productIDField.setColumns(10);

		// Submit Button
		JButton btnSubmit = new JButton("Monitor Products");
		btnSubmit.setBounds(400, 275, 200, 35);
		contentPane.add(btnSubmit);

		// Error/Info Label
		JLabel lblMessage = new JLabel("");
		lblMessage.setBounds(400, 275, 450, 100);
		contentPane.add(lblMessage);

		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblMessage.setText("");
				lblMessage.setVisible(false);

				String warehouseID = warehouseIDField.getText().trim();
				String productID = productIDField.getText().trim();

				// Basic Validation 
				if (warehouseID.isEmpty() || productID.isEmpty()) {
					lblMessage.setText("<html>Please fill in all required fields</html>");
					lblMessage.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Validate numeric fields
				try {
					int wareID = Integer.parseInt(warehouseID);
					int prodID = Integer.parseInt(productID);

					if (wareID < 1000 || wareID > 9999) {
						lblMessage.setText("Warehouse ID must be between 1000 - 9999");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}

					if (prodID < 100000 || prodID > 999999){
						lblMessage.setText("Product ID must be between 100000 - 999999");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}

				} catch (NumberFormatException ex) {
					lblMessage.setText("Invalid number format, check input");
					lblMessage.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				try {
					// Validate Warehouse exists
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String warehouseSQL = "SELECT WarehouseName, City FROM Warehouse WHERE WarehouseID = ?";
					PreparedStatement warehousePstmt = conn.prepareStatement(warehouseSQL);
					warehousePstmt.setInt(1, Integer.parseInt(warehouseID));
					ResultSet warehouseRs = warehousePstmt.executeQuery();
 
					if (!warehouseRs.next()) {
						lblMessage.setText("Error: Warehouse ID " + warehouseID + " does not exist");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						warehouseRs.close();
						warehousePstmt.close();
						return;
					}

					warehouseRs.close();
					warehousePstmt.close();

					// Validate Product exists and in specified Warehouse
					String productSQL = "SELECT ProductName FROM Product WHERE ProductID = ? AND WarehouseID = ?";
					PreparedStatement productPstmt = conn.prepareStatement(productSQL);
					productPstmt.setInt(1, Integer.parseInt(productID));
					productPstmt.setInt(2, Integer.parseInt(warehouseID));
					ResultSet productRs = productPstmt.executeQuery();

					if (!productRs.next()) {
						lblMessage.setText("Error: Product ID does not exist or not in specified Warehouse");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						productRs.close();
						productPstmt.close();
						return;
					}

					// Check for expiry
					String expirySQL = "SELECT DateOfExpiry FROM Product WHERE ProductID = ? AND WarehouseID = ?";
					PreparedStatement expiryPstmt = conn.prepareStatement(expirySQL);
					expiryPstmt.setInt(1, Integer.parseInt(productID));
					expiryPstmt.setInt(2, Integer.parseInt(warehouseID));
					ResultSet expiryRs = expiryPstmt.executeQuery();

					if(!expiryRs.next()){
						lblMessage.setText("Error: Product does not exist or not in specified Warehouse");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						productRs.close();
						productPstmt.close();
						return;
					}
					else{
						java.sql.Date sqlDate = expiryRs.getDate("DateOfExpiry");
						LocalDate expiryDate = sqlDate.toLocalDate();
						LocalDate currDate = LocalDate.now();
						if(expiryDate.isBefore(currDate)){
							// Insert Monitor Record
							String insertSQL = "INSERT INTO Monitor (WarehouseID, ProductID) VALUES (?, ?)";
							PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
							insertStmt.setInt(1, Integer.parseInt(warehouseID));
							insertStmt.setInt(2, Integer.parseInt(productID));
							insertStmt.executeUpdate();

							// Update Expired Products
							String updateSQL = "UPDATE Product SET Is_Expired = 1 WHERE ProductID = ? AND WarehouseID = ?";
							PreparedStatement updatePstmt = conn.prepareStatement(updateSQL);
							updatePstmt.setInt(1, Integer.parseInt(productID));
							updatePstmt.setInt(2, Integer.parseInt(warehouseID));
							updatePstmt.executeUpdate();

							System.out.println("Product successfully declared as expired: ID " + productID);
							lblMessage.setText("Updated product successfully!");
							lblMessage.setForeground(java.awt.Color.GREEN);
							lblMessage.setVisible(true);
						
							// Clear
							warehouseIDField.setText("");
							productIDField.setText("");
						}
						else{
							lblMessage.setText("Product is not yet expired");
							lblMessage.setVisible(true);
							contentPane.revalidate();
							contentPane.repaint();
							productRs.close();
							productPstmt.close();
							return;
						}
					}

				} catch (SQLException ex) {
					System.err.println("Database error: " + ex.getMessage());
					lblMessage.setText("<html>Database Error:<br/>" + ex.getMessage() + "</html>");
					lblMessage.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
				}
			}
		});
	}

    // Method to load and display warehouses (ID, Name and City)
	private void loadWarehouseList(JTextArea textArea) {
		try {
			DBManager dbManager = DBManager.getInstance();
			Connection conn = dbManager.getConnection();

			String sql = "SELECT WarehouseID, WarehouseName, City FROM Warehouse ORDER BY WarehouseID";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			StringBuilder listText = new StringBuilder();
			listText.append(String.format("%-8s %-20s %-10s\n", "ID", "Name", "City"));
			listText.append("────────────────────────────────────────────────────────────────────────\n");

			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				listText.append(String.format("%-8d %-20s %-10s\n",
					rs.getInt("WarehouseID"),
					rs.getString("WarehouseName"),
					rs.getString("City")));
			}

			if (!hasData) {
				listText.append("No warehouses found in database.\n");
			}

			textArea.setText(listText.toString());

			rs.close();
			pstmt.close();

		} catch (SQLException ex) {
			System.err.println("Database error: " + ex.getMessage());
			textArea.setText("Error loading warehouses: " + ex.getMessage());
		}
	}
}
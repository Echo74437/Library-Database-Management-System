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
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class WarehouseFrameRead extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public WarehouseFrameRead() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 700);
		setTitle("View Warehouse Records");
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
		listScrollPane.setBounds(20, 50, 840, 150);
		contentPane.add(listScrollPane);

		// Load warehouses on initialization
		loadWarehouseList(warehouseListArea);

		// Separator
		JLabel lblSeparator = new JLabel("View Details:");
		lblSeparator.setBounds(20, 220, 200, 25);
		lblSeparator.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblSeparator);

		JLabel lblWarehouseID = new JLabel("Enter Warehouse ID to View:");
		lblWarehouseID.setBounds(20, 260, 250, 25);
		contentPane.add(lblWarehouseID);

		JTextField warehouseIDField = new JTextField();
		warehouseIDField.setBounds(20, 290, 200, 25);
		contentPane.add(warehouseIDField);
		warehouseIDField.setColumns(10);

		JButton btnView = new JButton("View Details");
		btnView.setBounds(240, 290, 150, 25);
		btnView.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11));
		contentPane.add(btnView);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(20, 330, 840, 320);
		contentPane.add(scrollPane);

		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String warehouseID = warehouseIDField.getText().trim();
				textArea.setText("");

				if (warehouseID.isEmpty()) {
					textArea.setText("Please enter Warehouse ID");
					return;
				}

				// Validate ID
				try {
					int id = Integer.parseInt(warehouseID);
				if (id < 1000 || id > 9999) {
					textArea.setText("Warehouse ID must be between 1000 - 9999");
						return;
					}
				} catch (NumberFormatException ex) {
					textArea.setText("Warehouse ID must be a valid number");
					return;
				}

				// Database Query
				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					// Get Warehouse Basic Info
					String sql = "SELECT * FROM Warehouse WHERE WarehouseID = ?";
					PreparedStatement warePstmt = conn.prepareStatement(sql);
					warePstmt.setInt(1, Integer.parseInt(warehouseID));
					ResultSet wareRS = warePstmt.executeQuery();

					StringBuilder output = new StringBuilder();
					if(wareRS.next()){
						output.append("=== Warehouse RECORD ===\n\n");
						output.append("Warehouse ID: ").append(wareRS.getInt("WarehouseID")).append("\n");
						output.append("Name: ").append(wareRS.getString("WarehouseName")).append("\n");
						output.append("Address: ").append(wareRS.getString("Address")).append("\n");
						output.append("City: ").append(wareRS.getString("City")).append("\n");
						output.append("========================\n\n");
					} else{
						textArea.setText("Warehouse ID not found: " + warehouseID);
						return;
					}

					wareRS.close();
					warePstmt.close();

					// Get Product Info
					String prodSql = "SELECT * FROM Product WHERE WarehouseID = ?";
					PreparedStatement prodPstmt = conn.prepareStatement(prodSql);
					prodPstmt.setInt(1, Integer.parseInt(warehouseID));
					ResultSet prodRS = prodPstmt.executeQuery();

					output.append("=== Products in Warehouse ===\n\n");
					output.append(String.format("%-15s %-55s %-15s %-15s\n", "Product ID", "Product Name", "Quantity", "Date of Expiry"));
					output.append("──────────────────────────────────────────────────────────────────────────────\n");

					boolean hasData = false;
					while(prodRS.next()){
						hasData = true;
						String prodDate;
						java.sql.Date date = prodRS.getDate("DateOfExpiry");
						if(date != null){
							prodDate = date.toString();
						} else{
							prodDate = "None";
						}

						output.append(String.format("%-15d %-60s %-15d %-15s\n",
						prodRS.getInt("ProductID"),
						prodRS.getString("ProductName"),
						prodRS.getInt("Quantity"),
						prodDate));
					}

					if(!hasData){
						output.append("No Products in Warehouse\n");
					}

					textArea.setText(output.toString());

					prodRS.close();
					prodPstmt.close();
				} catch (SQLException ex) {
					System.err.println("Database error: " + ex.getMessage());
					textArea.setText("Database Error: " + ex.getMessage());
				}
			}
		});
	}

    // Method to load and display warehouses (ID, Name and City only)
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
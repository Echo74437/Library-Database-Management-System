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
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class BeneficiaryFrameView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public BeneficiaryFrameView() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 700);
		setTitle("View Beneficiary Records");
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
		beneficiaryListArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
		JScrollPane listScrollPane = new JScrollPane(beneficiaryListArea);
		listScrollPane.setBounds(20, 50, 840, 150);
		contentPane.add(listScrollPane);

		// Load beneficiaries on initialization
		loadBeneficiaryList(beneficiaryListArea);

		JLabel lblSeparator = new JLabel("View Details:");
		lblSeparator.setBounds(20, 220, 200, 25);
		lblSeparator.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblSeparator);

		JLabel lblBeneficiaryID = new JLabel("Enter Beneficiary ID to View:");
		lblBeneficiaryID.setBounds(20, 260, 250, 25);
		contentPane.add(lblBeneficiaryID);

		JTextField beneficiaryIDField = new JTextField();
		beneficiaryIDField.setBounds(20, 290, 200, 25);
		contentPane.add(beneficiaryIDField);
		beneficiaryIDField.setColumns(10);

		JButton btnView = new JButton("View Details");
		btnView.setBounds(240, 290, 150, 25);
		btnView.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11));
		contentPane.add(btnView);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(20, 330, 840, 320);
		contentPane.add(scrollPane);

		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String beneficiaryID = beneficiaryIDField.getText().trim();
				textArea.setText("");

				if (beneficiaryID.isEmpty()) {
					textArea.setText("Please enter Beneficiary ID");
					return;
				}

				try {
					int id = Integer.parseInt(beneficiaryID);
				if (id < 100000 || id > 999999) {
					textArea.setText("Beneficiary ID must be between 100000-999999");
						return;
					}
				} catch (NumberFormatException ex) {
					textArea.setText("Beneficiary ID must be a valid number");
					return;
				}

				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String sql = "SELECT * FROM Beneficiaries WHERE BeneficiaryID = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(beneficiaryID));
					ResultSet rs = pstmt.executeQuery();

					if (rs.next()) {
						StringBuilder output = new StringBuilder();
						output.append("=== BENEFICIARY RECORD ===\n\n");
						output.append("Beneficiary ID: ").append(rs.getInt("BeneficiaryID")).append("\n");
						output.append("Name: ").append(rs.getString("BeneficiaryName")).append("\n");
						output.append("Address: ").append(rs.getString("Address")).append("\n");
						output.append("City: ").append(rs.getString("City")).append("\n");
						output.append("Contact Number: ").append(rs.getString("ContactNumber")).append("\n");
						output.append("Status: ").append(rs.getString("Status")).append("\n\n");

						output.append("=== RELATED RECORDS ===\n\n");
						
						// Products Requested
						output.append("Products Requested:\n");
						String productsSQL = "SELECT br.RequestID, br.Quantity, br.RequestDate, br.Status, p.ProductName " +
											 "FROM BeneficiaryRequests br " +
											 "JOIN Product p ON br.ProductID = p.ProductID " +
											 "WHERE br.BeneficiaryID = ? " +
											 "ORDER BY br.RequestDate DESC";
						PreparedStatement productsPstmt = conn.prepareStatement(productsSQL);
						productsPstmt.setInt(1, Integer.parseInt(beneficiaryID));
						ResultSet productsRs = productsPstmt.executeQuery();
						
						boolean hasProducts = false;
						StringBuilder productsOutput = new StringBuilder();
						productsOutput.append(String.format("%-12s %-30s %-10s %-15s %-12s\n", 
							"Request ID", "Product Name", "Quantity", "Request Date", "Status"));
						productsOutput.append("────────────────────────────────────────────────────────────────────────────────────────────────\n");
						
						while (productsRs.next()) {
							hasProducts = true;
							productsOutput.append(String.format("%-12d %-30s %-10d %-15s %-12s\n",
								productsRs.getInt("RequestID"),
								productsRs.getString("ProductName"),
								productsRs.getInt("Quantity"),
								productsRs.getString("RequestDate").substring(0, 10),
								productsRs.getString("Status")));
						}
						
						if (hasProducts) {
							output.append(productsOutput.toString());
						} else {
							output.append("No product requests found for this beneficiary.\n");
						}
						productsRs.close();
						productsPstmt.close();
						
						output.append("\n");

						// Warehouses That Supplied
						output.append("Warehouses That Supplied:\n");
						String warehousesSQL = "SELECT DISTINCT w.WarehouseID, w.WarehouseName, w.City " +
											   "FROM BeneficiaryRequests br " +
											   "JOIN Warehouse w ON br.WarehouseID = w.WarehouseID " +
											   "WHERE br.BeneficiaryID = ?";
						PreparedStatement warehousesPstmt = conn.prepareStatement(warehousesSQL);
						warehousesPstmt.setInt(1, Integer.parseInt(beneficiaryID));
						ResultSet warehousesRs = warehousesPstmt.executeQuery();
						
						boolean hasWarehouses = false;
						StringBuilder warehousesOutput = new StringBuilder();
						warehousesOutput.append(String.format("%-15s %-35s %-15s\n", 
							"Warehouse ID", "Warehouse Name", "City"));
						warehousesOutput.append("────────────────────────────────────────────────────────────────────────────────────────────────\n");
						
						while (warehousesRs.next()) {
							hasWarehouses = true;
							warehousesOutput.append(String.format("%-15d %-35s %-15s\n",
								warehousesRs.getInt("WarehouseID"),
								warehousesRs.getString("WarehouseName"),
								warehousesRs.getString("City")));
						}
						
						if (hasWarehouses) {
							output.append(warehousesOutput.toString());
						} else {
							output.append("No warehouse records found for this beneficiary.\n");
						}
						warehousesRs.close();
						warehousesPstmt.close();
						
						output.append("\n");

						// Couriers Who Delivered (if delivery records exist)
						output.append("Couriers Who Delivered:\n");
						String couriersSQL = "SELECT DISTINCT c.Cour_ID, c.FirstName, c.LastName, c.VehicleType " +
											 "FROM Delivery d " +
											 "JOIN Courier c ON d.CourierID = c.Cour_ID " +
											 "WHERE d.BeneficiaryID = ?";
						PreparedStatement couriersPstmt = conn.prepareStatement(couriersSQL);
						couriersPstmt.setInt(1, Integer.parseInt(beneficiaryID));
						ResultSet couriersRs = couriersPstmt.executeQuery();
						
						boolean hasCouriers = false;
						StringBuilder couriersOutput = new StringBuilder();
						couriersOutput.append(String.format("%-12s %-15s %-15s %-15s\n", 
							"Courier ID", "First Name", "Last Name", "Vehicle Type"));
						couriersOutput.append("────────────────────────────────────────────────────────────────────────────────────────────────\n");
						
						while (couriersRs.next()) {
							hasCouriers = true;
							couriersOutput.append(String.format("%-12d %-15s %-15s %-15s\n",
								couriersRs.getInt("Cour_ID"),
								couriersRs.getString("FirstName"),
								couriersRs.getString("LastName"),
								couriersRs.getString("VehicleType")));
						}
						
						if (hasCouriers) {
							output.append(couriersOutput.toString());
						} else {
							output.append("No delivery records found for this beneficiary.\n");
						}
						couriersRs.close();
						couriersPstmt.close();

						textArea.setText(output.toString());
					} else {
						textArea.setText("Beneficiary ID not found: " + beneficiaryID);
					}

					rs.close();
					pstmt.close();

				} catch (SQLException ex) {
					System.err.println("Database error: " + ex.getMessage());
					textArea.setText("Database Error: " + ex.getMessage());
				}
			}
		});
	}

	// Method to load and display beneficiaries (ID, Name, and Status)
	private void loadBeneficiaryList(JTextArea textArea) {
		try {
			DBManager dbManager = DBManager.getInstance();
			Connection conn = dbManager.getConnection();

			String sql = "SELECT BeneficiaryID, BeneficiaryName, Status FROM Beneficiaries ORDER BY BeneficiaryID";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			StringBuilder listText = new StringBuilder();
			listText.append(String.format("%-8s %-30s %-10s\n", "ID", "Name", "Status"));
			listText.append("────────────────────────────────────────────────────────────\n");

			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				listText.append(String.format("%-8d %-30s %-10s\n",
					rs.getInt("BeneficiaryID"),
					rs.getString("BeneficiaryName"),
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
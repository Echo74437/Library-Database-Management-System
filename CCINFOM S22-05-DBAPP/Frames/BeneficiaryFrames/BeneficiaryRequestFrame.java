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

public class BeneficiaryRequestFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public BeneficiaryRequestFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 480);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

		JLabel lblTitle = new JLabel("Beneficiary Request for Products");
		lblTitle.setBounds(120, 10, 300, 25);
		lblTitle.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 14));
		contentPane.add(lblTitle);

		// Instructions
		JLabel lblInstructions = new JLabel("Fill in required fields to request products from warehouse");
		lblInstructions.setBounds(10, 30, 400, 15);
		lblInstructions.setFont(new java.awt.Font("Tahoma", java.awt.Font.ITALIC, 11));
		contentPane.add(lblInstructions);

		// Beneficiary ID
		JLabel lblBeneficiaryID = new JLabel("Beneficiary ID (Required)");
		lblBeneficiaryID.setBounds(10, 60, 200, 20);
		contentPane.add(lblBeneficiaryID);

		JTextField beneficiaryIDField = new JTextField();
		beneficiaryIDField.setBounds(280, 60, 150, 20);
		contentPane.add(beneficiaryIDField);
		beneficiaryIDField.setColumns(10);

		// Warehouse ID
		JLabel lblWarehouseID = new JLabel("Warehouse ID (Required)");
		lblWarehouseID.setBounds(10, 100, 200, 20);
		contentPane.add(lblWarehouseID);

		JTextField warehouseIDField = new JTextField();
		warehouseIDField.setBounds(280, 100, 150, 20);
		contentPane.add(warehouseIDField);
		warehouseIDField.setColumns(10);

		// Product ID
		JLabel lblProductID = new JLabel("Product ID (Required)");
		lblProductID.setBounds(10, 140, 200, 20);
		contentPane.add(lblProductID);

		JTextField productIDField = new JTextField();
		productIDField.setBounds(280, 140, 150, 20);
		contentPane.add(productIDField);
		productIDField.setColumns(10);

		// Quantity
		JLabel lblQuantity = new JLabel("Quantity Requested (Required)");
		lblQuantity.setBounds(10, 180, 200, 20);
		contentPane.add(lblQuantity);

		JTextField quantityField = new JTextField();
		quantityField.setBounds(280, 180, 150, 20);
		contentPane.add(quantityField);
		quantityField.setColumns(10);

		// Request Date
		JLabel lblRequestDate = new JLabel("Request Date (Auto-generated)");
		lblRequestDate.setBounds(10, 220, 300, 20);
		contentPane.add(lblRequestDate);

		// Sample data
		JLabel lblHint = new JLabel("<html><u>Sample Valid IDs:</u> Beneficiary: 100001-100015, Warehouse: 101-105, Product: 100001-100026</html>");
		lblHint.setBounds(10, 240, 450, 25);
		lblHint.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 10));
		contentPane.add(lblHint);

		// Left side buttons
		JButton btnShowWarehouses = new JButton("Show Warehouses");
		btnShowWarehouses.setBounds(50, 280, 150, 30);
		btnShowWarehouses.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 10));
		contentPane.add(btnShowWarehouses);

		JButton btnShowProducts = new JButton("Show Products");
		btnShowProducts.setBounds(50, 320, 150, 30);
		btnShowProducts.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 10));
		contentPane.add(btnShowProducts);

		// Submit Button (Right side)
		JButton btnSubmit = new JButton("Submit Request");
		btnSubmit.setBounds(280, 300, 150, 35);
		contentPane.add(btnSubmit);

		// Error/Info Label
		JLabel lblMessage = new JLabel("");
		lblMessage.setBounds(20, 360, 450, 100);
		contentPane.add(lblMessage);

		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblMessage.setText("");
				lblMessage.setVisible(false);

				String beneficiaryID = beneficiaryIDField.getText().trim();
				String warehouseID = warehouseIDField.getText().trim();
				String productID = productIDField.getText().trim();
				String quantity = quantityField.getText().trim();

				if (beneficiaryID.isEmpty() || warehouseID.isEmpty() || productID.isEmpty() || quantity.isEmpty()) {
					lblMessage.setText("<html>Please fill in required fields:<br/>Beneficiary ID, Warehouse ID, Product ID, Quantity</html>");
					lblMessage.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				try {
					int benID = Integer.parseInt(beneficiaryID);
					int qty = Integer.parseInt(quantity);
					Integer.parseInt(warehouseID);

				if (benID < 100000 || benID > 999999) {
					lblMessage.setText("Beneficiary ID must be between 100000-999999");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}

					if (qty <= 0) {
						lblMessage.setText("Quantity must be greater than 0");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}

				} catch (NumberFormatException ex) {
					lblMessage.setText("Invalid number format in ID or Quantity fields");
					lblMessage.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Verify Beneficiary is Active
				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String checkBeneficiary = "SELECT Status, City FROM Beneficiaries WHERE BeneficiaryID = ?";
					PreparedStatement pstmt = conn.prepareStatement(checkBeneficiary);
					pstmt.setInt(1, Integer.parseInt(beneficiaryID));
					ResultSet rs = pstmt.executeQuery();

					if (!rs.next()) {
						lblMessage.setText("Beneficiary ID not found");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						rs.close();
						pstmt.close();
						return;
					}

					String beneficiaryStatus = rs.getString("Status");
					String beneficiaryCity = rs.getString("City");

					if (!beneficiaryStatus.equals("Active")) {
						lblMessage.setText("Beneficiary must be Active to make requests");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						rs.close();
						pstmt.close();
						return;
					}

					rs.close();
					pstmt.close();

					// Validate Warehouse exists
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

					String warehouseName = warehouseRs.getString("WarehouseName");
					String warehouseCity = warehouseRs.getString("City");
					warehouseRs.close();
					warehousePstmt.close();

					// Validate that beneficiary and warehouse are in the same city
					if (!beneficiaryCity.equals(warehouseCity)) {
						lblMessage.setText("<html>Error: City mismatch!<br/>Beneficiary: " + beneficiaryCity + "<br/>Warehouse: " + warehouseCity + "<br/>Both must be in the same city.</html>");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}

					// Validate Product exists
					String productSQL = "SELECT ProductName, CategoryID FROM Product WHERE ProductID = ?";
					PreparedStatement productPstmt = conn.prepareStatement(productSQL);
					productPstmt.setInt(1, Integer.parseInt(productID));
					ResultSet productRs = productPstmt.executeQuery();

					if (!productRs.next()) {
						lblMessage.setText("Error: Product ID " + productID + " does not exist");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						productRs.close();
						productPstmt.close();
						return;
					}

					String productName = productRs.getString("ProductName");
					productRs.close();
					productPstmt.close();

					// Insert Request
					String insertSQL = "INSERT INTO BeneficiaryRequests (BeneficiaryID, WarehouseID, ProductID, Quantity, RequestDate, Status) VALUES (?, ?, ?, ?, NOW(), 'Pending')";
					PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
					insertStmt.setInt(1, Integer.parseInt(beneficiaryID));
					insertStmt.setInt(2, Integer.parseInt(warehouseID));
					insertStmt.setInt(3, Integer.parseInt(productID));
					insertStmt.setInt(4, Integer.parseInt(quantity));

					int rowsAffected = insertStmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Request submitted successfully for Beneficiary ID: " + beneficiaryID);
						lblMessage.setText("<html><b>Request submitted successfully!</b><br/>Beneficiary: " + beneficiaryID + "<br/>Warehouse: " + warehouseName + " (" + warehouseCity + ")<br/>Product: " + productName + "<br/>Quantity: " + quantity + "<br/>Status: Pending (awaiting approval)</html>");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						
						beneficiaryIDField.setText("");
						warehouseIDField.setText("");
						productIDField.setText("");
						quantityField.setText("");
					} else {
						lblMessage.setText("Failed to submit request");
						lblMessage.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
					}

					insertStmt.close();

				} catch (SQLException ex) {
					System.err.println("Database error: " + ex.getMessage());
					lblMessage.setText("<html>Database Error:<br/>" + ex.getMessage() + "</html>");
					lblMessage.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
				}
			}
		});

		// Show Warehouses Button Action
		btnShowWarehouses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showWarehouseList();
			}
		});

		// Show Products Button Action
		btnShowProducts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showProductList();
			}
		});
	}

	// Show warehouses
	private void showWarehouseList() {
		try {
			DBManager dbManager = DBManager.getInstance();
			Connection conn = dbManager.getConnection();

			String sql = "SELECT WarehouseID, WarehouseName, City FROM Warehouse ORDER BY WarehouseID";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			StringBuilder warehouseInfo = new StringBuilder();
			warehouseInfo.append("=== AVAILABLE WAREHOUSES ===\n\n");
			warehouseInfo.append(String.format("%-12s %-25s %-15s\n", "Warehouse ID", "Warehouse Name", "City"));
			warehouseInfo.append("────────────────────────────────────────────────────────────\n");

			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				warehouseInfo.append(String.format("%-12d %-25s %-15s\n",
					rs.getInt("WarehouseID"),
					rs.getString("WarehouseName"),
					rs.getString("City")));
			}

			if (!hasData) {
				warehouseInfo.append("No warehouses found in database.\n");
			}

			// Create and display popup window
			javax.swing.JFrame warehouseFrame = new javax.swing.JFrame("Available Warehouses");
			warehouseFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
			warehouseFrame.setBounds(150, 150, 600, 400);
			warehouseFrame.setAlwaysOnTop(true);

			javax.swing.JTextArea textArea = new javax.swing.JTextArea();
			textArea.setEditable(false);
			textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
			textArea.setText(warehouseInfo.toString());

			javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
			warehouseFrame.add(scrollPane);
			warehouseFrame.setVisible(true);

			rs.close();
			pstmt.close();

		} catch (SQLException ex) {
			System.err.println("Database error: " + ex.getMessage());
			javax.swing.JOptionPane.showMessageDialog(this, "Error loading warehouses: " + ex.getMessage(), "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}

	// Show products
	private void showProductList() {
		try {
			DBManager dbManager = DBManager.getInstance();
			Connection conn = dbManager.getConnection();

			String sql = "SELECT p.ProductID, p.ProductName, pc.CategoryName " +
						 "FROM Product p " +
						 "JOIN ProductCategory pc ON p.CategoryID = pc.CategoryID " +
						 "ORDER BY p.ProductID";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			StringBuilder productInfo = new StringBuilder();
			productInfo.append("=== AVAILABLE PRODUCTS ===\n\n");
			productInfo.append(String.format("%-12s %-35s %-20s\n", "Product ID", "Product Name", "Category"));
			productInfo.append("───────────────────────────────────────────────────────────────────────────────\n");

			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				productInfo.append(String.format("%-12d %-35s %-20s\n",
					rs.getInt("ProductID"),
					rs.getString("ProductName"),
					rs.getString("CategoryName")));
			}

			if (!hasData) {
				productInfo.append("No products found in database.\n");
			}

			// Create and display popup window
			javax.swing.JFrame productFrame = new javax.swing.JFrame("Available Products");
			productFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
			productFrame.setBounds(200, 200, 700, 500);
			productFrame.setAlwaysOnTop(true);

			javax.swing.JTextArea textArea = new javax.swing.JTextArea();
			textArea.setEditable(false);
			textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
			textArea.setText(productInfo.toString());

			javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
			productFrame.add(scrollPane);
			productFrame.setVisible(true);

			rs.close();
			pstmt.close();

		} catch (SQLException ex) {
			System.err.println("Database error: " + ex.getMessage());
			javax.swing.JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage(), "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}
}
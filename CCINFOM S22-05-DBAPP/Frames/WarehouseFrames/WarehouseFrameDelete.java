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

public class WarehouseFrameDelete extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public WarehouseFrameDelete() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 450);
		setTitle("Delete Warehouse");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);
		setVisible(true);

		// Warehouse List Section
		JLabel lblWarehouseList = new JLabel("Existing Warehouses:");
		lblWarehouseList.setBounds(20, 20, 200, 25);
		lblWarehouseList.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblWarehouseList);

		JTextArea warehouseListArea = new JTextArea();
		warehouseListArea.setEditable(false);
		warehouseListArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(warehouseListArea);
		scrollPane.setBounds(20, 50, 540, 150);
		contentPane.add(scrollPane);

		// Load Warehouses on initialization
		loadWarehouseList(warehouseListArea);

		// Separator
		JLabel lblSeparator = new JLabel("Delete Form:");
		lblSeparator.setBounds(20, 220, 200, 25);
		lblSeparator.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblSeparator);

		JLabel lblWarehouseID = new JLabel("Warehouse ID to Delete:");
		lblWarehouseID.setBounds(20, 260, 200, 25);
		contentPane.add(lblWarehouseID);

		JTextField warehouseIDField = new JTextField();
		warehouseIDField.setBounds(20, 290, 200, 25);
		contentPane.add(warehouseIDField);
		warehouseIDField.setColumns(10);

		JButton btnDelete = new JButton("Delete Warehouse");
		btnDelete.setBounds(180, 340, 200, 35);
		btnDelete.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		btnDelete.setBackground(new java.awt.Color(220, 53, 69));
		btnDelete.setForeground(java.awt.Color.WHITE);
		contentPane.add(btnDelete);

		// Error Label
		JLabel lblError = new JLabel("");
		lblError.setBounds(20, 380, 540, 30);
		lblError.setForeground(java.awt.Color.RED);
		contentPane.add(lblError);

		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblError.setText("");
				lblError.setVisible(false);

				String warehouseID = warehouseIDField.getText().trim();

				if (warehouseID.isEmpty()) {
					lblError.setText("Please enter Warehouse ID");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Validate ID
				try {
					int id = Integer.parseInt(warehouseID);
				if (id < 1000 || id > 9999) {
					lblError.setText("ID must be between 1000 - 9999");
						lblError.setVisible(true);
						contentPane.revalidate();
						contentPane.repaint();
						return;
					}
				} catch (NumberFormatException ex) {
					lblError.setText("ID must be a valid number");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				// Database Delete
				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String sql = "DELETE FROM Warehouse WHERE WarehouseID = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(warehouseID));

					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Warehouse deleted successfully: ID " + warehouseID);
						lblError.setText("Warehouse deleted successfully!");
						lblError.setForeground(java.awt.Color.GREEN);
						lblError.setVisible(true);
						// Refresh the beneficiary list
						loadWarehouseList(warehouseListArea);
						// Clear form field
						warehouseIDField.setText("");
					} else {
						lblError.setText("Warehouse ID was not deleted (ID not found)");
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
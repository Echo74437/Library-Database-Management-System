package Frames.DeliveryFrames;

import Frames.DBManager;
import Frames.MainFrame;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class DeliveryFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JTextField allocField;
	private JTextField courField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeliveryFrame frame = new DeliveryFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DeliveryFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setSize(1400,800);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 88, 457, 599);
		contentPane.add(scrollPane);

		table = new JTable();
		table.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"AllocationID", "Warehouse Name", "Beneficiary", "Date of Request"
				}
		) {
			boolean[] columnEditables = new boolean[] {
					false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		scrollPane.setViewportView(table);

		allocField = new JTextField();
		allocField.setBounds(495, 194, 138, 27);
		contentPane.add(allocField);
		allocField.setColumns(10);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(378, 54, 89, 23);
		contentPane.add(btnRefresh);

		JLabel lblAllocID = new JLabel("Enter Allocation ID to Deliver");
		lblAllocID.setBounds(495, 169, 168, 23);
		contentPane.add(lblAllocID);

		JLabel lblCourID = new JLabel("Enter Courier ID to Assign");
		lblCourID.setBounds(495, 242, 168, 23);
		contentPane.add(lblCourID);

		courField = new JTextField();
		courField.setColumns(10);
		courField.setBounds(495, 267, 138, 27);
		contentPane.add(courField);

		JButton btnComplete = new JButton("Complete Transaction");
		btnComplete.setBounds(480, 327, 168, 33);
		contentPane.add(btnComplete);

		JButton btnBack = new JButton("Go Back to Main Menu\r\n");
		btnBack.setBounds(1077, 12, 297, 70);
		contentPane.add(btnBack);

		JLabel lblError = new JLabel("Courier ID is Invalid ");
		lblError.setBounds(505, 387, 168, 43);
		lblError.setVisible(false);
		contentPane.add(lblError);
		JLabel lblErrorCity = new JLabel("Courier cannot deliver outside of their city");
		lblError.setBounds(505, 407, 168, 43);
		lblError.setVisible(false);
		contentPane.add(lblError);

		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					PreparedStatement stmt = conn.prepareStatement(
							"SELECT AllocationID, BeneficiaryName, WarehouseName, RequestDate FROM Allocation a " +
									"JOIN BeneficiaryRequests br ON br.RequestID = a.RequestID " +
									"JOIN Beneficiaries b ON br.BeneficiaryID = b.BeneficiaryID " +
									"JOIN Warehouse w ON br.WarehouseID = w.WarehouseID " +
									"WHERE br.Status = 'Pending';");



					ResultSet rs = stmt.executeQuery();
					tableModel.setRowCount(0);


					ResultSetMetaData metaData = rs.getMetaData();
					int columnCount = metaData.getColumnCount();


					while(rs.next()){

						Object[] rowData = new Object[columnCount];
						for (int i = 1; i <= columnCount; i++) {

							rowData[i - 1] = rs.getObject(i);
						}

						tableModel.addRow(rowData);
					}



					stmt.close();

					tableModel.fireTableDataChanged();


				} catch (SQLException ex) {
					System.err.println("Failed to establish connection: " + ex.getMessage());
				}
			}
		});

		btnComplete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {


				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();
					String benefID = null;
					String wareID = null;
					boolean is_Complete = false;

					PreparedStatement stmt = conn.prepareStatement(
							"SELECT City FROM Courier WHERE Cour_ID = ? AND Status = 'Available'");

					stmt.setString(1, courField.getText());

					ResultSet rs = stmt.executeQuery();

					PreparedStatement stmt2 = conn.prepareStatement(
							"SELECT WarehouseID, BeneficiaryID FROM Allocation a JOIN BeneficiaryRequests r " +
									"ON a.RequestID = r.RequestID WHERE a.AllocationID = ?");

					stmt2.setString(1, allocField.getText());
					try{

						ResultSet rs2 = stmt2.executeQuery();
						if (rs2.next()) { // get Beneficiary ID and warehouseID
							benefID = rs2.getString("BeneficiaryID");
							wareID = rs2.getString("WarehouseID");
						}
						rs2.close();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}



					if (rs.next() && wareID != null && benefID != null){ // courier ID exists in db validation

						String courCity = rs.getString("City");
						String wareCity = getCity(conn, "Warehouse", "WarehouseID", wareID);
						String benefCity = getCity(conn, "Beneficiaries", "BeneficiaryID", benefID);


						if (courCity.equalsIgnoreCase(wareCity) && courCity.equalsIgnoreCase(benefCity) ) {
							// if courier is same city as warehouse and beneficiary, delivery can go through
							// record transaction via insert <<--- start here
							// Assuming 'conn' is your active connection


							try (PreparedStatement stmtProd = conn.prepareStatement( // get products
									"SELECT r.ProductID, r.Quantity " +
											"FROM Allocation a JOIN BeneficiaryRequests r ON a.RequestID = r.RequestID " +
											"WHERE a.AllocationID = ?;")) {

								stmtProd.setString(1, allocField.getText());

								// insert statement into delivery
								try (PreparedStatement stmt3 = conn.prepareStatement(
										"INSERT INTO Delivery (BeneficiaryID, WarehouseID, CourierID, ProductID, Quantity, AllocationID) " +
												"VALUES (?, ?, ?, ?, ?, ?)")) {

									// Update products quantity as it loops through the products for recording
									try (PreparedStatement stmtUpdateProduct = conn.prepareStatement(
											"UPDATE Product SET Quantity = Quantity - ? WHERE ProductID = ? AND WarehouseID = ?")) {

										// execute
										try (ResultSet productsRs = stmtProd.executeQuery()) {

											String courierID = courField.getText();
											String beneficiaryID = benefID;
											String warehouseID = wareID;
											String allocationID = allocField.getText();

											while (productsRs.next()) {

												String productID = productsRs.getString("ProductID");
												String quantityDelivered = productsRs.getString("Quantity");

												// INSERT into Delivery
												stmt3.setString(1, beneficiaryID);
												stmt3.setString(2, warehouseID);
												stmt3.setString(3, courierID);
												stmt3.setString(4, productID);
												stmt3.setString(5, quantityDelivered);
												stmt3.setString(6, allocationID);
												stmt3.executeUpdate();

												//UPDATE Product Inventory


												stmtUpdateProduct.setString(1, quantityDelivered);
												stmtUpdateProduct.setString(2, productID);
												stmtUpdateProduct.setString(3, warehouseID);
												stmtUpdateProduct.executeUpdate();

												PreparedStatement stmtUpd = conn.prepareStatement(
														"UPDATE BeneficiaryRequests SET Status = 'Approved' WHERE ProductID = ? AND Quantity = ? AND WarehouseID = ? AND BeneficiaryID = ?");
												stmtUpd.setString(1, productID);
												stmtUpd.setString(2, quantityDelivered);
												stmtUpd.setString(3, warehouseID);
												stmtUpd.setString(4, beneficiaryID);


												stmtUpd.executeUpdate();
											}
											is_Complete = true;
											PreparedStatement stmtUpd = conn.prepareStatement(
													"UPDATE Courier SET Status = 'Unavailable' WHERE Cour_ID = ?");

											stmtUpd.setString(1, courField.getText());
											stmtUpd.execute();



										} // productsRs closes here
									} // stmtUpdateProduct closes here
								} // stmt3 closes here
							} // stmtProd closes here
							catch (SQLException ex) {
								// Handle exceptions
								ex.printStackTrace();
							}

							// update other records









						}else{
							lblErrorCity.setVisible(true);
						}

					}else{
						lblError.setVisible(true);
					}
					stmt.close();
					stmt2.close();
					rs.close();

					if (is_Complete){

						JOptionPane.showMessageDialog(
								null, // or the parent JFrame instance, e.g., 'YourMainFrame.this'
								"Delivery Transaction Complete!\nInventory for Allocation ID " + allocField.getText() + " has been successfully updated.",
								"Transaction Success",
								JOptionPane.INFORMATION_MESSAGE);


					}



				} catch (SQLException ex) {
					System.err.println("Failed to establish connection: " + ex.getMessage());
				}



			}
		});
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame main = new MainFrame();
				dispose();
			}
		});




	}
	// Helper method to retrieve City from any table
	private String getCity(Connection conn, String tableName, String idColumnName, String idValue) throws SQLException {
		String sql = "SELECT City FROM " + tableName + " WHERE " + idColumnName + " = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, idValue);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("City");
				}
				return null; // ID not found
			}
		}
	}
}



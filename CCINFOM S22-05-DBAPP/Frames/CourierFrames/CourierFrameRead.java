package Frames.CourierFrames;

import Frames.DBManager;
import Frames.MainFrame;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class CourierFrameRead extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JTextField idField;

	/**
	 * Launch the application.
	 */

	public CourierFrameRead() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setVisible(true);
		setSize(1400,800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnGoBackTo = new JButton("Go Back to Main Menu\r\n");
		btnGoBackTo.setBounds(1067, 72, 309, 70);
		contentPane.add(btnGoBackTo);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 202, 1366, 476);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Courier ID", "Last Name", "First Name", "Warehouse Name", "Beneficiary Name", "Product Name"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		table.getColumnModel().getColumn(4).setResizable(false);
		table.getColumnModel().getColumn(5).setResizable(false);
		scrollPane.setViewportView(table);

		JLabel lblID = new JLabel();
		lblID.setBounds(10,30, 160, 34);
		lblID.setText("Enter Courier ID to view");
		contentPane.add(lblID);

		idField = new JTextField();
		idField.setBounds(10, 72, 158, 34);
		contentPane.add(idField);
		idField.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(10, 137, 113, 28);
		contentPane.add(btnSearch);


		DefaultTableModel tm = (DefaultTableModel) table.getModel();

		btnGoBackTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame main = new MainFrame();
				dispose();
			}
		});

		btnSearch.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					// select courier id, lastname, firstname,availability warehouse name, beneficiary name, product name
					PreparedStatement stmt = conn.prepareStatement(
							"SELECT cour_ID, LastName, FirstName,WarehouseName, BeneficiaryName, ProductName FROM Courier c " +
									"JOIN Warehouse w ON c.City = w.City " +
									"JOIN Beneficiaries b ON c.City = b.City " +
									"CROSS JOIN Product " +
									"WHERE cour_ID = ? " +
									"AND b.City = w.City " +
									"AND c.City = b.City " +
									"AND c.Status = 'Available';");

					stmt.setString(1, idField.getText());
					tm.setRowCount(0);


					ResultSet rs = stmt.executeQuery(); //"Courier ID", "Last Name", "First Name", "Warehouse Name", "Beneficiary Name", "Product Name"
					String CourierID, LastName, FirstName, WarehouseName, BeneficiaryName, ProductName;
					while (rs.next()) {
						CourierID = rs.getString("Cour_ID");
						LastName = rs.getString("LastName");
						FirstName = rs.getString("FirstName");
						WarehouseName = rs.getString("WarehouseName");
						BeneficiaryName = rs.getString("BeneficiaryName");
						ProductName = rs.getString("ProductName");
						String[] row = {CourierID, LastName, FirstName, WarehouseName, BeneficiaryName, ProductName};
						tm.addRow(row);
					}


					stmt.close();
					tm.fireTableDataChanged();

					if (tm.getRowCount() == 0){
						JOptionPane.showMessageDialog(
								null,
								"No records were returned for the provided Courier ID or criteria.\nPlease check the ID and ensure the Courier is available or that there is sufficient data in the records.",
								"No Records Found",
								JOptionPane.ERROR_MESSAGE
						);
					}

				} catch (SQLException ex) {
					System.err.println("Failed to establish connection: " + ex.getMessage());
				}
			}
		});

	}
}

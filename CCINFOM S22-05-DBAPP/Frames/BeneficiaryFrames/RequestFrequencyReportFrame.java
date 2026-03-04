package Frames.BeneficiaryFrames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;

public class RequestFrequencyReportFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;

	public RequestFrequencyReportFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 700);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

		JLabel lblTitle = new JLabel("Request Frequency Report");
		lblTitle.setBounds(300, 10, 300, 25);
		lblTitle.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 16));
		contentPane.add(lblTitle);

		// Date Range Filters
		JLabel lblStartDate = new JLabel("Start Date (YYYY-MM-DD or YYYY):");
		lblStartDate.setBounds(10, 50, 200, 20);
		contentPane.add(lblStartDate);

		JTextField startDateField = new JTextField();
		startDateField.setBounds(220, 50, 150, 20);
		contentPane.add(startDateField);
		startDateField.setColumns(10);

		JLabel lblEndDate = new JLabel("End Date (YYYY-MM-DD or YYYY):");
		lblEndDate.setBounds(400, 50, 200, 20);
		contentPane.add(lblEndDate);

		JTextField endDateField = new JTextField();
		endDateField.setBounds(610, 50, 150, 20);
		contentPane.add(endDateField);
		endDateField.setColumns(10);

		// Generate Report Button
		JButton btnGenerate = new JButton("Generate Report");
		btnGenerate.setBounds(250, 90, 150, 30);
		contentPane.add(btnGenerate);

		// Export to Text Button
		JButton btnExport = new JButton("Export to Text");
		btnExport.setBounds(420, 90, 150, 30);
		contentPane.add(btnExport);

		// Report Display Area
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(10, 140, 860, 500);
		contentPane.add(scrollPane);

		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String startDate = startDateField.getText().trim();
				String endDate = endDateField.getText().trim();
				textArea.setText("");

				// Handle year-only input
				startDate = parseDateInputWithContext(startDate, true);
				endDate = parseDateInputWithContext(endDate, false);

				// If no dates provided, show all time report
				boolean hasDateFilter = !startDate.isEmpty() && !endDate.isEmpty();

				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					StringBuilder report = new StringBuilder();
					report.append("+===========================================================================+\n");
					report.append("|                   BENEFICIARY REQUEST FREQUENCY REPORT                    |\n");
					report.append("+===========================================================================+\n\n");

					if (hasDateFilter) {
						report.append("Report Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");
					} else {
						report.append("Report Period: All Time\n\n");
					}

					// Query 1: Total and Average Requests by Beneficiary
					report.append("+===========================================================================+\n");
					report.append("|              TOTAL AND AVERAGE PRODUCT REQUESTS BY BENEFICIARY            |\n");
					report.append("+===========================================================================+\n\n");

					String sql1 = 
						"SELECT " +
						"    b.BeneficiaryID, " +
						"    b.BeneficiaryName, " +
						"    b.City, " +
						"    COUNT(br.RequestID) AS TotalRequests, " +
						"    COALESCE(AVG(br.Quantity), 0) AS AvgQuantityPerRequest, " +
						"    SUM(br.Quantity) AS TotalQuantityRequested " +
						"FROM Beneficiaries b " +
						"LEFT JOIN BeneficiaryRequests br ON b.BeneficiaryID = br.BeneficiaryID ";

					if (hasDateFilter) {
						sql1 += "AND br.RequestDate BETWEEN ? AND ? ";
					}

					sql1 += "GROUP BY b.BeneficiaryID, b.BeneficiaryName, b.City " +
							"ORDER BY TotalRequests DESC";

					PreparedStatement pstmt1 = conn.prepareStatement(sql1);
					if (hasDateFilter) {
						pstmt1.setString(1, startDate);
						pstmt1.setString(2, endDate);
					}

					ResultSet rs1 = pstmt1.executeQuery();

					report.append(String.format("%-8s %-30s %-15s %-12s %-18s %-18s\n",
						"Ben ID", "Name", "City", "Total Req", "Avg Qty/Request", "Total Qty"));
					report.append("---------------------------------------------------------------------------------------------------\n");
					boolean hasData = false;
					while (rs1.next()) {
						hasData = true;
						report.append(String.format("%-8d %-30s %-15s %-12d %-18.2f %-18d\n",
							rs1.getInt("BeneficiaryID"),
							rs1.getString("BeneficiaryName"),
							rs1.getString("City"),
							rs1.getInt("TotalRequests"),
							rs1.getDouble("AvgQuantityPerRequest"),
							rs1.getInt("TotalQuantityRequested")));
					}

					if (!hasData) {
						report.append("No data available for the selected period.\n");
					}

					rs1.close();
					pstmt1.close();

					// Query 2: Most Requested Products
					report.append("\n\n+===========================================================================+\n");
					report.append("|                          MOST REQUESTED PRODUCTS                          |\n");
					report.append("+===========================================================================+\n\n");

					String sql2 = 
						"SELECT " +
						"    p.ProductName, " +
						"    pc.CategoryName, " +
						"    COUNT(*) AS RequestCount, " +
						"    SUM(br.Quantity) AS TotalQuantity " +
						"FROM BeneficiaryRequests br " +
						"JOIN Product p ON br.ProductID = p.ProductID " +
						"JOIN ProductCategory pc ON p.CategoryID = pc.CategoryID ";

					if (hasDateFilter) {
						sql2 += "WHERE br.RequestDate BETWEEN ? AND ? ";
					}

					sql2 += "GROUP BY p.ProductName, pc.CategoryName " +
							"ORDER BY RequestCount DESC " +
							"LIMIT 10";

					PreparedStatement pstmt2 = conn.prepareStatement(sql2);
					if (hasDateFilter) {
						pstmt2.setString(1, startDate);
						pstmt2.setString(2, endDate);
					}

					ResultSet rs2 = pstmt2.executeQuery();

					report.append(String.format("%-30s %-20s %-15s %-15s\n",
						"Product Name", "Category", "Request Count", "Total Quantity"));
					report.append("---------------------------------------------------------------------------------------------------\n");

					hasData = false;
					while (rs2.next()) {
						hasData = true;
						String categoryName = rs2.getString("CategoryName");
						if (categoryName == null) categoryName = "N/A";
						
						report.append(String.format("%-30s %-20s %-15d %-15d\n",
							rs2.getString("ProductName"),
							categoryName,
							rs2.getInt("RequestCount"),
							rs2.getInt("TotalQuantity")));
					}

					if (!hasData) {
						report.append("No product data available.\n");
					}

					rs2.close();
					pstmt2.close();

					// Query 3: Request Status Summary
					report.append("\n\n+===========================================================================+\n");
					report.append("|                         REQUEST STATUS SUMMARY                            |\n");
					report.append("+===========================================================================+\n\n");

					String sql3 = 
						"SELECT " +
						"    Status, " +
						"    COUNT(*) AS Count, " +
						"    SUM(Quantity) AS TotalQuantity " +
						"FROM BeneficiaryRequests ";

					if (hasDateFilter) {
						sql3 += "WHERE RequestDate BETWEEN ? AND ? ";
					}

					sql3 += "GROUP BY Status " +
							"ORDER BY Count DESC";

					PreparedStatement pstmt3 = conn.prepareStatement(sql3);
					if (hasDateFilter) {
						pstmt3.setString(1, startDate);
						pstmt3.setString(2, endDate);
					}

					ResultSet rs3 = pstmt3.executeQuery();

					report.append(String.format("%-20s %-15s %-15s\n",
						"Status", "Request Count", "Total Quantity"));
					report.append("---------------------------------------------------------------------------------------------------\n");

					while (rs3.next()) {
						report.append(String.format("%-20s %-15d %-15d\n",
							rs3.getString("Status"),
							rs3.getInt("Count"),
							rs3.getInt("TotalQuantity")));
					}

					rs3.close();
					pstmt3.close();

					// Query 4: Warehouse Request Analysis
					report.append("\n\n+===========================================================================+\n");
					report.append("|                       WAREHOUSE REQUEST ANALYSIS                          |\n");
					report.append("+===========================================================================+\n\n");

					String sql4 = 
						"SELECT " +
						"    w.WarehouseName, " +
						"    w.City, " +
						"    COUNT(*) AS RequestCount, " +
						"    SUM(br.Quantity) AS TotalQuantity " +
						"FROM BeneficiaryRequests br " +
						"LEFT JOIN Warehouse w ON br.WarehouseID = w.WarehouseID ";

					if (hasDateFilter) {
						sql4 += "WHERE br.RequestDate BETWEEN ? AND ? ";
					}

					sql4 += "GROUP BY w.WarehouseName, w.City " +
							"ORDER BY RequestCount DESC";

					PreparedStatement pstmt4 = conn.prepareStatement(sql4);
					if (hasDateFilter) {
						pstmt4.setString(1, startDate);
						pstmt4.setString(2, endDate);
					}

					ResultSet rs4 = pstmt4.executeQuery();

					report.append(String.format("%-30s %-20s %-15s %-15s\n",
						"Warehouse Name", "City", "Request Count", "Total Quantity"));
					report.append("---------------------------------------------------------------------------------------------------\n");

					hasData = false;
					while (rs4.next()) {
						hasData = true;
						String warehouseName = rs4.getString("WarehouseName");
						String city = rs4.getString("City");
						if (warehouseName == null) warehouseName = "Unknown Warehouse";
						if (city == null) city = "N/A";
						
						report.append(String.format("%-30s %-20s %-15d %-15d\n",
							warehouseName,
							city,
							rs4.getInt("RequestCount"),
							rs4.getInt("TotalQuantity")));
					}

					if (!hasData) {
						report.append("No warehouse data available.\n");
					}

					rs4.close();
					pstmt4.close();

					report.append("\n+===========================================================================+\n");
					report.append("|                              END OF REPORT                                |\n");
					report.append("+===========================================================================+\n");

					textArea.setText(report.toString());

				} catch (SQLException ex) {
					System.err.println("Database error: " + ex.getMessage());
					textArea.setText("Database Error:\n" + ex.getMessage() + 
						"\n\nNote: Make sure the BeneficiaryRequests table exists in your database.");
				}
			}
		});

		// Export Button Action
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportToText();
			}
		});

		// Show initial report on load
		btnGenerate.doClick();
	}

	/**
	 * Export the current report to a text file
	 */
	private void exportToText() {
		String reportContent = textArea.getText();
		
		if (reportContent.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No report to export. Please generate a report first.", 
				"Export Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File("RequestFrequencyReport.txt"));
		fileChooser.setDialogTitle("Save Report As");
		
		int userSelection = fileChooser.showSaveDialog(this);
		
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			
			if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
				fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
			}
			
			try (FileWriter writer = new FileWriter(fileToSave)) {
				writer.write(reportContent);
				JOptionPane.showMessageDialog(this, 
					"Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
					"Export Successful", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, 
					"Error exporting report:\n" + ex.getMessage(),
					"Export Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Enhanced date parsing that considers whether it's for start or end date
	 */
	private String parseDateInputWithContext(String input, boolean isStartDate) {
		if (input.isEmpty()) {
			return "";
		}
		
		// If already in YYYY-MM-DD format, return as is
		if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
			return input;
		}
		
		// If year only
		if (input.matches("\\d{4}")) {
			if (isStartDate) {
				return input + "-01-01";
			} else {
				return input + "-12-31";
			}
		}
		
		// If year and month
		if (input.matches("\\d{4}-\\d{2}")) {
			if (isStartDate) {
				return input + "-01";
			} else {
				// For end date, calculate last day of month
				String[] parts = input.split("-");
				int year = Integer.parseInt(parts[0]);
				int month = Integer.parseInt(parts[1]);
				int lastDay = getLastDayOfMonth(year, month);
				return input + "-" + String.format("%02d", lastDay);
			}
		}
		
		return "";
	}

	/**
	 * Helper method to get the last day of a month
	 */
	private int getLastDayOfMonth(int year, int month) {
		switch (month) {
			case 2: // February
				return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
			case 4: case 6: case 9: case 11: // April, June, September, November
				return 30;
			default: // January, March, May, July, August, October, December
				return 31;
		}
	}
}
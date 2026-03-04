package Frames.MonitorFrames;

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

public class MonitorReportFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;

	public MonitorReportFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 990, 700);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);

		JLabel lblTitle = new JLabel("Expired Products Report");
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
		scrollPane.setBounds(10, 140, 950, 500);
		contentPane.add(scrollPane);

		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String startDate = startDateField.getText().trim();
				String endDate = endDateField.getText().trim();
				textArea.setText("");

				// Smart date parsing - handle year-only input
				startDate = parseDateInputWithContext(startDate, true);
				endDate = parseDateInputWithContext(endDate, false);

				// If no dates provided, show all time report
				boolean hasDateFilter = !startDate.isEmpty() && !endDate.isEmpty();

				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					StringBuilder report = new StringBuilder();
					report.append("+====================================================================================================================================+\n");
					report.append("                    					EXPIRED PRODUCTS REPORT       		             \n");
					report.append("+====================================================================================================================================+\n\n");

					if (hasDateFilter) {
						report.append("Report Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");
					} else {
						report.append("Report Period: All Time\n\n");
					}

					report.append("+====================================================================================================================================+\n");
					report.append("             					TOTAL AMOUNT OF EXPIRED PRODUCTS			        \n");
					report.append("+====================================================================================================================================+\n\n");

					String sql = 
						"SELECT " +
						"    m.MonitorID, " +
						"    w.WarehouseID, " +
						"    w.WarehouseName, " +
						"    w.City, " +
						"    p.ProductID, " +
						"    p.ProductName, " +
						"    p.Quantity, " +
						"    p.DateOfExpiry " +
						"FROM monitor m " +
						"LEFT JOIN warehouse w ON m.WarehouseID = w.WarehouseID " +
						"LEFT JOIN product p ON m.ProductID = p.ProductID " +
						"WHERE p.Is_Expired = 1 ";

					if (hasDateFilter) {
						sql += "AND m.MonitorDate BETWEEN ? AND ? ";
					}

					sql += "ORDER BY MonitorID DESC";

					PreparedStatement pstmt = conn.prepareStatement(sql);
					if (hasDateFilter) {
						pstmt.setString(1, startDate);
						pstmt.setString(2, endDate);
					}

					ResultSet rs = pstmt.executeQuery();

					report.append(String.format("%-15s %-20s %-20s %-10s %-15s %-15s %-15s %-10s\n",
						"Monitor ID", "Warehouse ID", "Warehouse Name", "City", "Product ID", "Product Name", "Total Amount", "Date of Expiry"));
					report.append("--------------------------------------------------------------------------------------------------------------------------------------\n");
					boolean hasData = false;
					while (rs.next()) {
						hasData = true;
						
						java.sql.Date date = rs.getDate("DateOfExpiry");
						String prodDate = date.toString();
						
						report.append(String.format("%-15d %-20d %-20s %-10s %-15d %-15s %-15d %-10s\n",
							rs.getInt("MonitorID"),
							rs.getInt("WarehouseID"),
							rs.getString("WarehouseName"),
							rs.getString("City"),
							rs.getInt("ProductID"),
							rs.getString("ProductName"),
							rs.getInt("Quantity"),
							prodDate));
					}

					if (!hasData) {
						report.append("No data available.\n");
					}

					rs.close();
					pstmt.close();

					report.append("\n+====================================================================================================================================+\n");
					report.append("                              	 			 END OF REPORT                                \n");
					report.append("+====================================================================================================================================+\n");

					textArea.setText(report.toString());

				} catch (SQLException ex) {
					System.err.println("Database error: " + ex.getMessage());
					textArea.setText("Database Error:\n" + ex.getMessage() + 
						"\n\nNote: Make sure that the database exists");
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
		fileChooser.setSelectedFile(new File("MonitorReport.txt"));
		fileChooser.setDialogTitle("Save Report As");
		
		int userSelection = fileChooser.showSaveDialog(this);
		
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			
			// Ensure .txt extension
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
		
		// If year only (4 digits)
		if (input.matches("\\d{4}")) {
			if (isStartDate) {
				return input + "-01-01"; // Start of year for start date
			} else {
				return input + "-12-31"; // End of year for end date
			}
		}
		
		// If year and month (YYYY-MM)
		if (input.matches("\\d{4}-\\d{2}")) {
			if (isStartDate) {
				return input + "-01"; // First day of month for start date
			} else {
				// For end date, calculate last day of month
				String[] parts = input.split("-");
				int year = Integer.parseInt(parts[0]);
				int month = Integer.parseInt(parts[1]);
				int lastDay = getLastDayOfMonth(year, month);
				return input + "-" + String.format("%02d", lastDay);
			}
		}
		
		// Invalid format, return empty to disable filtering
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

package Frames.CourierFrames;

import Frames.DBManager;
import Frames.MainFrame;

import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import javax.swing.table.DefaultTableModel;
import java.awt.Choice;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CourierReports extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JTextField courField;
	private JTextField warehouseField;
	private JTextField startTimeField;
	private JTextField endTimeField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CourierReports frame = new CourierReports();
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
	public CourierReports() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setSize(1400, 800);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnBack = new JButton("Go Back to Main Menu\r\n");
		btnBack.setBounds(1077, 12, 297, 70);
		contentPane.add(btnBack);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 93, 1364, 623);
		contentPane.add(scrollPane);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblCourPerf = new JLabel("Courier Performance Reports");
		lblCourPerf.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblCourPerf.setBounds(10, 12, 758, 28);
		contentPane.add(lblCourPerf);

		JLabel lblTimespan = new JLabel("Timespan");
		lblTimespan.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTimespan.setBounds(418, 56, 61, 28);
		contentPane.add(lblTimespan);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(745, 59, 89, 23);
		contentPane.add(btnRefresh);

		JLabel lblCourID = new JLabel("CourierID");
		lblCourID.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCourID.setBounds(20, 56, 61, 28);
		contentPane.add(lblCourID);

		courField = new JTextField();
		courField.setBounds(80, 61, 96, 18);
		contentPane.add(courField);
		courField.setColumns(10);

		JLabel lblWarehouseID = new JLabel("Warehouse ID");
		lblWarehouseID.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblWarehouseID.setBounds(197, 56, 112, 28);
		contentPane.add(lblWarehouseID);

		warehouseField = new JTextField();
		warehouseField.setColumns(10);
		warehouseField.setBounds(288, 61, 96, 18);
		contentPane.add(warehouseField);

		startTimeField = new JTextField();
		startTimeField.setBounds(503, 61, 96, 18);
		contentPane.add(startTimeField);
		startTimeField.setText("YYYYY-MM-DD");
		startTimeField.setColumns(10);

		endTimeField = new JTextField();
		endTimeField.setColumns(10);
		endTimeField.setBounds(622, 61, 96, 18);
		endTimeField.setText("YYYYY-MM-DD");
		contentPane.add(endTimeField);



		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame main = new MainFrame();
				dispose();
			}
		});

		btnRefresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {


				try { // query and compute for total and average
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String courID = courField.getText();;
					String warehouseID = warehouseField.getText();
					String start = startTimeField.getText();
					String end = endTimeField.getText();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDate startAvg = LocalDate.parse(start);
					LocalDate endAvg = LocalDate.parse(end);
					int timeSpan = (int) ChronoUnit.DAYS.between(startAvg, endAvg);

					PreparedStatement stmt = conn.prepareStatement(
							"SELECT c.LastName, c.FirstName, w.WarehouseName, COUNT(d.DeliveryID) AS totalDeliveries FROM Delivery d " +
									"JOIN Courier c ON c.Cour_ID = d.CourierID " +
									"JOIN Warehouse w ON w.WarehouseID = d.WarehouseID" +
									" WHERE d.CourierID = ? " +
									"AND d.WarehouseID = ? " +
									"AND DeliveryDate > ? " +
									"AND DeliveryDate < ?" +
									"GROUP BY c.Cour_ID, c.LastName, c.FirstName, w.WarehouseID, w.WarehouseName;");



					stmt.setString(1,courID);
					stmt.setString(2, warehouseID);
					stmt.setString(3, startTimeField.getText());
					stmt.setString(4, endTimeField.getText());

					ResultSet rs = stmt.executeQuery();


					boolean resultsFound = rs.next();
					if(!resultsFound){
							JOptionPane.showMessageDialog(
									null,                         // Parent component (or null to center on screen)
									"No results found for your query.",  // The message content
									"Search Error",                      // The title of the dialog box
									JOptionPane.ERROR_MESSAGE            // The message type (shows an 'X' icon)
							);
					}

					String lastname = rs.getString("LastName");
					String firstname = rs.getString("FirstName");
					String ware = rs.getString("WarehouseName");
					int delivTotal= Integer.parseInt(rs.getString("totalDeliveries"));

					double delivAvg=  delivTotal * 1.0/timeSpan ;

					String fullReport =
							"COURIER DELIVERY REPORT (" + startTimeField.getText() + " to " + endTimeField.getText() + ")\n" +
									"==================================================================================================================================================\n" +
									"| Courier ID | Last Name       | First Name   | Warehouse                           | Total Deliveries   | Avg. Deliveries    |\n" +
									"|------------|-----------------|--------------|-------------------------------------|--------------------|--------------------|\n" +
									"| " + String.format("%-10s", courID) +
									" | " + String.format("%-15s", lastname) +
									" | " + String.format("%-12s", firstname) +
									" | " + String.format("%-35s", ware) +  // <-- Changed width to 35 here
									" | " + String.format("%-18s", delivTotal) +
									" | " + String.format("%-18s", delivAvg) +
									" |\n" +
									"==================================================================================================================================================\n" +
									"Averages calculated over the timespan.";

					textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
					textArea.setText(fullReport);



					stmt.close();
				} catch (Exception ex) {
					System.err.println("Failed to establish connection: " + ex.getMessage());
				}

			}
		});



	}
}

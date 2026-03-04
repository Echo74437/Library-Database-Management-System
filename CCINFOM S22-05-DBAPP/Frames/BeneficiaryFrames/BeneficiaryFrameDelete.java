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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BeneficiaryFrameDelete extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public BeneficiaryFrameDelete() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 450);
		setTitle("Delete Beneficiary");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);
		setVisible(true);

		// Beneficiary List Section
		JLabel lblBeneficiaryList = new JLabel("Existing Beneficiaries:");
		lblBeneficiaryList.setBounds(20, 20, 200, 25);
		lblBeneficiaryList.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblBeneficiaryList);

		JTextArea beneficiaryListArea = new JTextArea();
		beneficiaryListArea.setEditable(false);
		beneficiaryListArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(beneficiaryListArea);
		scrollPane.setBounds(20, 50, 540, 150);
		contentPane.add(scrollPane);

		// Load beneficiaries on initialization
		loadBeneficiaryList(beneficiaryListArea);

		JLabel lblSeparator = new JLabel("Delete Form:");
		lblSeparator.setBounds(20, 220, 200, 25);
		lblSeparator.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
		contentPane.add(lblSeparator);

		JLabel lblBeneficiaryID = new JLabel("Beneficiary ID to Delete:");
		lblBeneficiaryID.setBounds(20, 260, 200, 25);
		contentPane.add(lblBeneficiaryID);

		JTextField beneficiaryIDField = new JTextField();
		beneficiaryIDField.setBounds(20, 290, 200, 25);
		contentPane.add(beneficiaryIDField);
		beneficiaryIDField.setColumns(10);

		JButton btnDelete = new JButton("Delete Beneficiary");
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

				String beneficiaryID = beneficiaryIDField.getText().trim();

				if (beneficiaryID.isEmpty()) {
					lblError.setText("Please enter Beneficiary ID");
					lblError.setVisible(true);
					contentPane.revalidate();
					contentPane.repaint();
					return;
				}

				try {
					int id = Integer.parseInt(beneficiaryID);
				if (id < 100000 || id > 999999) {
					lblError.setText("ID must be between 100000-999999");
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

				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					String sql = "DELETE FROM Beneficiaries WHERE BeneficiaryID = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(beneficiaryID));

					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Beneficiary deleted successfully: ID " + beneficiaryID);
						lblError.setText("Beneficiary deleted successfully!");
						lblError.setForeground(java.awt.Color.GREEN);
						lblError.setVisible(true);
						loadBeneficiaryList(beneficiaryListArea);
						beneficiaryIDField.setText("");
					} else {
						lblError.setText("Beneficiary ID not found");
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

	private void loadBeneficiaryList(JTextArea textArea) {
		try {
			DBManager dbManager = DBManager.getInstance();
			Connection conn = dbManager.getConnection();

			String sql = "SELECT BeneficiaryID, BeneficiaryName, City FROM Beneficiaries ORDER BY BeneficiaryID";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			StringBuilder listText = new StringBuilder();
			listText.append(String.format("%-8s %-30s %-20s\n", "ID", "Name", "City"));
			listText.append("────────────────────────────────────────────────────────────\n");

			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				listText.append(String.format("%-8d %-30s %-20s\n",
					rs.getInt("BeneficiaryID"),
					rs.getString("BeneficiaryName"),
					rs.getString("City")));
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
package Frames.CourierFrames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Frames.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class CourierFrameDelete extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	
	

	
	public CourierFrameDelete() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 380, 180);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setAlwaysOnTop(true);
		setVisible(true);
		
		JTextField idField = new JTextField();
		idField.setBounds(226, 36, 130, 20);
		
		contentPane.add(idField);
		idField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Courier ID to be deleted\r\n");
		lblNewLabel.setBounds(10, 36, 185, 20);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Delete Courier");
		btnNewButton.setBounds(60, 87, 246, 20);
		contentPane.add(btnNewButton);
		
	
		btnNewButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
				String id = idField.getText();
				try {
					DBManager dbManager = DBManager.getInstance();
					Connection conn = dbManager.getConnection();

					PreparedStatement stmt = conn.prepareStatement(
							"DELETE FROM Courier WHERE Cour_ID = ?");

					stmt.setString(1, id);


					stmt.execute();
					stmt.close();
				} catch (SQLException ex) {
					System.err.println("Failed to establish connection: " + ex.getMessage());
				}


				dispose();

		    }
		});

	}
}

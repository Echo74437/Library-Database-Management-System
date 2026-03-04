package Frames.BeneficiaryFrames;

import Frames.MainFrame;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class BeneficiaryFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BeneficiaryFrame frame = new BeneficiaryFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public BeneficiaryFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setSize(1400, 800);
		setVisible(true);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(150, 600, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton createButton = new JButton("Add a New Beneficiary");
		createButton.setBounds(345, 100, 709, 85);
		contentPane.add(createButton);

		JButton updateButton = new JButton("Update Beneficiary Information");
		updateButton.setBounds(345, 200, 709, 85);
		contentPane.add(updateButton);

		JButton deleteButton = new JButton("Delete Beneficiary");
		deleteButton.setBounds(345, 300, 709, 85);
		contentPane.add(deleteButton);

		JButton viewButton = new JButton("View Beneficiary Records");
		viewButton.setBounds(345, 400, 709, 85);
		contentPane.add(viewButton);

		JButton backButton = new JButton("Go Back to Main Menu");
		backButton.setBounds(345, 500, 709, 85);
		contentPane.add(backButton);

		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BeneficiaryFrameCreate createFrame = new BeneficiaryFrameCreate();
			}
		});

		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BeneficiaryFrameUpdate updateFrame = new BeneficiaryFrameUpdate();
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BeneficiaryFrameDelete deleteFrame = new BeneficiaryFrameDelete();
			}
		});

		viewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BeneficiaryFrameView viewFrame = new BeneficiaryFrameView();
			}
		});

		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame main = new MainFrame();
				dispose();
			}
		});
	}
}
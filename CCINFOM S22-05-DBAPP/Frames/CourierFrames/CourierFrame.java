package Frames.CourierFrames;

import Frames.MainFrame;
import com.sun.tools.javac.Main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.BoxLayout;

public class CourierFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CourierFrame frame = new CourierFrame();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CourierFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setSize(1400,800);
		setVisible(true);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(150, 600, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton createButton = new JButton("Add a New Courier");
		createButton.setBounds(345, 150, 709, 85);
		contentPane.add(createButton);
		
		JButton updateButton = new JButton("Update Courier Information\r\n");
		updateButton.setBounds(345, 255, 709, 85);
		contentPane.add(updateButton);
		
		JButton deleteButton = new JButton("Delete Courier");
		deleteButton.setBounds(345, 360, 709, 85);
		contentPane.add(deleteButton);
		
		JButton readButton = new JButton("View Courier Deliveries");
		readButton.setBounds(345, 465, 709, 85);
		contentPane.add(readButton);

		JButton backButton = new JButton("Go Back to Main Menu");
		backButton.setBounds(345, 570, 709, 85);
		contentPane.add(backButton);
		
		
		createButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        CourierFrameCreate addCour = new CourierFrameCreate();
		    }
		});

		updateButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	CourierFrameUpdate updateCour = new CourierFrameUpdate();
		    }
		});

		deleteButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	CourierFrameDelete deleteCour = new CourierFrameDelete();
		    }
		});

		readButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
				CourierFrameRead readCour = new CourierFrameRead();
				dispose();
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

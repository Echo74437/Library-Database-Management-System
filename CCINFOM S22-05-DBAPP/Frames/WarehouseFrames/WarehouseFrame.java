package Frames.WarehouseFrames;

import Frames.MainFrame;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class WarehouseFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
                    WarehouseFrame frame = new WarehouseFrame();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
	 * Create the frame.
	 */
	public WarehouseFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setSize(1400,800);
		setVisible(true);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(150, 600, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton createButton = new JButton("Add a New Warehouse");
		createButton.setBounds(345, 100, 709, 85);
		contentPane.add(createButton);
		
		JButton updateButton = new JButton("Update Warehouse Information\r\n");
		updateButton.setBounds(345, 200, 709, 85);
		contentPane.add(updateButton);
		
		JButton deleteButton = new JButton("Delete Warehouse");
		deleteButton.setBounds(345, 300, 709, 85);
		contentPane.add(deleteButton);

		JButton readButton = new JButton("View Warehouse Records");
		readButton.setBounds(345, 400, 709, 85);
		contentPane.add(readButton);

		JButton backButton = new JButton("Go Back to Main Menu");
		backButton.setBounds(345, 550, 709, 85);
		contentPane.add(backButton);
		
		createButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        WarehouseFrameCreate addWare = new WarehouseFrameCreate();
		    }
		});

		updateButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	WarehouseFrameUpdate updateWare = new WarehouseFrameUpdate();
		    }
		});

		deleteButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	WarehouseFrameDelete deleteWare = new WarehouseFrameDelete();
		    }
		});

		readButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
				WarehouseFrameRead readWare = new WarehouseFrameRead();
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
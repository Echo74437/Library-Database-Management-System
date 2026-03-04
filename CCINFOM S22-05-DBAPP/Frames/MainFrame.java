package Frames;
import Frames.BeneficiaryFrames.*;
import Frames.CourierFrames.*;
import Frames.DeliveryFrames.DeliveryFrame;
import Frames.MonitorFrames.*;
import Frames.WarehouseFrames.WarehouseFrame;
import Frames.ProductFrames.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Image backgroundImage;

	DBManager dbManager = DBManager.getInstance();

	// Custom JPanel class for background image
	class BackgroundPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		public BackgroundPanel() {
			setOpaque(true);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			} else {
				setBackground(new Color(0, 0, 64));
				g.setColor(new Color(0, 0, 64));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}
	}



	public MainFrame() {
	setResizable(false);
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	setBounds(100, 100, 450, 300);
	setSize(1400,800);
	
	// Load background image
	try {
		ImageIcon imageIcon = new ImageIcon("Frames/Image/hands_bg.png");
		backgroundImage = imageIcon.getImage();
		System.out.println("Background image loaded successfully");
	} catch (Exception e) {
		System.out.println("Error loading background image: " + e.getMessage());
		e.printStackTrace();
	}
	
	contentPane = new BackgroundPanel();
	contentPane.setForeground(new Color(255, 255, 255));
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);
	
	// Left navigation buttons
	int leftButtonWidth = 180;
	int leftButtonHeight = 60;
	int leftButtonX = 50;
	
	JButton recordsButton = new JButton("Records");
	recordsButton.setBounds(leftButtonX, 250, leftButtonWidth, leftButtonHeight);
	contentPane.add(recordsButton);
	
	JButton transactionsButton = new JButton("Transactions");
	transactionsButton.setBounds(leftButtonX, 330, leftButtonWidth, leftButtonHeight);
	contentPane.add(transactionsButton);
	
	JButton reportsButton = new JButton("Reports");
	reportsButton.setBounds(leftButtonX, 410, leftButtonWidth, leftButtonHeight);
	contentPane.add(reportsButton);

	// Right side
	JPanel recordsPanel = new JPanel();
	recordsPanel.setBounds(280, 120, 1050, 600);
	recordsPanel.setOpaque(false); 
	recordsPanel.setVisible(false);
	contentPane.add(recordsPanel);
	recordsPanel.setLayout(null);
		
	JButton beneficiariesButton = new JButton("Beneficiaries Record");
	beneficiariesButton.setBounds(100, 50, 850, 80);
	recordsPanel.add(beneficiariesButton);
	
	JButton warehouseButton = new JButton("Warehouse Record");
	warehouseButton.setBounds(100, 150, 850, 80);
	recordsPanel.add(warehouseButton);
	
	JButton productButton = new JButton("Product Record");
	productButton.setBounds(100, 250, 850, 80);
	recordsPanel.add(productButton);
	
	JButton couriersButton = new JButton("Courier Record");
	couriersButton.setBounds(100, 350, 850, 80);
	recordsPanel.add(couriersButton);

	// Right side
	JPanel transactionPanel = new JPanel();
	transactionPanel.setBounds(280, 120, 1050, 600);
	transactionPanel.setOpaque(false);
	transactionPanel.setVisible(false);
	contentPane.add(transactionPanel);
	transactionPanel.setLayout(null);

	JButton requestButton = new JButton("Request Products");
	requestButton.setBounds(100, 50, 850, 80);
	transactionPanel.add(requestButton);
	
	JButton allocateButton = new JButton("Allocate Products");
	allocateButton.setBounds(100, 150, 850, 80);
	transactionPanel.add(allocateButton);
	
	JButton monitorButton = new JButton("Monitor Products");
	monitorButton.setBounds(100, 250, 850, 80);
	transactionPanel.add(monitorButton);
	
	JButton deliverButton = new JButton("Deliver Products");
	deliverButton.setBounds(100, 350, 850, 80);
	transactionPanel.add(deliverButton);

	// Right side
	JPanel reportsPanel = new JPanel();
	reportsPanel.setBounds(280, 120, 1050, 600);
	reportsPanel.setOpaque(false);
	reportsPanel.setVisible(false);
	contentPane.add(reportsPanel);
	reportsPanel.setLayout(null);

	JButton frequencyButton = new JButton("Beneficiaries Frequency Report");
	frequencyButton.setBounds(100, 50, 850, 80);
	reportsPanel.add(frequencyButton);
	
	JButton allocateStatisticsButton = new JButton("Allocation Statistics Report");
	allocateStatisticsButton.setBounds(100, 150, 850, 80);
	reportsPanel.add(allocateStatisticsButton);
	
	JButton expiredButton = new JButton("Expired Products Report");
	expiredButton.setBounds(100, 250, 850, 80);
	reportsPanel.add(expiredButton);
	
	JButton courierPerformanceButton = new JButton("Courier Performance Report");
	courierPerformanceButton.setBounds(100, 350, 850, 80);
	reportsPanel.add(courierPerformanceButton);
		
		
	JLabel lblNewLabel = new JLabel("Beneficiary Monitoring System", JLabel.CENTER);
	lblNewLabel.setBounds(0, 30, 1400, 60);
	lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
	lblNewLabel.setForeground(new Color(255, 255, 255));
	JLabel shadowLabel = new JLabel("Beneficiary Monitoring System", JLabel.CENTER);
	shadowLabel.setBounds(2, 32, 1400, 60);
	shadowLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
	shadowLabel.setForeground(new Color(0, 0, 0));
	contentPane.add(shadowLabel);
	contentPane.add(lblNewLabel);		
		
		recordsButton.addActionListener(new ActionListener() {
          
            // Override the actionPerformed() method
            public void actionPerformed(ActionEvent e){
            	
            	recordsPanel.setVisible(true);
            	transactionPanel.setVisible(false);
            	reportsPanel.setVisible(false);
            }
          
        });
		
		transactionsButton.addActionListener(new ActionListener() {
	          
            // Override the actionPerformed() method
            public void actionPerformed(ActionEvent e){
            	
            	recordsPanel.setVisible(false);
            	transactionPanel.setVisible(true);
            	reportsPanel.setVisible(false);
            }
          
        });
		
		reportsButton.addActionListener(new ActionListener() {
	          
            // Override the actionPerformed() method
            public void actionPerformed(ActionEvent e){
            	
            	recordsPanel.setVisible(false);
            	transactionPanel.setVisible(false);
            	reportsPanel.setVisible(true);
            }
          
        });

		beneficiariesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BeneficiaryFrame benFrame = new BeneficiaryFrame();
				dispose();
			}
		});

		requestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BeneficiaryRequestFrame reqFrame = new BeneficiaryRequestFrame();
			}
		});

		frequencyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RequestFrequencyReportFrame repFrame = new RequestFrequencyReportFrame();
			}
		});

		warehouseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				WarehouseFrame wareFrame = new WarehouseFrame();
				dispose();
			}
		});

		productButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ProductFrame prodFrame = new ProductFrame();
				dispose();
			}
		});
										
		monitorButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				MonitorProductFrame monitorFrame = new MonitorProductFrame();
			}
		});

		expiredButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				MonitorReportFrame prodFrame = new MonitorReportFrame();
			}
		});
		
		couriersButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				CourierFrame courFrame = new CourierFrame();
				dispose();
			}
		});
		
		courierPerformanceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CourierReports courierReports = new CourierReports();
				dispose();
			}
		});

		deliverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeliveryFrame deliveryFrame = new DeliveryFrame();
				dispose();
			}
		});

		allocateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProductRequestFrame prodReqFrame = new ProductRequestFrame();
			}
		});

		allocateStatisticsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProductRequestFrequencyReportFrame allocStatsFrame = new ProductRequestFrequencyReportFrame();
			}
		});

		//close sql connection
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// Close the SQL connection
				dbManager.closeConnection();
				dispose();
			}
		});
		
		setVisible(true);
	}
}
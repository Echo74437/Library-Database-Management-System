package Frames.ProductFrames;

import Frames.DBManager;
import Frames.MainFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductFrame extends JFrame {
    
    private JTable productTable;
    private JButton refreshButton;
    private DefaultTableModel tableModel;
    
    public ProductFrame() {
        setTitle("Product Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize components
        tableModel = new DefaultTableModel(new String[]{"ProductID","Category","Warehouse","Name","Description","Quantity","Expiry"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        productTable = new JTable(tableModel); // This will be populated with product data
        refreshButton = new JButton("Refresh");
        
        // Set layout
        setLayout(new BorderLayout());

        // Top panel with CRUD and request buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 4, 10, 10));

        JButton createButton = new JButton("Add a New Product");
        JButton updateButton = new JButton("Update Product Information");
        JButton deleteButton = new JButton("Delete Product");
        JButton viewButton = new JButton("View Product Records");
        JButton requestButton = new JButton("Create Product Request");
        JButton freqReportButton = new JButton("Product Request Frequency Report");
        JButton backButton = new JButton("Go Back to Main Menu");

        topPanel.add(createButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(viewButton);
        topPanel.add(requestButton);
        topPanel.add(freqReportButton);
        topPanel.add(new JPanel()); // spacer
        topPanel.add(backButton);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
        
        // Add action listener for the refresh button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProductData();
            }
        });

        // Button action listeners - mirror BeneficiaryFrame behavior
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductFrameCreate createFrame = new ProductFrameCreate(ProductFrame.this);
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductFrameUpdate updateFrame = new ProductFrameUpdate();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductFrameDelete deleteFrame = new ProductFrameDelete(ProductFrame.this);
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductFrameView viewFrame = new ProductFrameView();
            }
        });

        requestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductRequestFrame reqFrame = new ProductRequestFrame();
            }
        });

        freqReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductRequestFrequencyReportFrame freq = new ProductRequestFrequencyReportFrame();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame main = new MainFrame();
                dispose();
            }
        });
        
        // Load initial product data
        loadProductData();
        // Make frame visible (MainFrame disposes itself when opening this frame)
        setVisible(true);
    }
    
    public void loadProductData() {
        tableModel.setRowCount(0);
        String sql = "SELECT p.ProductID, c.CategoryName, p.WarehouseID, p.ProductName, p.ProductDescription, p.Quantity, p.DateOfExpiry "
                + "FROM Product p LEFT JOIN ProductCategory c ON p.CategoryID = c.CategoryID ORDER BY p.ProductName";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ProductID"));
                row.add(rs.getString("CategoryName"));
                row.add(rs.getInt("WarehouseID"));
                row.add(rs.getString("ProductName"));
                row.add(rs.getString("ProductDescription"));
                row.add(rs.getInt("Quantity"));
                row.add(rs.getDate("DateOfExpiry"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductFrame frame = new ProductFrame();
            frame.setVisible(true);
        });
    }
}
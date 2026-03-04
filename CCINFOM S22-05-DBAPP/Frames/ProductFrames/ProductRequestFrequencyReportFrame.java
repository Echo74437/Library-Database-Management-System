package Frames.ProductFrames;

import Frames.DBManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class ProductRequestFrequencyReportFrame extends JFrame {
    private JTable reportTable;
    private JButton generateReportButton;
    private JPanel panel;

    public ProductRequestFrequencyReportFrame() {
        setTitle("Allocation Statistics Report");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        reportTable = new JTable(); // This will be populated with report data
        JScrollPane scrollPane = new JScrollPane(reportTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        panel.add(generateReportButton, BorderLayout.SOUTH);

        add(panel);
        // Finalize window appearance
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateReport() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Warehouse ID","Warehouse Name","City","Product ID","Product Name","Total Allocations","Total Quantity"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reportTable.setModel(model);
        String sql = "SELECT w.WarehouseID, w.WarehouseName, w.City, p.ProductID, p.ProductName, " +
                     "COUNT(a.AllocationID) AS totalAllocations, SUM(br.Quantity) AS totalQuantity " +
                     "FROM Allocation a " +
                     "JOIN BeneficiaryRequests br ON a.RequestID = br.RequestID " +
                     "JOIN Product p ON br.ProductID = p.ProductID " +
                     "JOIN Warehouse w ON br.WarehouseID = w.WarehouseID " +
                     "GROUP BY w.WarehouseID, w.WarehouseName, w.City, p.ProductID, p.ProductName " +
                     "ORDER BY w.WarehouseID, totalAllocations DESC";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("WarehouseID"), 
                    rs.getString("WarehouseName"), 
                    rs.getString("City"),
                    rs.getInt("ProductID"), 
                    rs.getString("ProductName"), 
                    rs.getInt("totalAllocations"),
                    rs.getInt("totalQuantity")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to generate report: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductRequestFrequencyReportFrame frame = new ProductRequestFrequencyReportFrame();
            frame.setVisible(true);
        });
    }
}
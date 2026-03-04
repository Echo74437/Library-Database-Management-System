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
import java.sql.Statement;

public class ProductRequestFrame extends JFrame {
    private JTextField productIdField;
    private JTextField quantityField;
    private JTextField beneficiaryIdField;
    private JTextField warehouseIdField;
    private JTextArea requestArea;
    private JButton submitButton;
    private JButton viewRequestsButton;

    public ProductRequestFrame() {
        setTitle("Product Request Management");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2, 8, 8));

        inputPanel.add(new JLabel("Beneficiary ID:"));
        beneficiaryIdField = new JTextField();
        inputPanel.add(beneficiaryIdField);

        inputPanel.add(new JLabel("Warehouse ID:"));
        warehouseIdField = new JTextField();
        inputPanel.add(warehouseIdField);

        inputPanel.add(new JLabel("Product ID:"));
        productIdField = new JTextField();
        inputPanel.add(productIdField);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        submitButton = new JButton("Submit Request");
        inputPanel.add(submitButton);

        viewRequestsButton = new JButton("View Requests");
        inputPanel.add(viewRequestsButton);

        add(inputPanel, BorderLayout.NORTH);

        requestArea = new JTextArea();
        requestArea.setEditable(false);
        add(new JScrollPane(requestArea), BorderLayout.CENTER);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitRequest();
            }
        });

        viewRequestsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewRequests();
            }
        });
        // Finalize window appearance
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void submitRequest() {
        String beneficiaryIdText = beneficiaryIdField.getText().trim();
        String warehouseIdText = warehouseIdField.getText().trim();
        String productId = productIdField.getText().trim();
        String quantity = quantityField.getText().trim();

        if (beneficiaryIdText.isEmpty() || warehouseIdText.isEmpty() || productId.isEmpty() || quantity.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Beneficiary ID, Warehouse ID, Product ID, and Quantity.");
            return;
        }

        int beneficiaryId, warehouseId, prodId, qty;
        try {
            beneficiaryId = Integer.parseInt(beneficiaryIdText);
            warehouseId = Integer.parseInt(warehouseIdText);
            prodId = Integer.parseInt(productId);
            qty = Integer.parseInt(quantity);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID and quantity fields must be valid numbers.");
            return;
        }

        // Perform DB transaction: insert BeneficiaryRequests then create Allocation record
        Connection conn = null;
        try {
            conn = DBManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            String insertRequest = "INSERT INTO BeneficiaryRequests (BeneficiaryID, WarehouseID, ProductID, Quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, beneficiaryId);
                ps.setInt(2, warehouseId);
                ps.setInt(3, prodId);
                ps.setInt(4, qty);
                int affected = ps.executeUpdate();
                if (affected == 0) throw new SQLException("Creating request failed, no rows affected.");

                // Get generated RequestID
                int requestId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        requestId = keys.getInt(1);
                    } else {
                        throw new SQLException("Creating request failed, no ID obtained.");
                    }
                }

                // Generate new AllocationID (max + 1)
                int newAllocId = 1;
                String allocIdSql = "SELECT COALESCE(MAX(AllocationID), 0) + 1 AS nextAlloc FROM Allocation";
                try (PreparedStatement allocStmt = conn.prepareStatement(allocIdSql);
                     ResultSet rs = allocStmt.executeQuery()) {
                    if (rs.next()) newAllocId = rs.getInt("nextAlloc");
                }

                String insertAlloc = "INSERT INTO Allocation (AllocationID, RequestID) VALUES (?, ?)";
                try (PreparedStatement allocInsert = conn.prepareStatement(insertAlloc)) {
                    allocInsert.setInt(1, newAllocId);
                    allocInsert.setInt(2, requestId);
                    allocInsert.executeUpdate();
                }

                conn.commit();
                requestArea.append(String.format("Request submitted and allocated. RequestID=%d AllocationID=%d\n", requestId, newAllocId));

                // Clear fields
                beneficiaryIdField.setText("");
                warehouseIdField.setText("");
                productIdField.setText("");
                quantityField.setText("");
            }
        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException roll) {
                // ignore
            }
            JOptionPane.showMessageDialog(this, "Failed to submit request: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            requestArea.append("Failed to submit request: " + ex.getMessage() + "\n");
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ex) {
                // ignore
            }
        }
    }

    private void viewRequests() {
        requestArea.setText("");
        String sql = "SELECT RequestID, BeneficiaryID, ProductID, Quantity, Status, RequestDate FROM BeneficiaryRequests ORDER BY RequestDate DESC";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requestArea.append(String.format("ReqID:%d Beneficiary:%d Product:%d Qty:%d Status:%s Date:%s\n",
                        rs.getInt("RequestID"), rs.getInt("BeneficiaryID"), rs.getInt("ProductID"), rs.getInt("Quantity"), rs.getString("Status"), rs.getTimestamp("RequestDate")));
            }
        } catch (SQLException ex) {
            requestArea.append("Failed to load requests: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductRequestFrame frame = new ProductRequestFrame();
            frame.setVisible(true);
        });
    }
}
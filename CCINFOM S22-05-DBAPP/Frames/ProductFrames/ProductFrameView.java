package Frames.ProductFrames;

import Frames.DBManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductFrameView extends JFrame {

    private JPanel contentPane;

    public ProductFrameView() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 700);
        setTitle("View Product Records");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        // allow normal window stacking

        JLabel lblProductList = new JLabel("Existing Products:");
        lblProductList.setBounds(20, 20, 200, 25);
        lblProductList.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
        contentPane.add(lblProductList);

        JTextArea productListArea = new JTextArea();
        productListArea.setEditable(false);
        productListArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JScrollPane listScrollPane = new JScrollPane(productListArea);
        listScrollPane.setBounds(20, 50, 840, 150);
        contentPane.add(listScrollPane);

        // Load products on initialization
        loadProductList(productListArea);

        JLabel lblSeparator = new JLabel("View Details:");
        lblSeparator.setBounds(20, 220, 200, 25);
        lblSeparator.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
        contentPane.add(lblSeparator);

        JLabel lblProductID = new JLabel("Enter Product ID to View:");
        lblProductID.setBounds(20, 260, 250, 25);
        contentPane.add(lblProductID);

        JTextField productIDField = new JTextField();
        productIDField.setBounds(20, 290, 200, 25);
        contentPane.add(productIDField);
        productIDField.setColumns(10);

        JButton btnView = new JButton("View Details");
        btnView.setBounds(240, 290, 150, 25);
        btnView.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11));
        contentPane.add(btnView);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 330, 840, 320);
        contentPane.add(scrollPane);

        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String productID = productIDField.getText().trim();
                textArea.setText("");

                if (productID.isEmpty()) {
                    textArea.setText("Please enter Product ID");
                    return;
                }

                int id;
                try {
                    id = Integer.parseInt(productID);
                } catch (NumberFormatException ex) {
                    textArea.setText("Product ID must be a valid number");
                    return;
                }

                try {
                    DBManager dbManager = DBManager.getInstance();
                    Connection conn = dbManager.getConnection();

                    String sql = "SELECT p.ProductID, p.ProductName, p.ProductDescription, p.Quantity, p.DateOfExpiry, c.CategoryName, p.WarehouseID "
                            + "FROM Product p LEFT JOIN ProductCategory c ON p.CategoryID = c.CategoryID WHERE p.ProductID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        StringBuilder output = new StringBuilder();
                        output.append("=== PRODUCT RECORD ===\n\n");
                        output.append("Product ID: ").append(rs.getInt("ProductID")).append("\n");
                        output.append("Name: ").append(rs.getString("ProductName")).append("\n");
                        output.append("Description: ").append(rs.getString("ProductDescription")).append("\n");
                        output.append("Category: ").append(rs.getString("CategoryName")).append("\n");
                        output.append("Quantity: ").append(rs.getInt("Quantity")).append("\n");
                        java.sql.Date d = rs.getDate("DateOfExpiry");
                        output.append("Date Of Expiry: ").append(d == null ? "" : d.toString()).append("\n");
                        output.append("Warehouse ID: ").append(rs.getInt("WarehouseID")).append("\n\n");

                        // Beneficiary Requests
                        output.append("=== REQUESTS FOR THIS PRODUCT ===\n");
                        String reqsSQL = "SELECT br.RequestID, br.Quantity, br.RequestDate, br.Status, b.BeneficiaryName "
                                + "FROM BeneficiaryRequests br JOIN Beneficiaries b ON br.BeneficiaryID = b.BeneficiaryID "
                                + "WHERE br.ProductID = ? ORDER BY br.RequestDate DESC";
                        PreparedStatement reqsPstmt = conn.prepareStatement(reqsSQL);
                        reqsPstmt.setInt(1, id);
                        ResultSet reqsRs = reqsPstmt.executeQuery();

                        boolean hasReqs = false;
                        StringBuilder reqsOutput = new StringBuilder();
                        reqsOutput.append(String.format("%-12s %-30s %-10s %-15s %-12s\n", 
                                "Request ID", "Beneficiary", "Quantity", "Request Date", "Status"));
                        reqsOutput.append("───────────────────────────────────────────────────────────────────────────────\n");
                        while (reqsRs.next()) {
                            hasReqs = true;
                            String dateStr = reqsRs.getString("RequestDate");
                            if (dateStr != null && dateStr.length() >= 10) dateStr = dateStr.substring(0, 10);
                            reqsOutput.append(String.format("%-12d %-30s %-10d %-15s %-12s\n",
                                    reqsRs.getInt("RequestID"),
                                    reqsRs.getString("BeneficiaryName"),
                                    reqsRs.getInt("Quantity"),
                                    dateStr,
                                    reqsRs.getString("Status")));
                        }
                        if (hasReqs) output.append(reqsOutput.toString()); else output.append("No requests found for this product.\n");
                        reqsRs.close(); reqsPstmt.close();

                        output.append("\n");

                        // Warehouses that have this product (usually one)
                        output.append("=== WAREHOUSE INFO ===\n");
                        String whSQL = "SELECT w.WarehouseID, w.WarehouseName, w.City "
                                + "FROM Warehouse w JOIN Product p ON p.WarehouseID = w.WarehouseID WHERE p.ProductID = ?";
                        PreparedStatement whPstmt = conn.prepareStatement(whSQL);
                        whPstmt.setInt(1, id);
                        ResultSet whRs = whPstmt.executeQuery();
                        boolean hasWh = false;
                        StringBuilder whOut = new StringBuilder();
                        whOut.append(String.format("%-12s %-35s %-15s\n", "Warehouse ID", "Warehouse Name", "City"));
                        whOut.append("─────────────────────────────────────────────────────────────────\n");
                        while (whRs.next()) {
                            hasWh = true;
                            whOut.append(String.format("%-12d %-35s %-15s\n",
                                    whRs.getInt("WarehouseID"),
                                    whRs.getString("WarehouseName"),
                                    whRs.getString("City")));
                        }
                        if (hasWh) output.append(whOut.toString()); else output.append("No warehouse info found for this product.\n");
                        whRs.close(); whPstmt.close();

                        output.append("\n");

                        // Deliveries involving this product
                        output.append("=== DELIVERY RECORDS ===\n");
                        String delSQL = "SELECT d.DeliveryID, d.Quantity, d.DeliveryDate, c.FirstName, c.LastName "
                                + "FROM Delivery d LEFT JOIN Courier c ON d.CourierID = c.Cour_ID WHERE d.ProductID = ? ORDER BY d.DeliveryDate DESC";
                        PreparedStatement delPstmt = conn.prepareStatement(delSQL);
                        delPstmt.setInt(1, id);
                        ResultSet delRs = delPstmt.executeQuery();
                        boolean hasDel = false;
                        StringBuilder delOut = new StringBuilder();
                        delOut.append(String.format("%-12s %-10s %-15s %-30s\n", "Delivery ID", "Quantity", "Date", "Courier"));
                        delOut.append("─────────────────────────────────────────────────────────\n");
                        while (delRs.next()) {
                            hasDel = true;
                            String dateStr = delRs.getString("DeliveryDate");
                            if (dateStr != null && dateStr.length() >= 10) dateStr = dateStr.substring(0, 10);
                            String courier = ((delRs.getString("FirstName") == null) ? "" : delRs.getString("FirstName")) + " " + ((delRs.getString("LastName") == null) ? "" : delRs.getString("LastName"));
                            delOut.append(String.format("%-12d %-10d %-15s %-30s\n",
                                    delRs.getInt("DeliveryID"),
                                    delRs.getInt("Quantity"),
                                    dateStr,
                                    courier.trim()));
                        }
                        if (hasDel) output.append(delOut.toString()); else output.append("No delivery records found for this product.\n");
                        delRs.close(); delPstmt.close();

                        textArea.setText(output.toString());
                    } else {
                        textArea.setText("Product ID not found: " + productID);
                    }

                    rs.close();
                    pstmt.close();

                } catch (SQLException ex) {
                    System.err.println("Database error: " + ex.getMessage());
                    textArea.setText("Database Error: " + ex.getMessage());
                }
            }
        });
        // Finalize window appearance
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadProductList(JTextArea textArea) {
        try {
            DBManager dbManager = DBManager.getInstance();
            Connection conn = dbManager.getConnection();

            String sql = "SELECT ProductID, ProductName, Quantity FROM Product ORDER BY ProductID";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder listText = new StringBuilder();
            listText.append(String.format("%-8s %-40s %-10s\n", "ID", "Name", "Quantity"));
            listText.append("────────────────────────────────────────────────────────────\n");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                listText.append(String.format("%-8d %-40s %-10d\n",
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getInt("Quantity")));
            }

            if (!hasData) {
                listText.append("No products found in database.\n");
            }

            textArea.setText(listText.toString());

            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            System.err.println("Database error: " + ex.getMessage());
            textArea.setText("Error loading products: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductFrameView frame = new ProductFrameView();
            frame.setVisible(true);
        });
    }
}
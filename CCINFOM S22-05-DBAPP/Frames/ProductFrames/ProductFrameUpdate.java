package Frames.ProductFrames;

import Frames.DBManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class ProductFrameUpdate extends JFrame {
    private JPanel contentPane;

    public ProductFrameUpdate() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 700);
        setTitle("Update Product Information");
        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        // don't force always-on-top; allow normal window stacking

        JLabel lblProductList = new JLabel("Existing Products:");
        lblProductList.setBounds(20, 20, 200, 25);
        lblProductList.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
        contentPane.add(lblProductList);

        JTextArea productListArea = new JTextArea();
        productListArea.setEditable(false);
        productListArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JScrollPane listScrollPane = new JScrollPane(productListArea);
        listScrollPane.setBounds(20, 50, 740, 150);
        contentPane.add(listScrollPane);

        // Load products on initialization
        loadProductList(productListArea);

        JLabel lblSeparator = new JLabel("Update Form:");
        lblSeparator.setBounds(20, 220, 200, 25);
        lblSeparator.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
        contentPane.add(lblSeparator);

        JLabel lblProductID = new JLabel("Product ID to Update:");
        lblProductID.setBounds(20, 260, 250, 25);
        contentPane.add(lblProductID);

        JTextField productIdField = new JTextField();
        productIdField.setBounds(20, 290, 740, 25);
        contentPane.add(productIdField);

        JLabel lblName = new JLabel("New Product Name:");
        lblName.setBounds(20, 330, 250, 25);
        contentPane.add(lblName);

        JTextField productNameField = new JTextField();
        productNameField.setBounds(20, 360, 740, 25);
        contentPane.add(productNameField);

        JLabel lblDesc = new JLabel("New Product Description:");
        lblDesc.setBounds(20, 400, 250, 25);
        contentPane.add(lblDesc);

        JTextField productDescriptionField = new JTextField();
        productDescriptionField.setBounds(20, 430, 740, 25);
        contentPane.add(productDescriptionField);

        JLabel lblQty = new JLabel("New Quantity:");
        lblQty.setBounds(20, 470, 250, 25);
        contentPane.add(lblQty);

        JTextField quantityField = new JTextField();
        quantityField.setBounds(20, 500, 200, 25);
        contentPane.add(quantityField);

        JLabel lblCategory = new JLabel("New Category ID:");
        lblCategory.setBounds(240, 470, 250, 25);
        contentPane.add(lblCategory);

        JTextField categoryIdField = new JTextField();
        categoryIdField.setBounds(240, 500, 200, 25);
        contentPane.add(categoryIdField);

        JLabel lblWarehouse = new JLabel("New Warehouse ID:");
        lblWarehouse.setBounds(460, 470, 250, 25);
        contentPane.add(lblWarehouse);

        JTextField warehouseField = new JTextField();
        warehouseField.setBounds(460, 500, 200, 25);
        contentPane.add(warehouseField);

        JButton btnUpdate = new JButton("Update Product");
        btnUpdate.setBounds(280, 540, 200, 35);
        btnUpdate.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
        contentPane.add(btnUpdate);

        JLabel lblError = new JLabel("");
        lblError.setBounds(20, 580, 740, 30);
        lblError.setForeground(java.awt.Color.RED);
        contentPane.add(lblError);

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lblError.setText("");
                String productId = productIdField.getText().trim();
                String productName = productNameField.getText().trim();
                String productDescription = productDescriptionField.getText().trim();
                String quantity = quantityField.getText().trim();
                String categoryId = categoryIdField.getText().trim();
                String warehouseId = warehouseField.getText().trim();

                if (productId.isEmpty() || productName.isEmpty() || productDescription.isEmpty() || quantity.isEmpty() || categoryId.isEmpty() || warehouseId.isEmpty()) {
                    lblError.setText("Please fill in all fields");
                    return;
                }

                try {
                    int pid = Integer.parseInt(productId);
                    int qty = Integer.parseInt(quantity);
                    int catId = Integer.parseInt(categoryId);
                    int wid = Integer.parseInt(warehouseId);

                    DBManager dbManager = DBManager.getInstance();
                    Connection conn = dbManager.getConnection();
                    String sql = "UPDATE Product SET CategoryID = ?, WarehouseID = ?, ProductName = ?, ProductDescription = ?, Quantity = ? WHERE ProductID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, catId);
                    pstmt.setInt(2, wid);
                    pstmt.setString(3, productName);
                    pstmt.setString(4, productDescription);
                    pstmt.setInt(5, qty);
                    pstmt.setInt(6, pid);

                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        lblError.setForeground(java.awt.Color.GREEN);
                        lblError.setText("Product updated successfully!");
                        loadProductList(productListArea);
                        productIdField.setText("");
                        productNameField.setText("");
                        productDescriptionField.setText("");
                        quantityField.setText("");
                        categoryIdField.setText("");
                        warehouseField.setText("");
                    } else {
                        lblError.setForeground(java.awt.Color.RED);
                        lblError.setText("Product ID not found");
                    }
                    pstmt.close();

                } catch (NumberFormatException ex) {
                    lblError.setText("Numeric fields must be valid numbers");
                } catch (SQLException ex) {
                    lblError.setText("DB Error: " + ex.getMessage());
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

            String sql = "SELECT ProductID, ProductName, Quantity, WarehouseID FROM Product ORDER BY ProductID";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder listText = new StringBuilder();
            listText.append(String.format("%-8s %-40s %-10s %-10s\n", "ID", "Name", "Quantity", "Warehouse"));
            listText.append("────────────────────────────────────────────────────────────────────────────────\n");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                listText.append(String.format("%-8d %-40s %-10d %-10d\n",
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getInt("Quantity"),
                        rs.getInt("WarehouseID")));
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
            ProductFrameUpdate frame = new ProductFrameUpdate();
            frame.setVisible(true);
        });
    }
}
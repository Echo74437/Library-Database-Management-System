package Frames.ProductFrames;

import Frames.DBManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;
import javax.swing.*;

public class ProductFrameCreate extends JFrame {
    private JTextField productIdField;
    private JTextField productNameField;
    private JTextField productDescriptionField;
    private JTextField quantityField;
    private JComboBox<CategoryItem> categoryComboBox;
    private JTextField warehouseIdField;
    private JTextField expiryField; // yyyy-MM-dd
    private JButton createButton;
    private JButton cancelButton;
    private ProductFrame parent;

    public ProductFrameCreate() {
        setTitle("Create New Product");
        setSize(450, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Use flexible rows and 2 columns so components align neatly
        setLayout(new GridLayout(0, 2, 8, 8));

    // Product ID
    add(new JLabel("Product ID:"));
    productIdField = new JTextField();
    add(productIdField);

        // Product Name
        add(new JLabel("Product Name:"));
        productNameField = new JTextField();
        add(productNameField);

        // Product Description
        add(new JLabel("Product Description:"));
        productDescriptionField = new JTextField();
        add(productDescriptionField);

        // Quantity
        add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        add(quantityField);

    // Category
    add(new JLabel("Category:"));
    categoryComboBox = new JComboBox<>();
    add(categoryComboBox);

    // Warehouse ID
    add(new JLabel("Warehouse ID:"));
    warehouseIdField = new JTextField();
    add(warehouseIdField);

    // Expiry
    add(new JLabel("Date of Expiry (yyyy-MM-dd):"));
    expiryField = new JTextField();
    add(expiryField);

    loadCategories();

        // Create and Cancel Buttons placed in a centered panel (keeps layout aligned)
        createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createProduct();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        // Add an empty label to occupy the left cell, then the button panel on the right
        add(new JLabel(""));
        add(buttonPanel);
        // Make visible when constructed directly
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Constructor that accepts parent so we can refresh parent after creation
    public ProductFrameCreate(ProductFrame parent) {
        this();
        this.parent = parent;
    }

    private void createProduct() {
        String idText = productIdField.getText().trim();
        String productName = productNameField.getText().trim();
        String productDescription = productDescriptionField.getText().trim();
        String quantityText = quantityField.getText().trim();
        CategoryItem cat = (CategoryItem) categoryComboBox.getSelectedItem();
        String warehouseText = warehouseIdField.getText().trim();
        String expiryText = expiryField.getText().trim();

        if (idText.isEmpty() || productName.isEmpty() || productDescription.isEmpty() || quantityText.isEmpty() || cat == null || warehouseText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int productId = Integer.parseInt(idText);
            int qty = Integer.parseInt(quantityText);
            int warehouseId = Integer.parseInt(warehouseText);

            String insert = "INSERT INTO Product (ProductID, CategoryID, WarehouseID, ProductName, ProductDescription, Quantity, DateOfExpiry) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBManager.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setInt(1, productId);
                ps.setInt(2, cat.id);
                ps.setInt(3, warehouseId);
                ps.setString(4, productName);
                ps.setString(5, productDescription);
                ps.setInt(6, qty);
                if (expiryText.isEmpty()) ps.setNull(7, Types.DATE);
                else ps.setDate(7, Date.valueOf(expiryText));
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Product created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            if (parent != null) parent.loadProductData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Numeric fields must contain valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategories() {
        String sql = "SELECT CategoryID, CategoryName FROM ProductCategory ORDER BY CategoryName";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Vector<CategoryItem> items = new Vector<>();
            while (rs.next()) {
                items.add(new CategoryItem(rs.getInt("CategoryID"), rs.getString("CategoryName")));
            }
            DefaultComboBoxModel<CategoryItem> model = new DefaultComboBoxModel<>(items);
            categoryComboBox.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class CategoryItem {
        final int id;
        final String name;
        CategoryItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductFrameCreate frame = new ProductFrameCreate();
            frame.setVisible(true);
        });
    }
}
package Frames.ProductFrames;

import Frames.DBManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductFrameDelete extends JFrame {
    private JTextField productIdField;
    private JButton deleteButton;
    private JLabel messageLabel;
    private ProductFrame parent;

    public ProductFrameDelete() {
        setTitle("Delete Product");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel productIdLabel = new JLabel("Enter Product ID:");
        productIdField = new JTextField(15);
        deleteButton = new JButton("Delete Product");
        messageLabel = new JLabel("");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });

        add(productIdLabel);
        add(productIdField);
        add(deleteButton);
        add(messageLabel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Allow parent to be passed so we can refresh the product list after deletion
    public ProductFrameDelete(ProductFrame parent) {
        this();
        this.parent = parent;
    }

    private void deleteProduct() {
        String productId = productIdField.getText();
        if (productId == null || productId.trim().isEmpty()) {
            messageLabel.setText("Please enter a Product ID");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(productId.trim());
        } catch (NumberFormatException ex) {
            messageLabel.setText("Product ID must be a number");
            return;
        }

        String sql = "DELETE FROM Product WHERE ProductID = ?";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                messageLabel.setText("Product with ID " + id + " deleted successfully.");
                if (parent != null) parent.loadProductData();
            } else {
                messageLabel.setText("No product found with ID " + id);
            }
            productIdField.setText("");
        } catch (SQLException ex) {
            messageLabel.setText("Error deleting product: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductFrameDelete frame = new ProductFrameDelete();
            frame.setVisible(true);
        });
    }
}
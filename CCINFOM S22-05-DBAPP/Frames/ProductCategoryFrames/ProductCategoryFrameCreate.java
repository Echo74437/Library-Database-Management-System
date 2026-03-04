package Frames.ProductCategoryFrames;

import Frames.DBManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

public class ProductCategoryFrameCreate extends JFrame {
    private JTextField txtCategoryID;
    private JTextField txtCategoryName;
    private JButton btnSave;
    private JButton btnCancel;

    public ProductCategoryFrameCreate() {
        setTitle("Create Product Category");
        setSize(350,180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3,2,6,6));

        add(new JLabel("Category ID:"));
        txtCategoryID = new JTextField();
        add(txtCategoryID);

        add(new JLabel("Category Name:"));
        txtCategoryName = new JTextField();
        add(txtCategoryName);

        btnSave = new JButton("Save");
        btnSave.addActionListener((ActionEvent e) -> saveCategory());
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        add(btnSave);
        add(btnCancel);

        setVisible(true);
    }

    private void saveCategory() {
        String idText = txtCategoryID.getText().trim();
        String name = txtCategoryName.getText().trim();
        if (idText.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        try {
            int id = Integer.parseInt(idText);
            String sql = "INSERT INTO ProductCategory (CategoryID, CategoryName) VALUES (?, ?)";
            try (Connection conn = DBManager.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Category saved");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Category ID must be a number");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(ProductCategoryFrameCreate::new); }
}

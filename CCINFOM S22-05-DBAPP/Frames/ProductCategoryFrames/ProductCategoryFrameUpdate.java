package Frames.ProductCategoryFrames;

import Frames.DBManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class ProductCategoryFrameUpdate extends JFrame {
    private JTextField txtCategoryID;
    private JTextField txtCategoryName;
    private JButton btnLoad;
    private JButton btnUpdate;
    private JButton btnCancel;

    public ProductCategoryFrameUpdate() {
        setTitle("Update Product Category");
        setSize(360,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4,2,6,6));

        add(new JLabel("Category ID:"));
        txtCategoryID = new JTextField();
        add(txtCategoryID);

        add(new JLabel("Category Name:"));
        txtCategoryName = new JTextField();
        add(txtCategoryName);

        btnLoad = new JButton("Load");
        btnLoad.addActionListener((ActionEvent e) -> loadCategory());
        btnUpdate = new JButton("Update");
        btnUpdate.addActionListener((ActionEvent e) -> updateCategory());
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        add(btnLoad);
        add(btnUpdate);
        add(btnCancel);

        setVisible(true);
    }

    private void loadCategory() {
        String idText = txtCategoryID.getText().trim();
        if (idText.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Category ID"); return; }
        try {
            int id = Integer.parseInt(idText);
            String sql = "SELECT CategoryName FROM ProductCategory WHERE CategoryID = ?";
            try (Connection conn = DBManager.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtCategoryName.setText(rs.getString("CategoryName"));
                    } else {
                        JOptionPane.showMessageDialog(this, "No category with ID " + id);
                    }
                }
            }
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "ID must be a number"); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage()); }
    }

    private void updateCategory() {
        String idText = txtCategoryID.getText().trim();
        String name = txtCategoryName.getText().trim();
        if (idText.isEmpty() || name.isEmpty()) { JOptionPane.showMessageDialog(this, "Fill both fields"); return; }
        try {
            int id = Integer.parseInt(idText);
            String sql = "UPDATE ProductCategory SET CategoryName = ? WHERE CategoryID = ?";
            try (Connection conn = DBManager.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setInt(2, id);
                int updated = ps.executeUpdate();
                if (updated > 0) JOptionPane.showMessageDialog(this, "Category updated");
                else JOptionPane.showMessageDialog(this, "No category found with ID " + id);
            }
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "ID must be a number"); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage()); }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(ProductCategoryFrameUpdate::new); }
}

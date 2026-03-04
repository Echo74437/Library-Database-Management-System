package Frames.ProductCategoryFrames;

import Frames.DBManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

public class ProductCategoryFrameDelete extends JFrame {
    private JTextField txtCategoryID;
    private JButton btnDelete;

    public ProductCategoryFrameDelete() {
        setTitle("Delete Product Category");
        setSize(320,140);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2,2,6,6));

        add(new JLabel("Category ID:"));
        txtCategoryID = new JTextField();
        add(txtCategoryID);

        btnDelete = new JButton("Delete");
        btnDelete.addActionListener((ActionEvent e) -> deleteCategory());
        add(btnDelete);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);

        setVisible(true);
    }

    private void deleteCategory() {
        String idText = txtCategoryID.getText().trim();
        if (idText.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Category ID"); return; }
        try {
            int id = Integer.parseInt(idText);
            String sql = "DELETE FROM ProductCategory WHERE CategoryID = ?";
            try (Connection conn = DBManager.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                int affected = ps.executeUpdate();
                if (affected > 0) JOptionPane.showMessageDialog(this, "Category deleted");
                else JOptionPane.showMessageDialog(this, "No category with ID " + id);
            }
            dispose();
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "ID must be a number"); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage()); }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(ProductCategoryFrameDelete::new); }
}

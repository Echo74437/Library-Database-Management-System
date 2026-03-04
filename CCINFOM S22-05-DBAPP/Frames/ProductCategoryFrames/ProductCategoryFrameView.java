package Frames.ProductCategoryFrames;

import Frames.DBManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class ProductCategoryFrameView extends JFrame {
    private JTextField txtCategoryID;
    private JTextField txtCategoryName;
    private JButton btnView;

    public ProductCategoryFrameView() {
        setTitle("View Product Category");
        setSize(350,160);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3,2,6,6));

        add(new JLabel("Category ID:"));
        txtCategoryID = new JTextField();
        add(txtCategoryID);

        add(new JLabel("Category Name:"));
        txtCategoryName = new JTextField();
        txtCategoryName.setEditable(false);
        add(txtCategoryName);

        btnView = new JButton("View");
        btnView.addActionListener((ActionEvent e) -> viewCategory());
        add(btnView);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        add(btnClose);

        setVisible(true);
    }

    private void viewCategory() {
        String idText = txtCategoryID.getText().trim();
        if (idText.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Category ID"); return; }
        try {
            int id = Integer.parseInt(idText);
            String sql = "SELECT CategoryName FROM ProductCategory WHERE CategoryID = ?";
            try (Connection conn = DBManager.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) txtCategoryName.setText(rs.getString("CategoryName"));
                    else JOptionPane.showMessageDialog(this, "No category with ID " + id);
                }
            }
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "ID must be a number"); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage()); }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(ProductCategoryFrameView::new); }
}

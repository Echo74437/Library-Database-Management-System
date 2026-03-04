package Frames.ProductCategoryFrames;

import Frames.DBManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductCategoryFrame extends JFrame {
    private JTable tbl;
    private DefaultTableModel model;
    private JButton refreshBtn;

    public ProductCategoryFrame() {
        setTitle("Product Categories");
        setSize(600,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"CategoryID","CategoryName"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tbl = new JTable(model);

        refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener((ActionEvent e) -> loadCategories());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(refreshBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        loadCategories();
        setVisible(true);
    }

    private void loadCategories() {
        model.setRowCount(0);
        String sql = "SELECT CategoryID, CategoryName FROM ProductCategory ORDER BY CategoryName";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("CategoryID"));
                row.add(rs.getString("CategoryName"));
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductCategoryFrame::new);
    }
}

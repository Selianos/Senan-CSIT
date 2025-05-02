package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SuppliersPanel extends JPanel {
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton;

    public JTable getSuppliers() {
        return supplierTable;
    }

    public SuppliersPanel() {
        setLayout(new BorderLayout());

        String[] columns = {
                "Supplier ID", "Supplier Name", "Supplier Contact Info"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        supplierTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);

        addButton = new JButton("Add Supplier");
        deleteButton = new JButton("Delete Supplier");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddSupplierDialog());
        deleteButton.addActionListener(e -> deleteSelectedSupplier());

        // Load from database
        loadSuppliersFromDatabase();
    }

    private void loadSuppliersFromDatabase() {
        tableModel.setRowCount(0); // Clear existing rows
        try (
                Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/inventory_management", "root", "d");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT s.supplier_id, s.supplier_name, c.contact_info " + "FROM Supplier s JOIN Supplies_contact_info c ON s.supplier_id = c.supplier_id")
        ) {
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("contact_info")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load suppliers from DB", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddSupplierDialog() {
        // Similar to previous implementation, but also insert into DB
    }

    private void deleteSelectedSupplier() {
        // Similar to previous implementation, but also delete from DB
    }
}

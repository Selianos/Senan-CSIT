package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SuppliersPanel extends JPanel {
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton;

    private Connection conn;

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

        connectToDatabase();
        loadSuppliers();
    }

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/inventory_management",
                    "root",
                    "example"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.");
        }
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT S.Supplier_ID, S.Supplier_Name, C.Contact_Info " +
                    "FROM Supplier S " +
                    "JOIN Supplier_contact_info C ON S.Supplier_ID = C.Supplier_ID";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("Supplier_ID"),
                        rs.getString("Supplier_Name"),
                        rs.getString("Contact_Info")
                });
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load suppliers.");
        }
    }

    private void showAddSupplierDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Supplier ID:"));
        panel.add(idField);
        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Supplier Contact Info:"));
        panel.add(contactField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int supplierID = Integer.parseInt(idField.getText());
                String supplierName = nameField.getText().trim();
                String contactInfo = contactField.getText().trim();

                // Insert into Supplier
                String insertSupplier = "INSERT INTO Supplier (Supplier_ID, Supplier_Name) VALUES (?, ?)";
                PreparedStatement stmt1 = conn.prepareStatement(insertSupplier);
                stmt1.setInt(1, supplierID);
                stmt1.setString(2, supplierName);
                stmt1.executeUpdate();
                stmt1.close();

                // Insert into Supplier_contact_info
                String insertContact = "INSERT INTO Supplier_contact_info (Supplier_ID, Contact_Info) VALUES (?, ?)";
                PreparedStatement stmt2 = conn.prepareStatement(insertContact);
                stmt2.setInt(1, supplierID);
                stmt2.setString(2, contactInfo);
                stmt2.executeUpdate();
                stmt2.close();

                loadSuppliers();
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add supplier. Make sure ID is unique and valid.");
            }
        }
    }

    private void deleteSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this supplier?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int supplierID = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            // Delete from Supplies (foreign key constraint likely exists)
            PreparedStatement stmt0 = conn.prepareStatement("DELETE FROM Supplies WHERE Supplier_ID = ?");
            stmt0.setInt(1, supplierID);
            stmt0.executeUpdate();
            stmt0.close();

            // Delete from Supplier_contact_info
            PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM Supplier_contact_info WHERE Supplier_ID = ?");
            stmt1.setInt(1, supplierID);
            stmt1.executeUpdate();
            stmt1.close();

            // Delete from Supplier
            PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM Supplier WHERE Supplier_ID = ?");
            stmt2.setInt(1, supplierID);
            stmt2.executeUpdate();
            stmt2.close();

            loadSuppliers();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete supplier.");
        }
    }
}

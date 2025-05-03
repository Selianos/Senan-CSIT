package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SuppliersPanel extends JPanel {
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton, updateButton;

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

        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        updateButton = new JButton("Update");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddSupplierDialog());
        deleteButton.addActionListener(e -> deleteSelectedSupplier());
        updateButton.addActionListener(e -> showUpdateSupplierDialog());


        loadSuppliers();
    }

    //Loading Suppliers data into tableModel
    private void loadSuppliers() {
        tableModel.setRowCount(0);
        try {
            Connection db = InventoryDB.getConnection();
            String query = "SELECT Supplier_ID, Supplier_Name, Contact_Info FROM Supplier";
            PreparedStatement st = db.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("Supplier_ID"),
                        rs.getString("Supplier_Name"),
                        rs.getString("Contact_Info")
                });
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load suppliers.");
        }
    }

    //Adding supplier window
    private void showAddSupplierDialog() {
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1));

        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Supplier Contact Info:"));
        panel.add(contactField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String supplierName = nameField.getText().trim();
                String contactInfo = contactField.getText().trim();

                Connection db = InventoryDB.getConnection();

                // Insert into Supplier
                String insertSupplier = "INSERT INTO Supplier (Supplier_Name, contact_info) VALUES (?,?);";
                PreparedStatement stmt1 = db.prepareStatement(insertSupplier);
                stmt1.setString(1, supplierName);
                stmt1.setString(2, contactInfo);
                stmt1.executeUpdate();
                stmt1.close();

                loadSuppliers();
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    //Delete on selection
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
            Connection conn = InventoryDB.getConnection();

            // Delete from Supplier
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Supplier WHERE Supplier_ID = ?");
            stmt.setInt(1, supplierID);
            stmt.executeUpdate();
            stmt.close();

            loadSuppliers();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete supplier.");
        }
    }

    //Update on selection
    private void showUpdateSupplierDialog() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to update.");
            return;
        }

        int supplierID = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentContact = (String) tableModel.getValueAt(selectedRow, 2);

        JTextField nameField = new JTextField(currentName);
        JTextField contactField = new JTextField(currentContact);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Supplier Contact Info:"));
        panel.add(contactField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Supplier",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText().trim();
                String newContact = contactField.getText().trim();

                Connection db = InventoryDB.getConnection();

                String updateSQL = "UPDATE Supplier SET Supplier_Name = ?, Contact_Info = ? WHERE Supplier_ID = ?";
                PreparedStatement stmt = db.prepareStatement(updateSQL);
                stmt.setString(1, newName);
                stmt.setString(2, newContact);
                stmt.setInt(3, supplierID);
                stmt.executeUpdate();
                stmt.close();

                loadSuppliers();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update supplier.");
            }
        }
    }
}


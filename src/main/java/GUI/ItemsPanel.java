package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class ItemsPanel extends JPanel {
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton;
    private int idCounter = 1000;

    public ItemsPanel() {
        setLayout(new BorderLayout());

        String[] columns = {
                "ID", "Name", "Item QR Code", "Manufacturer", "Category",
                "Quantity", "Min Stock Level", "Unit Price", "Total Value",
                "Last Updated Date", "Supplier"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);

        addButton = new JButton("Add Item");
        deleteButton = new JButton("Delete Item");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddItemDialog());
        deleteButton.addActionListener(e -> handleDelete());
    }

    private void showAddItemDialog() {
        // Fetch suppliers from the database
        Vector<String> supplierList = getSupplierNamesFromDatabase();
        if (supplierList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No suppliers available. Please add suppliers first.", "No Suppliers", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField();
        JTextField qrField = new JTextField();
        JTextField manufacturerField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField minStockField = new JTextField();
        JTextField unitPriceField = new JTextField();
        JComboBox<String> supplierComboBox = new JComboBox<>(supplierList);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("QR Code:")); panel.add(qrField);
        panel.add(new JLabel("Manufacturer:")); panel.add(manufacturerField);
        panel.add(new JLabel("Category:")); panel.add(categoryField);
        panel.add(new JLabel("Quantity:")); panel.add(quantityField);
        panel.add(new JLabel("Min Stock Level:")); panel.add(minStockField);
        panel.add(new JLabel("Unit Price:")); panel.add(unitPriceField);
        panel.add(new JLabel("Supplier:")); panel.add(supplierComboBox);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add New Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String qr = qrField.getText().trim();
                String manufacturer = manufacturerField.getText().trim();
                String category = categoryField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                int minStock = Integer.parseInt(minStockField.getText().trim());
                double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
                String supplier = (String) supplierComboBox.getSelectedItem();

                double totalValue = unitPrice * quantity;
                String lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Insert item into the database
                insertItemIntoDatabase(name, qr, manufacturer, category, quantity, minStock, unitPrice, totalValue, lastUpdated, supplier);

                // After inserting, update the table
                tableModel.addRow(new Object[]{
                        idCounter++, name, qr, manufacturer, category,
                        quantity, minStock, unitPrice, totalValue, lastUpdated, supplier
                });

            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for quantity, stock level, and price.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Vector<String> getSupplierNamesFromDatabase() {
        Vector<String> names = new Vector<>();

        try (Connection conn = InventoryDB.getConnection()) {
            String query = "SELECT Supplier_Name FROM Supplier";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                names.add(rs.getString("Supplier_Name"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load suppliers from the database.");
        }

        return names;
    }

    private void insertItemIntoDatabase(String name, String qr, String manufacturer, String category, int quantity, int minStock, double unitPrice, double totalValue, String lastUpdated, String supplier) throws SQLException {
        try (Connection conn = InventoryDB.getConnection()) {
            // Get the supplier ID based on the supplier name selected from the dropdown
            String supplierQuery = "SELECT Supplier_ID FROM Supplier WHERE Supplier_Name = ?";
            PreparedStatement supplierStmt = conn.prepareStatement(supplierQuery);
            supplierStmt.setString(1, supplier);
            ResultSet rs = supplierStmt.executeQuery();

            int supplierID = -1;
            if (rs.next()) {
                supplierID = rs.getInt("Supplier_ID");
            }
            rs.close();
            supplierStmt.close();

            // Insert item into the Item table
            String insertItem = "INSERT INTO Item (item_name, item_qr_code, manufacturer, category, stock_quantity, min_stock_level, unit_price, total_value, last_updated, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertItem);
            stmt.setString(1, name);
            stmt.setString(2, qr);
            stmt.setString(3, manufacturer);
            stmt.setString(4, category);
            stmt.setInt(5, quantity);
            stmt.setInt(6, minStock);
            stmt.setDouble(7, unitPrice);
            stmt.setDouble(8, totalValue);
            stmt.setString(9, lastUpdated);
            stmt.setInt(10, supplierID);
            stmt.executeUpdate();
            stmt.close();
        }
    }

    private void handleDelete() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow != -1) {
            String itemName = tableModel.getValueAt(selectedRow, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete \"" + itemName + "\"?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}

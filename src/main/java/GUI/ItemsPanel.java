package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ItemsPanel extends JPanel {
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton,updateButton;

    public ItemsPanel() {
        setLayout(new BorderLayout());

        String[] columns = {
                "item_id", "item_name", "item_qr", "manufacturer",
                "category", "stock_quantity", "min_stock_level", "unit_price"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);

        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        updateButton = new JButton("Update");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        add(scrollPane, BorderLayout.CENTER);
        
        
        if (Staffbase.getSessionEmp() == null){  
        add(buttonPanel, BorderLayout.SOUTH);
        addButton.addActionListener(e -> showAddItemDialog());
        deleteButton.addActionListener(e -> deleteSelectedItem());
        updateButton.addActionListener(e->showUpdateItemDialog());
        }
        loadItems();
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        try {
            Connection db = InventoryDB.getConnection();
            String query = "SELECT item_id, item_name, item_qr, manufacturer, category, stock_quantity, min_stock_level, unit_price FROM Item";
            PreparedStatement st = db.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("item_qr"),
                        rs.getString("manufacturer"),
                        rs.getString("category"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("min_stock_level"),
                        rs.getDouble("unit_price")
                });
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load items.");
        }
    }

    private void showAddItemDialog() {
        JTextField nameField = new JTextField();
        JTextField qrField = new JTextField();
        JTextField manufacturerField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField minStockField = new JTextField();
        JTextField priceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Item QR:"));
        panel.add(qrField);
        panel.add(new JLabel("Manufacturer:"));
        panel.add(manufacturerField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockField);
        panel.add(new JLabel("Min Stock Level:"));
        panel.add(minStockField);
        panel.add(new JLabel("Unit Price:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String itemName = nameField.getText().trim();
                String itemQr = qrField.getText().trim();
                String manufacturer = manufacturerField.getText().trim();
                String category = categoryField.getText().trim();
                int stockQty = Integer.parseInt(stockField.getText());
                int minStock = Integer.parseInt(minStockField.getText());
                double unitPrice = Double.parseDouble(priceField.getText());

                Connection db = InventoryDB.getConnection();
                String insertItem = "INSERT INTO Item (item_name, item_qr, manufacturer, category, stock_quantity, min_stock_level, unit_price) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = db.prepareStatement(insertItem);
                stmt.setString(1, itemName);
                stmt.setString(2, itemQr);
                stmt.setString(3, manufacturer);
                stmt.setString(4, category);
                stmt.setInt(5, stockQty);
                stmt.setInt(6, minStock);
                stmt.setDouble(7, unitPrice);
                stmt.executeUpdate();
                stmt.close();

                loadItems();
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            Connection db = InventoryDB.getConnection();
            PreparedStatement stmt = db.prepareStatement("DELETE FROM Item WHERE item_id = ?");
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
            stmt.close();

            loadItems();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete item.");
        }
    }
    private void showUpdateItemDialog() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.");
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        String itemName = (String) tableModel.getValueAt(selectedRow, 1);
        String itemQr = (String) tableModel.getValueAt(selectedRow, 2);
        String manufacturer = (String) tableModel.getValueAt(selectedRow, 3);
        String category = (String) tableModel.getValueAt(selectedRow, 4);
        int stockQty = (int) tableModel.getValueAt(selectedRow, 5);
        int minStock = (int) tableModel.getValueAt(selectedRow, 6);
        double unitPrice = (double) tableModel.getValueAt(selectedRow, 7);

        JTextField nameField = new JTextField(itemName);
        JTextField qrField = new JTextField(itemQr);
        JTextField manufacturerField = new JTextField(manufacturer);
        JTextField categoryField = new JTextField(category);
        JTextField stockField = new JTextField(String.valueOf(stockQty));
        JTextField minStockField = new JTextField(String.valueOf(minStock));
        JTextField priceField = new JTextField(String.valueOf(unitPrice));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Item QR:"));
        panel.add(qrField);
        panel.add(new JLabel("Manufacturer:"));
        panel.add(manufacturerField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockField);
        panel.add(new JLabel("Min Stock Level:"));
        panel.add(minStockField);
        panel.add(new JLabel("Unit Price:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText().trim();
                String newQr = qrField.getText().trim();
                String newManufacturer = manufacturerField.getText().trim();
                String newCategory = categoryField.getText().trim();
                int newStock = Integer.parseInt(stockField.getText().trim());
                int newMinStock = Integer.parseInt(minStockField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());

                Connection db = InventoryDB.getConnection();
                String updateQuery = "UPDATE Item SET item_name = ?, item_qr = ?, manufacturer = ?, category = ?, stock_quantity = ?, min_stock_level = ?, unit_price = ? WHERE item_id = ?";
                PreparedStatement stmt = db.prepareStatement(updateQuery);
                stmt.setString(1, newName);
                stmt.setString(2, newQr);
                stmt.setString(3, newManufacturer);
                stmt.setString(4, newCategory);
                stmt.setInt(5, newStock);
                stmt.setInt(6, newMinStock);
                stmt.setDouble(7, newPrice);
                stmt.setInt(8, itemId);
                stmt.executeUpdate();
                stmt.close();

                loadItems();
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update item: " + e.getMessage());
            }
        }
    }

}

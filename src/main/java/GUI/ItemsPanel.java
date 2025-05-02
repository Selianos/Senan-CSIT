package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        // Fetch suppliers
        Vector<String> supplierList = getSupplierNames();
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
                int currentId = idCounter++;

                tableModel.addRow(new Object[]{
                        currentId, name, qr, manufacturer, category,
                        quantity, minStock, unitPrice, totalValue, lastUpdated, supplier
                });

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for quantity, stock level, and price.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Vector<String> getSupplierNames() {
        Vector<String> names = new Vector<>();

        for (Frame f : JFrame.getFrames()) {
            if (f instanceof JFrame) {
                Component[] components = ((JFrame) f).getContentPane().getComponents();
                for (Component comp : components) {
                    if (comp instanceof SuppliersPanel) {
                        JTable supplierTable = ((SuppliersPanel) comp).getSuppliers();
                        DefaultTableModel model = (DefaultTableModel) supplierTable.getModel();
                        for (int i = 0; i < model.getRowCount(); i++) {
                            names.add((String) model.getValueAt(i, 1));
                        }
                    }
                }
            }
        }

        return names;
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

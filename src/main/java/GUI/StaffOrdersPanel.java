
package GUI;

import models.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class StaffOrdersPanel extends JPanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton createOrderButton, cancelOrderButton, viewDetailsButton;
    private JComboBox<String> statusFilter;
    private Employee currentEmployee;

    public StaffOrdersPanel() {
        
        this.currentEmployee = Staffbase.getSessionEmp();
        
        setLayout(new BorderLayout());

        // Define columns
        String[] columns = {
                "Order ID", "Date", "Total Cost", "Status"
        };

        // Create table model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        // Status filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Pending", "Completed", "Cancelled"});
        filterPanel.add(statusFilter);

        // Buttons panel
        createOrderButton = new JButton("Create Order");
        cancelOrderButton = new JButton("Cancel Order");
        viewDetailsButton = new JButton("View Details");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createOrderButton);
        buttonPanel.add(cancelOrderButton);
        buttonPanel.add(viewDetailsButton);

        // Add components to main panel
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Setup listeners
        setupListeners();

        // Initial load of orders
        loadOrders("All");
    }

    // Setup action listeners
    private void setupListeners() {
        // Create order button
        createOrderButton.addActionListener(e -> showCreateOrderDialog());

        // Cancel order button
        cancelOrderButton.addActionListener(e -> cancelSelectedOrder());

        // View details button
        viewDetailsButton.addActionListener(e -> viewOrderDetails());

        // Status filter listener
        statusFilter.addActionListener(e -> loadOrders((String) statusFilter.getSelectedItem()));
    }

    // Load orders for current employee
    private void loadOrders(String status) {
        tableModel.setRowCount(0);
        try {
            Connection db = InventoryDB.getConnection();
            PreparedStatement st;
            
            if ("All".equals(status)) {
                // Query for all orders of current employee
                String query = "SELECT order_id, order_date, total_price, status " +
                               "FROM orders " +
                               "WHERE emp_id = ? " +
                               "ORDER BY order_date DESC";
                st = db.prepareStatement(query);
                st.setInt(1, currentEmployee.getEmpId());
            } else {
                // Query for orders with specific status
                String query = "SELECT order_id, order_date, total_price, status " +
                               "FROM orders " +
                               "WHERE emp_id = ? AND status = ? " +
                               "ORDER BY order_date DESC";
                st = db.prepareStatement(query);
                st.setInt(1, currentEmployee.getEmpId());
                st.setString(2, status);
            }

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getDate("order_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status")
                });
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load orders.");
        }
    }

    // Create order dialog (similar to existing implementation)
    private void showCreateOrderDialog() {
        // Similar to existing createOrderDialog method
        // But ensure it uses current employee's ID when creating order
      
        (new OrdersPanel()).showCreateOrderDialog(currentEmployee);
    }

    // Cancel selected order (only for pending orders)
    private void cancelSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel.");
            return;
        }

        String orderStatus = (String) tableModel.getValueAt(selectedRow, 3);
        
        // Only allow cancelling pending orders
        if (!"Pending".equals(orderStatus)) {
            JOptionPane.showMessageDialog(this, 
                "Only pending orders can be cancelled.", 
                "Cancel Order", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to cancel this order?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Connection db = InventoryDB.getConnection();
            
            // Update order status to 'Cancelled'
            PreparedStatement updateOrder = db.prepareStatement(
                    "UPDATE orders SET status = 'Cancelled' WHERE order_id = ?");
            updateOrder.setInt(1, orderId);
            updateOrder.executeUpdate();
            updateOrder.close();
            
            // Refresh orders list
            loadOrders((String) statusFilter.getSelectedItem());
            
            JOptionPane.showMessageDialog(this, "Order cancelled successfully.");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to cancel order.");
        }
    }

    // View order details
   private void viewOrderDetails() {
    int selectedRow = orderTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an order to view.");
        return;
    }

    int orderId = (int) tableModel.getValueAt(selectedRow, 0);
    
    try {
        Connection db = InventoryDB.getConnection();
        
        // Fetch order details query with scrollable ResultSet
        String query = "SELECT o.order_id, o.order_date, o.total_price, o.status, " +
                       "i.item_name, oi.quantity, i.unit_price " +
                       "FROM orders o " +
                       "JOIN order_items oi ON o.order_id = oi.order_id " +
                       "JOIN item i ON oi.item_id = i.item_id " +
                       "WHERE o.order_id = ?";
        
        PreparedStatement stmt = db.prepareStatement(
            query, 
            ResultSet.TYPE_SCROLL_INSENSITIVE, 
            ResultSet.CONCUR_READ_ONLY
        );
        stmt.setInt(1, orderId);
        ResultSet rs = stmt.executeQuery();
        
        // Prepare order details dialog
        JPanel detailPanel = new JPanel(new BorderLayout());
        
        // Order header information
        JPanel headerPanel = new JPanel(new GridLayout(0, 2));
        
        // Use first() instead of next()
        if (rs.first()) {
            headerPanel.add(new JLabel("Order ID:"));
            headerPanel.add(new JLabel(String.valueOf(rs.getInt("order_id"))));
            headerPanel.add(new JLabel("Date:"));
            headerPanel.add(new JLabel(rs.getDate("order_date").toString()));
            headerPanel.add(new JLabel("Total Cost:"));
            headerPanel.add(new JLabel(String.format("SAR %.2f", rs.getDouble("total_price"))));
            headerPanel.add(new JLabel("Status:"));
            headerPanel.add(new JLabel(rs.getString("status")));
            
            // Prepare items table
            Vector<String> columnNames = new Vector<>();
            columnNames.add("Item");
            columnNames.add("Quantity");
            columnNames.add("Unit Price");
            columnNames.add("Total");
            
            Vector<Vector<Object>> data = new Vector<>();
            
            // Move cursor before first row
            rs.beforeFirst();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("item_name"));
                int quantity = rs.getInt("quantity");
                double unitPrice = rs.getDouble("unit_price");
                row.add(quantity);
                row.add(String.format("SAR %.2f", unitPrice));
                row.add(String.format("SAR %.2f", quantity * unitPrice));
                data.add(row);
            }
            
            JTable itemsTable = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(itemsTable);
            
            // Combine panels
            detailPanel.add(headerPanel, BorderLayout.NORTH);
            detailPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Show dialog
            JOptionPane.showMessageDialog(this, detailPanel, 
                    "Order Details", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Order not found.");
        }
        
        rs.close();
        stmt.close();
        
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load order details.");
    }
}
}

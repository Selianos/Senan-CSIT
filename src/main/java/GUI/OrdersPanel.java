
package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import models.Employee ;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;


public class OrdersPanel extends JPanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton, viewDetailsButton ,updateStatusButton;
    private JComboBox<String> statusFilter;

    
    public OrdersPanel() {
        setLayout(new BorderLayout());

        String[] columns = {
                "Order ID", "Date", "Employee", "Total Cost", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Pending", "Completed", "Cancelled"});
        filterPanel.add(statusFilter);

        
        addButton = new JButton("Create Order");
        deleteButton = new JButton("Delete Order");
        viewDetailsButton = new JButton("View Details");
        updateStatusButton = new JButton("Update Status");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(updateStatusButton);

        
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        
        addButton.addActionListener(e -> showCreateOrderDialog());
        deleteButton.addActionListener(e -> deleteSelectedOrder());
        viewDetailsButton.addActionListener(e -> viewOrderDetails());
        updateStatusButton.addActionListener(e -> updateOrderStatus()); 
        statusFilter.addActionListener(e -> loadOrders((String) statusFilter.getSelectedItem()));

        // Initial load
        loadOrders("All");
    }

    private void loadOrders(String statusFilter) {
        tableModel.setRowCount(0);
        try {
            Connection db = InventoryDB.getConnection();
            String query;
            PreparedStatement st;
            
            if (statusFilter.equals("All")) {
                query = "SELECT o.order_id, o.order_date, " +
                        "CONCAT(e.first_name, ' ', e.last_name) as Employee_name, " +
                        "o.total_price, o.status " +
                        "FROM orders o " +
                        "JOIN Employee e ON o.emp_id = e.emp_id " +
                        "ORDER BY o.order_date DESC";
                st = db.prepareStatement(query);
            } else {
                query = "SELECT o.order_id, o.order_date," +
                        "CONCAT(e.first_name, ' ', e.last_name) as Employee_name, " +
                        "o.total_price, o.status " +
                        "FROM orders o " + 
                        "JOIN Employee e ON o.emp_id = e.emp_id " +
                        "WHERE o.status = ? " +
                        "ORDER BY o.order_date DESC";
                st = db.prepareStatement(query);
                st.setString(1, statusFilter);
            }

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getDate("order_date"),
                        rs.getString("employee_name"),
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
    
     private void updateOrderStatus() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update.");
            return;
        }
        
        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);
        
        // Create status selection combo box
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});
        statusCombo.setSelectedItem(currentStatus);
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Current Status:"));
        panel.add(new JLabel(currentStatus));
        panel.add(new JLabel("New Status:"));
        panel.add(statusCombo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Update Order Status", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) statusCombo.getSelectedItem();
            
            // Only proceed if status has changed
            if (!newStatus.equals(currentStatus)) {
                try {
                    Connection db = InventoryDB.getConnection();
                    db.setAutoCommit(false);
                    
                    try {
                        // Update order status
                        PreparedStatement updateStatus = db.prepareStatement(
                                "UPDATE orders SET status = ? WHERE order_id = ?");
                        updateStatus.setString(1, newStatus);
                        updateStatus.setInt(2, orderId);
                        updateStatus.executeUpdate();
                        
                        // If changing to "Completed", update inventory quantities
                        if (newStatus.equals("Completed") && !currentStatus.equals("Completed")) {
                            updateInventoryForOrder(db, orderId, true);
                        }
                        // If changing from "Completed" to something else, reverse the inventory changes
                        else if (currentStatus.equals("Completed") && !newStatus.equals("Completed")) {
                            updateInventoryForOrder(db, orderId, false);
                        }
                        
                        db.commit();
                        JOptionPane.showMessageDialog(this, "Order status updated successfully!");
                        loadOrders((String) statusFilter.getSelectedItem());
                        
                    } catch (SQLException ex) {
                        db.rollback();
                        throw ex;
                    } finally {
                        db.setAutoCommit(true);
                    }
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to update order status: " + e.getMessage());
                }
            }
        }
    }
    
     
     private void updateInventoryForOrder(Connection db, int orderId, boolean isCompleted) throws SQLException {
            String query = "SELECT item_id, quantity FROM order_items WHERE order_id = ?";
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                int quantity = rs.getInt("quantity");

                // If completing order, subtract from inventory; if un-completing, add back to inventory
                int adjustmentFactor = isCompleted ? -1 : 1;

                PreparedStatement updateStmt = db.prepareStatement(
                        "UPDATE item SET stock_quantity = stock_quantity + ? WHERE item_id = ?");
                updateStmt.setInt(1, quantity * adjustmentFactor);
                updateStmt.setInt(2, itemId);
                updateStmt.executeUpdate();
                updateStmt.close();
            }

            rs.close();
            stmt.close();
    }
     
     
    public void  showCreateOrderDialog(Employee emp){
        
        // Create the order form panel
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        
        // Prepare item selection for order details
        JTable itemsTable = new JTable(new DefaultTableModel(
                new Object[]{"Item", "Quantity", "Unit Price", "Total"}, 0));
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setPreferredSize(new Dimension(400, 150));
        
        // Add button to add items to the order
        JButton addItemButton = new JButton("Add Item");
        
        // Order date (default to current date)
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //JTextField dateField = new JTextField(dateFormat.format(new Date()));
        

        // Add components to panel
        //panel.add(new JLabel("Date:"));
       // panel.add(dateField);
        
        
        // Create main dialog panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(panel, BorderLayout.NORTH);
        
        // Add items table section
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.add(new JLabel("Order Items:"), BorderLayout.NORTH);
        itemsPanel.add(itemsScroll, BorderLayout.CENTER);
        itemsPanel.add(addItemButton, BorderLayout.SOUTH);
        mainPanel.add(itemsPanel, BorderLayout.CENTER);
        
        // Total cost display
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total: SAR0.00");
        totalPanel.add(totalLabel);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);
        
        // Add item button action
        final DefaultTableModel dtm = (DefaultTableModel) itemsTable.getModel();
        addItemButton.addActionListener(e -> {
            // Show item selection dialog
            JComboBox<ComboItem> itemCombo = new JComboBox<>();
            JTextField quantityField = new JTextField("1");
            JPanel itemPanel = new JPanel(new GridLayout(0, 2));
            
            // Load items for selection
            loadItems(itemCombo);
            
            itemPanel.add(new JLabel("Item:"));
            itemPanel.add(itemCombo);
            itemPanel.add(new JLabel("Quantity:"));
            itemPanel.add(quantityField);
            
            int result = JOptionPane.showConfirmDialog(
                    this, itemPanel, "Add Item", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION && itemCombo.getSelectedItem() != null) {
                try {
                    ComboItem selectedItem = (ComboItem) itemCombo.getSelectedItem();
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    
                    // Get item price from database
                    double unitPrice = getItemPrice(selectedItem.getId());
                    double totalPrice = unitPrice * quantity;
                    
                    // Add to table
                    dtm.addRow(new Object[]{
                            selectedItem.toString(),
                            quantity,
                            String.format("%.2f", unitPrice),
                            String.format("%.2f", totalPrice)
                    });
                    
                    // Update total
                    updateTotal(itemsTable, totalLabel);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Please enter a valid quantity.");
                }
            }
        });
        
        // Show the create order dialog
        int dialogResult = JOptionPane.showConfirmDialog(
                this, mainPanel, "Create New Order", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (dialogResult == JOptionPane.OK_OPTION && dtm.getRowCount() > 0) {
            try {
                // Parse input fields 
                //String status = (String) statusCombo.getSelectedItem();
               // Date orderDate = dateFormat.parse(dateField.getText().trim());
                
                // Calculate total
                double total = calculateTotal(itemsTable);
                     
                // Get current user ID 
                int employeeId =  Staffbase.getSessionEmp().getEmpId() ;
                
                // Begin transaction
                Connection db = InventoryDB.getConnection();
                db.setAutoCommit(false);
                
                try {
                    // 1. Insert order header
                    String insertOrder = "INSERT INTO orders (emp_id, order_date, total_price, status) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = db.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, employeeId);
                    stmt.setDate(2, new java.sql.Date(new Date().getTime()));
                    stmt.setDouble(3, total);
                    stmt.setString(4, "Pending");
                    stmt.executeUpdate(); 
                    
                    // Get generated order ID
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    int orderId;
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                    
                    // 2. Insert order items
                    for (int i = 0; i < dtm.getRowCount(); i++) {
                        // Parse item data
                        String itemName = dtm.getValueAt(i, 0).toString();
                        int itemId = getItemIdByName(itemName);
                        int quantity = Integer.parseInt(dtm.getValueAt(i, 1).toString());
                        String unitPriceStr = dtm.getValueAt(i, 2).toString().replace("SAR", "");
                        double unitPrice = Double.parseDouble(unitPriceStr);
                        
 
                        // Insert order detail
                        String insertDetail = "INSERT INTO order_items (order_id, item_id, quantity) VALUES (?, ?, ?)";
                        PreparedStatement detailStmt = db.prepareStatement(insertDetail);
                        detailStmt.setInt(1, orderId);
                        detailStmt.setInt(2, itemId);
                        detailStmt.setInt(3, quantity);
                        detailStmt.executeUpdate();
                        detailStmt.close();
    
                    }
                    
                    // Commit transaction
                    db.commit();
                    
                    // Refresh orders list
                    loadOrders((String) statusFilter.getSelectedItem());
                    
                    JOptionPane.showMessageDialog(this, "Order created successfully!");
                    
                } catch (SQLException ex) {
                    // Roll back transaction on error
                    try {
                        db.rollback();
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                    
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error creating order: " + ex.getMessage());
                } finally {
                    db.setAutoCommit(true);
                }    
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
 
    }


    private void showCreateOrderDialog() {
        // Create the order form panel
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        
        // Prepare item selection for order details
        JTable itemsTable = new JTable(new DefaultTableModel(
                new Object[]{"Item", "Quantity", "Unit Price", "Total"}, 0));
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setPreferredSize(new Dimension(400, 150));
        
        // Add button to add items to the order
        JButton addItemButton = new JButton("Add Item");
        
        // Order date (default to current date)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JTextField dateField = new JTextField(dateFormat.format(new Date()));
        
        // Status selection
        JComboBox<String> statusCombo = new JComboBox<>(
                new String[]{"Pending","Completed", "Cancelled"});
        
        // Add components to panel
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        
        // Create main dialog panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(panel, BorderLayout.NORTH);
        
        // Add items table section
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.add(new JLabel("Order Items:"), BorderLayout.NORTH);
        itemsPanel.add(itemsScroll, BorderLayout.CENTER);
        itemsPanel.add(addItemButton, BorderLayout.SOUTH);
        mainPanel.add(itemsPanel, BorderLayout.CENTER);
        
        // Total cost display
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total: SAR0.00");
        totalPanel.add(totalLabel);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);
        
        // Add item button action
        final DefaultTableModel dtm = (DefaultTableModel) itemsTable.getModel();
        addItemButton.addActionListener(e -> {
            // Show item selection dialog
            JComboBox<ComboItem> itemCombo = new JComboBox<>();
            JTextField quantityField = new JTextField("1");
            JPanel itemPanel = new JPanel(new GridLayout(0, 2));
            
            // Load items for selection
            loadItems(itemCombo);
            
            itemPanel.add(new JLabel("Item:"));
            itemPanel.add(itemCombo);
            itemPanel.add(new JLabel("Quantity:"));
            itemPanel.add(quantityField);
            
            int result = JOptionPane.showConfirmDialog(
                    this, itemPanel, "Add Item", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION && itemCombo.getSelectedItem() != null) {
                try {
                    ComboItem selectedItem = (ComboItem) itemCombo.getSelectedItem();
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    
                    // Get item price from database
                    double unitPrice = getItemPrice(selectedItem.getId());
                    double totalPrice = unitPrice * quantity;
                    
                    // Add to table
                    dtm.addRow(new Object[]{
                            selectedItem.toString(),
                            quantity,
                            String.format("%.2f", unitPrice),
                            String.format("%.2f", totalPrice)
                    });
                    
                    // Update total
                    updateTotal(itemsTable, totalLabel);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Please enter a valid quantity.");
                }
            }
        });
        
        // Show the create order dialog
        int dialogResult = JOptionPane.showConfirmDialog(
                this, mainPanel, "Create New Order", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (dialogResult == JOptionPane.OK_OPTION && dtm.getRowCount() > 0) {
            try {
                // Parse input fields 
                String status = (String) statusCombo.getSelectedItem();
                Date orderDate = dateFormat.parse(dateField.getText().trim());
                
                // Calculate total
                double total = calculateTotal(itemsTable);
                     
                // Get current user ID 
                int employeeId =  base.getSessionEmp().getEmpId() ;
                
                // Begin transaction
                Connection db = InventoryDB.getConnection();
                db.setAutoCommit(false);
                
                try {
                    // 1. Insert order header
                    String insertOrder = "INSERT INTO orders (emp_id, order_date, total_price, status) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = db.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, employeeId);
                    stmt.setDate(2, new java.sql.Date(orderDate.getTime()));
                    stmt.setDouble(3, total);
                    stmt.setString(4, status);
                    stmt.executeUpdate(); 
                    
                    // Get generated order ID
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    int orderId;
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                    
                    // 2. Insert order items
                    for (int i = 0; i < dtm.getRowCount(); i++) {
                        // Parse item data
                        String itemName = dtm.getValueAt(i, 0).toString();
                        int itemId = getItemIdByName(itemName);
                        int quantity = Integer.parseInt(dtm.getValueAt(i, 1).toString());
                        String unitPriceStr = dtm.getValueAt(i, 2).toString().replace("SAR", "");
                        double unitPrice = Double.parseDouble(unitPriceStr);
                        
                        if (status.equals("Completed")) {
                            int currentStock = getItemStock(db, itemId);
                            if (currentStock < quantity) {
                                throw new SQLException("Insufficient stock for item: " + itemName);
                            }
                        }
                        
                        // Insert order detail
                        String insertDetail = "INSERT INTO order_items (order_id, item_id, quantity) VALUES (?, ?, ?)";
                        PreparedStatement detailStmt = db.prepareStatement(insertDetail);
                        detailStmt.setInt(1, orderId);
                        detailStmt.setInt(2, itemId);
                        detailStmt.setInt(3, quantity);
                        detailStmt.executeUpdate();
                        detailStmt.close();
                        
                        // Update inventory if order is completed
                        if (status.equals("Completed")) {
                            // For sales, subtract from inventory (negative quantity adjustment)
                            updateInventory(db, itemId, -quantity);

                            // For sales, verify we have enough stock
                            int currentStock = getItemStock(db, itemId);
                            if (currentStock < quantity) {
                                throw new SQLException("Insufficient stock for item: " + itemName);
                        }
                        }
                    }
                    
                    // Commit transaction
                    db.commit();
                    
                    // Refresh orders list
                    loadOrders((String) statusFilter.getSelectedItem());
                    
                    JOptionPane.showMessageDialog(this, "Order created successfully!");
                    
                } catch (SQLException ex) {
                    // Roll back transaction on error
                    try {
                        db.rollback();
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                    
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error creating order: " + ex.getMessage());
                } finally {
                    db.setAutoCommit(true);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }
    
    
    private int getItemStock(Connection db, int itemId) throws SQLException {
        String query = "SELECT stock_quantity FROM item WHERE item_id = ?";
        PreparedStatement st = db.prepareStatement(query);
        st.setInt(1, itemId);
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            int stock = rs.getInt("stock_quantity");
            rs.close();
            st.close();
            return stock;
        }
        
        rs.close();
        st.close();
        return 0;
    }
    
    private void deleteSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this order?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        String orderStatus = (String) tableModel.getValueAt(selectedRow, 4);
        
        try {
            Connection db = InventoryDB.getConnection();
            db.setAutoCommit(false);
            
            try {
                // If order was completed, reverse the inventory changes
                if ("Completed".equals(orderStatus)) {
                    updateInventoryForOrder(db, orderId, false);
                }

                // First delete order items
                
                PreparedStatement deleteItems = db.prepareStatement(
                        "DELETE FROM order_items WHERE order_id = ?");
                deleteItems.setInt(1, orderId);
                deleteItems.executeUpdate();
                deleteItems.close();
                
                // Then delete order header
                PreparedStatement deleteOrder = db.prepareStatement(
                        "DELETE FROM orders WHERE order_id = ?");
                deleteOrder.setInt(1, orderId);
                deleteOrder.executeUpdate();
                deleteOrder.close();
                
                db.commit();
                loadOrders((String) statusFilter.getSelectedItem());
                
            } catch (SQLException ex) {
                db.rollback();
                throw ex;
            } finally {
                db.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete order.");
        }
    }
    
    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view.");
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Connection db = InventoryDB.getConnection();
            
            // Fetch order header details
            String headerQuery = "SELECT o.order_id, o.order_date," +
                    "CONCAT(e.first_name, ' ', e.last_name) as employee_name, " +
                    "o.total_price, o.status " +
                    "FROM orders o " +
                    "JOIN employee e ON o.emp_id = e.emp_id " +
                    "WHERE o.order_id = ?";
            
            PreparedStatement headerStmt = db.prepareStatement(headerQuery);
            headerStmt.setInt(1, orderId);
            ResultSet headerRs = headerStmt.executeQuery();
            
            if (headerRs.next()) {
                // Create detail panel
                JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
                
                // Order header info
                JPanel headerPanel = new JPanel(new GridLayout(0, 2, 5, 5));
                headerPanel.add(new JLabel("Order ID:"));
                headerPanel.add(new JLabel(String.valueOf(headerRs.getInt("order_id"))));
                headerPanel.add(new JLabel("Date:"));
                headerPanel.add(new JLabel(String.valueOf(headerRs.getDate("order_date")))) ; 
                headerPanel.add(new JLabel("Employee:"));
                headerPanel.add(new JLabel(headerRs.getString("employee_name")));
                headerPanel.add(new JLabel("Status:"));
                headerPanel.add(new JLabel(headerRs.getString("status")));
                headerPanel.add(new JLabel("Total Cost:"));
                headerPanel.add(new JLabel(String.format("SAR%.2f", headerRs.getDouble("total_price"))));
                
                // Order items
                String itemsQuery = "SELECT i.item_name, oi.quantity, i.unit_price " +
                        "FROM order_items oi " +
                        "JOIN item i ON oi.item_id = i.item_id " +
                        "WHERE oi.order_id = ?";
                
                PreparedStatement itemsStmt = db.prepareStatement(itemsQuery);
                itemsStmt.setInt(1, orderId);
                ResultSet itemsRs = itemsStmt.executeQuery();
                
                String[] columns = {"Item", "Quantity", "Unit Price", "Total"};
                DefaultTableModel dtm = new DefaultTableModel(columns, 0);
                
                while (itemsRs.next()) {
                    String itemName = itemsRs.getString("item_name");
                    int quantity = itemsRs.getInt("quantity");
                    double unitPrice = itemsRs.getDouble("unit_price");
                    double total = quantity * unitPrice;
                    
                    dtm.addRow(new Object[]{
                            itemName,
                            quantity,
                            String.format("SAR%.2f", unitPrice),
                            String.format("SAR%.2f", total)
                    });
                }
                
                JTable itemsTable = new JTable(dtm);
                JScrollPane scrollPane = new JScrollPane(itemsTable);
                
  
                JButton printButton = new JButton("Print to Image");
                printButton.addActionListener(e -> {
                saveOrderDetailsAsImage(detailPanel, orderId);
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(printButton);
                
                // Add components to main panel
                detailPanel.add(headerPanel, BorderLayout.NORTH);
                detailPanel.add(scrollPane, BorderLayout.CENTER);
                detailPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                // Show dialog
                JOptionPane.showMessageDialog(this, detailPanel, 
                        "Order Details", JOptionPane.PLAIN_MESSAGE);
                
                // Clean up
                itemsRs.close();
                itemsStmt.close();
            } else {
                JOptionPane.showMessageDialog(this, "Order not found.");
            }
            
            headerRs.close();
            headerStmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load order details.");
        }
    }
    
    // Helper methods
    private void loadSuppliers(JComboBox<ComboItem> combo) {
        try {
            Connection db = InventoryDB.getConnection();
            String query = "SELECT supplier_id, supplier_name FROM supplier ORDER BY supplier_name";
            PreparedStatement st = db.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                combo.addItem(new ComboItem(
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name")
                ));
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadItems(JComboBox<ComboItem> combo) {
        try {
            Connection db = InventoryDB.getConnection();
            String query = "SELECT item_id, item_name FROM item ORDER BY item_name";
            PreparedStatement st = db.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                combo.addItem(new ComboItem(
                        rs.getInt("item_id"),
                        rs.getString("item_name")
                ));
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private double getItemPrice(int itemId) {
        try {
            Connection db = InventoryDB.getConnection();
            String query = "SELECT unit_price FROM item WHERE item_id = ?";
            PreparedStatement st = db.prepareStatement(query);
            st.setInt(1, itemId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                double price = rs.getDouble("unit_price");
                rs.close();
                st.close();
                return price;
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    private int getItemIdByName(String name) {
        try {
            // Extract item name from "ID - Name" format if needed
            if (name.contains(" - ")) {
                name = name.substring(name.indexOf(" - ") + 3);
            }
            
            Connection db = InventoryDB.getConnection();
            String query = "SELECT item_id FROM item WHERE item_name = ?";
            PreparedStatement st = db.prepareStatement(query);
            st.setString(1, name);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("item_id");
                rs.close();
                st.close();
                return id;
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private void updateTotal(JTable table, JLabel totalLabel) {
        double total = calculateTotal(table);
        totalLabel.setText(String.format("Total: SAR%.2f", total));
    }
    
    private double calculateTotal(JTable table) {
        double total = 0.0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String totalStr = model.getValueAt(i, 3).toString().replace("$", "");
            total += Double.parseDouble(totalStr);
        }
        
        return total;
    }
    
    
    private void updateInventory(Connection db, int itemId, int quantity) throws SQLException {
        String query = "UPDATE item SET stock_quantity = stock_quantity + ? WHERE item_id = ?";
        PreparedStatement st = db.prepareStatement(query);
        st.setInt(1, quantity);
        st.setInt(2, itemId);
        int affectedRows = st.executeUpdate();
        st.close();
        
        if (affectedRows == 0) {
            throw new SQLException("Failed to update inventory for item ID: " + itemId);
        }
    }
    
    // Helper class for combo boxes
    private static class ComboItem {
        private int id;
        private String display;
        
        public ComboItem(int id, String display) {
            this.id = id;
            this.display = display;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return display;
        }
    }
    
    private void saveOrderDetailsAsImage(JPanel detailsPanel, int orderId) {
    int width = detailsPanel.getWidth();
    int height = detailsPanel.getHeight();
    
    if (width <= 0) width = 800;
    if (height <= 0) height = 600;
    
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = image.createGraphics();
    
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    
    detailsPanel.paint(g2d);
    g2d.dispose();
    
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Order Details");
    fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
    fileChooser.setSelectedFile(new File("Order_" + orderId + ".png"));
    
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getAbsolutePath() + ".png");
        }
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
            }
            
            JOptionPane.showMessageDialog(detailsPanel, 
                "Order details saved successfully to " + file.getName(), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(detailsPanel, 
                "Error saving image: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
}
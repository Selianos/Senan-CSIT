package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EmployeesPanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton, editEmployeeButton;
    private void showAddEmployeeDialog() {

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField middleNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Staff"});
        JTextField shareField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Username:"));     panel.add(usernameField);
        panel.add(new JLabel("Password:"));     panel.add(passwordField);
        panel.add(new JLabel("First Name:"));   panel.add(firstNameField);
        panel.add(new JLabel("Middle Name:"));  panel.add(middleNameField);
        panel.add(new JLabel("Last Name:"));    panel.add(lastNameField);
        panel.add(new JLabel("Role:"));         panel.add(roleBox);
        panel.add(new JLabel("Share (0-100):"));panel.add(shareField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Employee",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String firstName = firstNameField.getText().trim();
                String middleName = middleNameField.getText().trim().isEmpty() ? null : middleNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String role = roleBox.getSelectedItem().toString();
                double share = Double.parseDouble(shareField.getText().trim());

                if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    throw new IllegalArgumentException("All fields except Middle Name must be filled.");
                }

                if (share < 0 || share > 100) {
                    throw new NumberFormatException("Share must be between 0 and 100.");
                }

                try ( Connection db = InventoryDB.getConnection();
                      PreparedStatement pstmt = db.prepareStatement(
                              "INSERT INTO Employee (Username, Password, First_name, Middle_name, Last_name, Role, Share) VALUES (?, ?, ?, ?, ?, ?, ?)",
                              Statement.RETURN_GENERATED_KEYS)) {

                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.setString(3, firstName);
                    pstmt.setString(4, middleName);
                    pstmt.setString(5, lastName);
                    pstmt.setString(6, role);
                    pstmt.setDouble(7, share);
                    pstmt.executeUpdate();

                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        tableModel.addRow(new Object[]{newId, username, password, firstName, middleName, lastName, role, share});
                    }

                    JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding employee to database!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public EmployeesPanel() {
        setLayout(new BorderLayout());

        // Column names with ID added
        String[] columns = {"ID", "Username", "Password", "First Name", "Middle Name", "Last Name", "Role", "Share"};

        // Table model and table setup
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make ID column read-only
            }
        };

        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        // Buttons
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editEmployeeButton = new JButton("Update");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editEmployeeButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load existing employees from the database
        loadEmployees();

        addButton.addActionListener(e -> showAddEmployeeDialog());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());
        editEmployeeButton.addActionListener(e -> showEditEmployeeDialog()); // Added action listener
    }

    private void showEditEmployeeDialog() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            String username = tableModel.getValueAt(selectedRow, 1).toString();
            String password = tableModel.getValueAt(selectedRow, 2).toString();
            String firstName = tableModel.getValueAt(selectedRow, 3).toString();
            String middleName = tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "";
            String lastName = tableModel.getValueAt(selectedRow, 5).toString();
            String role = tableModel.getValueAt(selectedRow, 6).toString();
            double share = Double.parseDouble(tableModel.getValueAt(selectedRow, 7).toString());

            JTextField usernameField = new JTextField(username);
            JPasswordField passwordField = new JPasswordField(password);
            JTextField firstNameField = new JTextField(firstName);
            JTextField middleNameField = new JTextField(middleName);
            JTextField lastNameField = new JTextField(lastName);
            JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Staff"});
            roleBox.setSelectedItem(role);
            JTextField shareField = new JTextField(String.valueOf(share));

            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Username:")); panel.add(usernameField);
            panel.add(new JLabel("Password:")); panel.add(passwordField);
            panel.add(new JLabel("First Name:")); panel.add(firstNameField);
            panel.add(new JLabel("Middle Name:")); panel.add(middleNameField);
            panel.add(new JLabel("Last Name:")); panel.add(lastNameField);
            panel.add(new JLabel("Role:")); panel.add(roleBox);
            panel.add(new JLabel("Share:")); panel.add(shareField);

            int result = JOptionPane.showConfirmDialog(
                    this, panel, "Edit Employee", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                updateEmployee(selectedRow, usernameField.getText(), new String(passwordField.getPassword()),
                        firstNameField.getText(), middleNameField.getText(), lastNameField.getText(),
                        roleBox.getSelectedItem().toString(), Double.parseDouble(shareField.getText()));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateEmployee(int row, String username, String password, String firstName,
                                String middleName, String lastName, String role, double share) {
        int empId = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = InventoryDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE Employee SET Username = ?, Password = ?, First_name = ?, Middle_name = ?, Last_name = ?, Role = ?, Share = ? WHERE EMP_ID = ?")) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, firstName);
            pstmt.setString(4, middleName.isEmpty() ? null : middleName);
            pstmt.setString(5, lastName);
            pstmt.setString(6, role);
            pstmt.setDouble(7, share);
            pstmt.setInt(8, empId);
            pstmt.executeUpdate();

            // Update JTable data
            tableModel.setValueAt(username, row, 1);
            tableModel.setValueAt(password, row, 2);
            tableModel.setValueAt(firstName, row, 3);
            tableModel.setValueAt(middleName, row, 4);
            tableModel.setValueAt(lastName, row, 5);
            tableModel.setValueAt(role, row, 6);
            tableModel.setValueAt(share, row, 7);

            JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee!", "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadEmployees() {
        try (Connection db = InventoryDB.getConnection();
             Statement stmt = db.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Employee")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("EMP_ID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("First_name"),
                        rs.getString("Middle_name"),
                        rs.getString("Last_name"),
                        rs.getString("Role"),
                        rs.getDouble("Share")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employees!", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            int empId = (int) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to delete this employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection db = InventoryDB.getConnection();
                     PreparedStatement pstmt = db.prepareStatement("DELETE FROM Employee WHERE EMP_ID = ?")) {
                    pstmt.setInt(1, empId);
                    pstmt.executeUpdate();

                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting employee!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}
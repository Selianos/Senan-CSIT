package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EmployeesPanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton;
    private int idCounter = 1000; // Auto-incrementing ID starting at 1000

    public EmployeesPanel() {
        setLayout(new BorderLayout());

        // Column names with ID added
        String[] columns = {"ID", "Username", "Password", "First Name", "Middle Name", "Last Name", "Role", "Share"};

        // Table model and table setup
        tableModel = new DefaultTableModel(columns, 0) {
            // Make ID and Share non-editable, optional
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make ID column read-only
            }
        };
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        // Buttons
        addButton = new JButton("Add Employee");
        deleteButton = new JButton("Delete Employee");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add employee button logic
        addButton.addActionListener(e -> showAddEmployeeDialog());

        // Delete employee button logic
        deleteButton.addActionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this employee?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void showAddEmployeeDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField middleNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField roleField = new JTextField();
        JTextField shareField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Username:"));     panel.add(usernameField);
        panel.add(new JLabel("Password:"));     panel.add(passwordField);
        panel.add(new JLabel("First Name:"));   panel.add(firstNameField);
        panel.add(new JLabel("Middle Name:"));  panel.add(middleNameField);
        panel.add(new JLabel("Last Name:"));    panel.add(lastNameField);
        panel.add(new JLabel("Role:"));         panel.add(roleField);
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
                String middleName = middleNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String role = roleField.getText().trim();
                int share = Integer.parseInt(shareField.getText().trim());

                if (share < 0 || share > 100) {
                    throw new NumberFormatException("Share must be between 0 and 100.");
                }

                int currentId = idCounter++;
                tableModel.addRow(new Object[]{currentId, username, password, firstName, middleName, lastName, role, share});
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for share. Please enter a number between 0 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

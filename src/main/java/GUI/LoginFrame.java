
package GUI;

import models.Employee;
import dao.EmployeeDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private EmployeeDAO employeeDAO;

    public LoginFrame() {
        // Initialize DAO
        employeeDAO = new EmployeeDAO();

        // Frame setup
        setTitle("SenanDB - Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

   
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

 
        JLabel titleLabel = new JLabel("SenanDB Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

  
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);

  
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

    
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.addActionListener(this::authenticateUser);
        mainPanel.add(loginButton, gbc);

     
        add(mainPanel);
        
        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    
    private void authenticateUser(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

       
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        Employee authenticatedEmployee = employeeDAO.authenticateEmployee(username, password);

        if (authenticatedEmployee != null) {
            
            dispose();
            
            
            openAppropriateWindow(authenticatedEmployee);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password", 
                "Authentication Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void openAppropriateWindow(Employee employee) {
        if (employee.isAdmin()) {
            
            SwingUtilities.invokeLater(() -> new base(employee));
        } else if (employee.isStaff()) {
            SwingUtilities.invokeLater(() -> new Staffbase(employee));
            };
        }
    
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}


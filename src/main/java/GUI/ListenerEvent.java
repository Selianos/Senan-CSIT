package GUI;

import javax.swing.*;
import java.awt.*;

public class ListenerEvent {
    private final JPanel eastPanel;
    private final JFrame frame;

    public ListenerEvent(JFrame frame, JPanel eastPanel) {
        this.frame = frame;
        this.eastPanel = eastPanel;
    }

    public void home() {
        frame.setTitle("Home");
        JLabel welcomeLabel = new JLabel("Welcome to SenanDB", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(Color.WHITE);
        homePanel.add(Box.createVerticalGlue());
        homePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        homePanel.add(welcomeLabel);
        homePanel.add(Box.createVerticalGlue());

        setContentPanel(homePanel);
    }

    public void items() {
        frame.setTitle("Items");
        setContentPanel(new ItemsPanel());
    }

    public void orders() {
        frame.setTitle("Orders");
        setContentPanel(new JPanel()); // Replace with actual OrdersPanel when implemented
    }

    public void employees() {
        frame.setTitle("Employees");
        setContentPanel(new EmployeesPanel());
    }

    public void suppliers() {
        frame.setTitle("Suppliers");
        setContentPanel(new SuppliersPanel());
    }

    private void setContentPanel(JPanel panel) {
        eastPanel.removeAll();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.add(panel, BorderLayout.CENTER);
        eastPanel.revalidate();
        eastPanel.repaint();
    }
}

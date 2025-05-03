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
        setContentPanel(new HomePanel());
    }

    public void items() {
        frame.setTitle("Items");
        setContentPanel(new ItemsPanel());
    }

    public void orders() {
        try {
            if (base.getSessionEmp().isAdmin()) {
                frame.setTitle("Orders");
                setContentPanel(new OrdersPanel());
            }
        } catch (NullPointerException e) {
        }

        try {
            if (Staffbase.getSessionEmp().isStaff()) {
                frame.setTitle("Orders");
                setContentPanel(new StaffOrdersPanel());
            }
        } catch (NullPointerException e) {
        }
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

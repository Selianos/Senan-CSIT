package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class base extends JFrame {
    private JPanel base;
    private JButton homeButton;
    private JButton itemsButton;
    private JButton employeesButton;
    private JButton suppliersButton;
    private JButton ordersButton;
    private JPanel westPanel;
    private JPanel EastPanel;

    public base() {
        setContentPane(base);
        setTitle("Home");
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle("Home");
            }
        });

        itemsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle("Items");
                ItemsPanel itemsPanel = new ItemsPanel();
                EastPanel.removeAll();
                EastPanel.setLayout(new BorderLayout());
                EastPanel.add(itemsPanel, BorderLayout.CENTER);
                EastPanel.revalidate();
                EastPanel.repaint();
            }
        });

        ordersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle("Orders");
                EastPanel.removeAll();
                EastPanel.setLayout(new BorderLayout());
                //EastPanel.add(orders, BorderLayout.CENTER);
                EastPanel.revalidate();
                EastPanel.repaint();
            }
        });

        employeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle("Employees");
                EmployeesPanel employeesPanel = new EmployeesPanel();
                EastPanel.removeAll();
                EastPanel.setLayout(new BorderLayout());
                EastPanel.add(employeesPanel, BorderLayout.CENTER);
                EastPanel.revalidate();
                EastPanel.repaint();
            }
        });

        suppliersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle("Suppliers");
                SuppliersPanel suppliersPanel = new SuppliersPanel();
                EastPanel.removeAll();
                EastPanel.setLayout(new BorderLayout());
                EastPanel.add(suppliersPanel, BorderLayout.CENTER);
                EastPanel.revalidate();
                EastPanel.repaint();
            }
        });
    }
}

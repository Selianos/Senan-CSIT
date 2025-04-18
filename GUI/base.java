package GUI;

import javax.swing.*;

public class base extends JFrame {
    private JPanel base;
    private JButton homeButton;
    private JButton itemsButton;
    private JButton employeesButton;
    private JButton suppliersButton;
    private JButton ordersButton;
    private JPanel westPanel;
    private JPanel EastPanel;


    public static void main(String[] args) {
        base frame = new base();
        frame.setContentPane(frame.base);
        frame.setTitle("Employee");
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

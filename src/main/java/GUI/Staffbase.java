
package GUI;



import javax.swing.*;
import java.awt.* ;
import models.Employee ;

public class Staffbase extends JFrame{
    

    // current session 
    
    static Employee  emp  ;

    //------------------Create Components------------------
    JPanel mainView = new JPanel(new BorderLayout());
    JPanel sidebar = new JPanel();
    JLabel titleLabel = new JLabel("SenanDB", SwingConstants.CENTER);
    JButton homeButton = new JButton("Home");
    JButton itemsButton = new JButton("Items");
    JButton ordersButton = new JButton("Orders");
    //------------------------------------------------------


    public Staffbase(Employee emp) {

        this.emp = emp ; 
        //------------------Components Configs------------------
        // Base config
        setTitle("Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        JPanel base = new JPanel(new BorderLayout());

        // Sidebar config
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBackground(new Color(20, 20, 40));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Title config
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.add(titleLabel);

        // Top glue
        sidebar.add(Box.createVerticalGlue());

        // Add buttons with spacing
        addSidebarButton(homeButton);
        sidebar.add(Box.createVerticalGlue());

        addSidebarButton(itemsButton);
        sidebar.add(Box.createVerticalGlue());

        addSidebarButton(ordersButton);
        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton = new JButton("Logout");
        addSidebarButton(logoutButton);


        // Bottom glue
        sidebar.add(Box.createVerticalGlue());

        // Footer
        JLabel footerLabel = new JLabel("Account", SwingConstants.CENTER);
        footerLabel.setForeground(Color.LIGHT_GRAY);
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(footerLabel);

        // Main content area
        mainView.setBackground(Color.WHITE);

        base.add(sidebar, BorderLayout.WEST);
        base.add(mainView, BorderLayout.CENTER);
        setContentPane(base);
        setVisible(true);

        //------------------------Listeners-------------------------
        ListenerEvent listener = new ListenerEvent(this, mainView);
        homeButton.addActionListener(e -> listener.home());
        itemsButton.addActionListener(e -> listener.items());
        ordersButton.addActionListener(e -> listener.orders());
        logoutButton.addActionListener(e -> {dispose(); new LoginFrame(); this.emp = null ;});}
    //------------------------------------------------------------------------
    private void addSidebarButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 15));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setUI(new RoundedButtonUI());
        sidebar.add(button);
    }

    // ------------------------------------Rounded UI design------------------------------------
    static class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setOpaque(false);
            c.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (b.getModel().isArmed()) {
                g2.setColor(new Color(90, 90, 90));
            } else {
                g2.setColor(b.getBackground());
            }

            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);

            FontMetrics fm = g.getFontMetrics();
            Rectangle r = new Rectangle(c.getSize());
            int textWidth = fm.stringWidth(b.getText());
            int textHeight = fm.getAscent();
            g2.setColor(b.getForeground());
            g2.drawString(b.getText(), (r.width - textWidth) / 2, (r.height + textHeight) / 2 - 3);

            g2.dispose();
        }
    }
    
    public static Employee getSessionEmp(){
        return emp ;
    }
}

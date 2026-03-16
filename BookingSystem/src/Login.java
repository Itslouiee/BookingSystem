import javax.swing.*;
import java.awt.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnForgot;
    private JPanel loginPanel;
    private int panelWidth = 400;
    private int panelHeight = 200;

    public Login() {
        setTitle("Login Form");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel na nagre-resize
        ResizableBackgroundPanel background = new ResizableBackgroundPanel("bg.jpg");
        background.setLayout(null);
        setContentPane(background);

        // ======= LOGO =======
        ImageIcon logoIcon = new ImageIcon("logo.png");
        Image img = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon scaledLogo = new ImageIcon(img);

        JLabel lblLogo = new JLabel(scaledLogo);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBounds((getWidth() - 80)/2, 20, 80, 80);
        background.add(lblLogo);

        // ======= TITLE =======
        JLabel lblTitle = new JLabel("Crimson Oak", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 48));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 90, getWidth(), 60);
        background.add(lblTitle);

        // ======= SUBTITLE =======
        JLabel lblSub = new JLabel("FINE DINING RESTAURANT", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSub.setForeground(Color.LIGHT_GRAY);
        lblSub.setBounds(0, 140, getWidth(), 30);
        background.add(lblSub);

        // Login panel
        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setOpaque(false);
        loginPanel.setBounds((getWidth() - panelWidth)/2, (getHeight() - panelHeight)/2 + 40, panelWidth, panelHeight);

        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(20, 20, 100, 25);
        lblUsername.setForeground(Color.WHITE);
        loginPanel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(130, 20, 200, 25);
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setBackground(new Color(0,0,0,100));
        loginPanel.add(txtUsername);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(20, 60, 100, 25);
        lblPassword.setForeground(Color.WHITE);
        loginPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 60, 200, 25);
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBackground(new Color(0,0,0,100));
        loginPanel.add(txtPassword);

        // Forgot button
        btnForgot = new JButton("Forgot Password?");
        btnForgot.setBounds(130, 100, 200, 25);
        btnForgot.setBorderPainted(false);
        btnForgot.setContentAreaFilled(false);
        btnForgot.setForeground(Color.BLUE);
        btnForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginPanel.add(btnForgot);

        // Login button
        btnLogin = new JButton("Login");
        btnLogin.setBounds(130, 130, 200, 30);
        loginPanel.add(btnLogin);

        background.add(loginPanel);

        // Action listeners
        btnLogin.addActionListener(e -> loginAction());
        btnForgot.addActionListener(e -> JOptionPane.showMessageDialog(this, "Forgot password clicked!"));

        // Re-center panel when window resizes
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {

                // center login panel
                loginPanel.setBounds((getWidth() - panelWidth)/2, (getHeight() - panelHeight)/2 + 40, panelWidth, panelHeight);

                // center logo
                lblLogo.setBounds((getWidth() - 80)/2, 20, 80, 80);

                // keep title centered
                lblTitle.setBounds(0, 90, getWidth(), 60);

                // keep subtitle centered
                lblSub.setBounds(0, 140, getWidth(), 30);
            }
        });

        setVisible(true);
    }

    private void loginAction() {

    String username = txtUsername.getText();
    String password = new String(txtPassword.getPassword());

    if(username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter username and password!");
        return;
    }

    try {

        Connection conn = Dbconnection.getConnection();

        String sql = "SELECT * FROM staff WHERE username=? AND password=?";
        PreparedStatement pst = conn.prepareStatement(sql);

        pst.setString(1, username);
        pst.setString(2, password);

        ResultSet rs = pst.executeQuery();

        if(rs.next()) {

            JOptionPane.showMessageDialog(this, "Welcome " + username + "!");

            new Dashboard();
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password");
        }

    } catch(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database Error!");
    }
}

    class ResizableBackgroundPanel extends JPanel {
        private Image bgImage;

        public ResizableBackgroundPanel(String imagePath) {
            bgImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
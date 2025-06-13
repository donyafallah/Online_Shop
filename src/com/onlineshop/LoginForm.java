package com.onlineshop;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {
    public static String loggedInUser = "";

    public LoginForm() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 244, 248));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        registerButton.setFont(font);
  
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
  
        JLabel titleLabel = new JLabel("Welcome to My Online Shop");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(loginButton, gbc);
        gbc.gridx = 1; panel.add(registerButton, gbc);

        add(panel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/online_shop", "root", "");
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    double userCredit = rs.getDouble("credit");
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    conn.close();
                    dispose();

                    LoginForm.loggedInUser = username;
                    User.clear();
                    User.init(username, userCredit);

                    if (role.equalsIgnoreCase("Admin")) {
                        new AdminAccount(username);
                    } else {
                        new UserAccount();
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect username or password.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database connection error.");
            }
        });

        registerButton.addActionListener(e -> {
            new SubmitForm();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}

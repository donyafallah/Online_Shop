package com.onlineshop;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class SubmitForm extends JFrame {

    private JTextField fnameField;
    private JTextField lnameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<String> roleBox;
    private String photoPath = "";

    public SubmitForm() {
        setTitle("Register Form");
        setSize(500, 500);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 244, 248));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel titleLabel = new JLabel("Create a New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        fnameField = new JTextField(); fnameField.setFont(font);
        lnameField = new JTextField(); lnameField.setFont(font);
        usernameField = new JTextField(); usernameField.setFont(font);
        passwordField = new JPasswordField(); passwordField.setFont(font);
        emailField = new JTextField(); emailField.setFont(font);
        String[] roles = {"User", "Admin"};
        roleBox = new JComboBox<>(roles); roleBox.setFont(font);

        JButton selectPhotoButton = new JButton("Choose Photo");
        selectPhotoButton.setFont(font);
        selectPhotoButton.setBackground(new Color(255, 193, 7));
        selectPhotoButton.setForeground(Color.BLACK);

        JButton submitButton = new JButton("Register");
        submitButton.setFont(font);
        submitButton.setBackground(new Color(40, 167, 69));
        submitButton.setForeground(Color.WHITE);

        int y = 1;
        addField(panel, gbc, "First Name:", fnameField, y++);
        addField(panel, gbc, "Last Name:", lnameField, y++);
        addField(panel, gbc, "Username:", usernameField, y++);
        addField(panel, gbc, "Password:", passwordField, y++);
        addField(panel, gbc, "Email:", emailField, y++);
        addField(panel, gbc, "Role:", roleBox, y++);

        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Profile Photo:"), gbc);
        gbc.gridx = 1;
        panel.add(selectPhotoButton, gbc);
        y++;

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);
  
        selectPhotoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                photoPath = selectedFile.getAbsolutePath();
            }
        });

        submitButton.addActionListener(e -> {
            String fname = fnameField.getText().trim();
            String lname = lnameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String email = emailField.getText().trim();
            String role = (String) roleBox.getSelectedItem();

            if (fname.isEmpty() || lname.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/online_shop", "root", "");
                String sql = "INSERT INTO users (fname, lname, username, password, email, role, photo_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, fname);
                stmt.setString(2, lname);
                stmt.setString(3, username);
                stmt.setString(4, password);
                stmt.setString(5, email);
                stmt.setString(6, role);
                stmt.setString(7, photoPath);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration successful!");
                conn.close();
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Registration failed!");
            }
        });

        add(panel);
        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}

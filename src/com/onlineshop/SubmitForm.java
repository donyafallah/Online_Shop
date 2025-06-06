package com.onlineshop;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class SubmitForm extends JFrame {

    private JTextField fnameField;
    private JTextField lnameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public SubmitForm() {
        setTitle("فرم ثبت‌نام");
        setSize(350, 300);
        setLayout(new GridLayout(6, 2));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

   
        fnameField = new JTextField();
        lnameField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        String[] roles = {"User", "Admin"};  
        roleBox = new JComboBox<>(roles);

        JButton submitButton = new JButton("ثبت‌نام");
        
        add(fnameField);
        add(new JLabel("نام:"));

        add(lnameField);
        add(new JLabel("نام خانوادگی:"));

        add(usernameField);
        add(new JLabel("نام کاربری:"));

        add(passwordField);
        add(new JLabel("رمز عبور:"));

        add(roleBox);
        add(new JLabel("نوع دسترسی:"));


        add(new JLabel(""));
        add(submitButton);

        submitButton.addActionListener(e -> {
            String fname = fnameField.getText().trim();
            String lname = lnameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleBox.getSelectedItem();

         
            if (fname.isEmpty() || lname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "همه فیلدها را به‌طور کامل پر کنید.");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/online_shop", "root", ""
                );
                String sql = "INSERT INTO users (fname , lname , username, password, role) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, fname);
                stmt.setString(2, lname);
                stmt.setString(3, username);
                stmt.setString(4, password);
                stmt.setString(5, role);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "ثبت‌نام با موفقیت انجام شد!");
                conn.close();
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "خطا در ثبت‌نام!");
            }
        });


        setVisible(true);
    }

    public static void main(String[] args) {
        new SubmitForm();
    }
}

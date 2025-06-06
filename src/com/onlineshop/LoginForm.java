package com.onlineshop;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {
	public static String loggedInUser = "";

    public LoginForm() {
    	
        setTitle("صفحه ورود");
        setSize(300, 200);
        setLayout(new GridLayout(4, 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("ورود");
        JButton registerButton = new JButton("ثبت نام");

        add(usernameField);
        add(new JLabel("نام کاربری:"));

        add(passwordField);
        add(new JLabel("رمز عبور:"));


        add(loginButton);
        add(registerButton);

     
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "لطفاً نام کاربری و رمز عبور را وارد کنید.");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/online_shop", "root", ""
                );
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role"); 

                    JOptionPane.showMessageDialog(this, "ورود موفقیت‌آمیز بود!");
                    conn.close();
                    dispose();

                    if (role.equalsIgnoreCase("Admin")) {
                      
                        new AdminUI(ProductDatabase.getProducts());
                    } else {
                        
                        new ShopUIForUsers();
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "نام کاربری یا رمز عبور اشتباه است.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "خطا در اتصال به دیتابیس");
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

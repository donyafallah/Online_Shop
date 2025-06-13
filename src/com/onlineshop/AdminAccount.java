package com.onlineshop;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.function.Consumer;

public class AdminAccount extends JFrame {

       private String username; 
    public AdminAccount(String username) {
        this.username = username;

        setTitle("Admin Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
  
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(35, 10, 10, 10));

        Dimension btnSize = new Dimension(180, 35);

        Color buttonBgColor = new Color(70, 130, 180); // رنگ آبی تیره
        Color buttonHoverColor = new Color(100, 149, 237); // رنگ هاور آبی روشن
        Color buttonTextColor = Color.WHITE;

        JButton shopButton = new JButton("Manage");
        shopButton.setMaximumSize(btnSize);
        shopButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setMaximumSize(btnSize);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton[] buttons = {shopButton, logoutButton};

        for (JButton btn : buttons) {
            btn.setBackground(buttonBgColor);
            btn.setForeground(buttonTextColor);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(buttonHoverColor);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(buttonBgColor);
                }
            });
        }


        buttonPanel.add(shopButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(logoutButton);

        buttonPanel.setPreferredSize(new Dimension(120, 400));
        add(buttonPanel, BorderLayout.WEST);

        JPanel yellowPanel = new JPanel(new BorderLayout());
        yellowPanel.setBackground(new Color(30, 41, 59));

        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 60));
        paddingPanel.add(yellowPanel, BorderLayout.CENTER);
        add(paddingPanel, BorderLayout.CENTER);

        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));

        JLabel photoLabel = new JLabel();
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoLabel.setPreferredSize(new Dimension(80, 80));
        photoLabel.setMinimumSize(new Dimension(80, 80));
        photoLabel.setMaximumSize(new Dimension(80, 80));

        JLabel nameLabel = new JLabel();
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel();
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setForeground(Color.LIGHT_GRAY);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel();
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(Color.LIGHT_GRAY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        int userId = -1;
   
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/online_shop", "root", ""
        )) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("fname") + " " + rs.getString("lname");
                String email = rs.getString("email");
                String photoPath = rs.getString("photo_path");
                userId = rs.getInt("id");
                nameLabel.setText(fullName);
                idLabel.setText("Useradmin : " + username);
                emailLabel.setText("Email : " + email);
 
                if (photoPath != null && !photoPath.isEmpty()) {
                    ImageIcon roundedIcon = getRoundedImage(photoPath, 80);
                    if (roundedIcon != null) {
                        photoLabel.setIcon(roundedIcon);
                        photoLabel.setText("");
                    } else {
                        photoLabel.setText("No Photo");
                    }
                } else {
                    photoLabel.setText("No Photo");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            nameLabel.setText("User");
            idLabel.setText("ID: unknown");
            emailLabel.setText("Email: Unknown");
            photoLabel.setText("No Photo");
        }

        userInfoPanel.add(Box.createVerticalStrut(40));
        userInfoPanel.add(photoLabel);
        userInfoPanel.add(Box.createVerticalStrut(10));
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(idLabel);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(emailLabel);
        yellowPanel.add(userInfoPanel, BorderLayout.CENTER);

        shopButton.addActionListener(e -> {
            ArrayList<Product> products = new ArrayList<>();
            products.add(new MobileProduct("Apple Mobile", 3, 400));
            products.add(new MobileProduct("Samsung Mobile", 5, 350));
            products.add(new MobileProduct("Sony Mobile", 2, 300));
            products.add(new MobileProduct("Xiaomi Mobile", 4, 200));

            new ManageShop(products);
        });

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
 
    public static double fetchCreditFromDatabase(String username) {
        double creditFromDb = 0;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/online_shop", "root", "")) {
            String sql = "SELECT credit FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                creditFromDb = rs.getDouble("credit");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return creditFromDb;
    }

 
    private ImageIcon getRoundedImage(String imagePath, int diameter) {
        try {
            BufferedImage original = ImageIO.read(new File(imagePath));
            Image scaled = original.getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH);

            BufferedImage rounded = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = rounded.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();

            return new ImageIcon(rounded);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

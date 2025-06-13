 package com.onlineshop;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ShopUIForUsers extends JFrame {
	
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Product> cart = new ArrayList<>();
    private ArrayList<Integer> cartQuantities = new ArrayList<>();
    private String fullName = "";
    private JPanel productsPanel;
    private int userBalance = (int) User.getInstance().getCredit();
    private JLabel balanceLabel;
    private JLabel userLabel;
    public double updatedCredit;

    public ShopUIForUsers() {
        setTitle("Online Shop");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        this.products = new ArrayList<>(ProductDataBase.getProducts());
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        updatedCredit = User.getInstance().getCredit();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/online_shop", "root", "");
            String sql = "SELECT fname, lname FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, LoginForm.loggedInUser);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                fullName = rs.getString("fname") + " " + rs.getString("lname");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            fullName = LoginForm.loggedInUser; 
        }
   
        userLabel = new JLabel("User: " + LoginForm.loggedInUser );
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerPanel.add(userLabel, BorderLayout.WEST);
        
        balanceLabel = new JLabel("Balance: " + userBalance + " Toman");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        productsPanel = new JPanel(new GridLayout(0, 1, 10, 10));  
        productsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        for (Product product : products) {
            JPanel productPanel = new JPanel(new GridLayout(1, 4, 10, 0));
            productPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            JLabel nameLabel = new JLabel(product.getName() + " - " + product.getPrice() + " Toman");
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

            int[] currentStock = { product.getStock() }; 

            JLabel stockLabel = new JLabel("Stock: " + currentStock[0]);
            stockLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            buttonsPanel.setOpaque(false);

            JButton minusButton = new JButton("-");
            minusButton.setPreferredSize(new Dimension(25, 25));
            minusButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            minusButton.setToolTipText("Remove one item");
            minusButton.setMargin(new Insets(0, 0, 0, 0));

            JLabel countLabel = new JLabel("0", SwingConstants.CENTER);
            countLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
            countLabel.setPreferredSize(new Dimension(30, 25));
            countLabel.setForeground(Color.BLUE);
            countLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countLabel.setVerticalAlignment(SwingConstants.CENTER);

            JButton plusButton = new JButton("+");
            plusButton.setPreferredSize(new Dimension(25, 25));
            plusButton.setToolTipText("Add one item");
            plusButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            plusButton.setMargin(new Insets(0, 0, 0, 0));
            plusButton.setBackground(new Color(76, 175, 80)); 
            plusButton.setForeground(Color.WHITE);
            plusButton.setOpaque(true);
            plusButton.setBorderPainted(false);

            minusButton.setBackground(new Color(244, 67, 54)); 
            minusButton.setForeground(Color.WHITE);


            buttonsPanel.add(minusButton);
            buttonsPanel.add(countLabel);
            buttonsPanel.add(plusButton);

            JButton addToCartButton = new JButton("Add to Cart");

            plusButton.addActionListener(e -> {
                int count = Integer.parseInt(countLabel.getText());
                if (currentStock[0] > 0) {
                    countLabel.setText(String.valueOf(count + 1));
                    currentStock[0]--;
                    stockLabel.setText("Stock: " + currentStock[0]);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Currently, we don't have enough balance.");
                    }
            });

            minusButton.addActionListener(e -> {
                int count = Integer.parseInt(countLabel.getText());
                if (count > 0) {
                    countLabel.setText(String.valueOf(count - 1));
                    currentStock[0]++;
                    stockLabel.setText("Stock: " + currentStock[0]);
                }
            });

            addToCartButton.addActionListener(e -> {
                int count = Integer.parseInt(countLabel.getText());
                if (count > 0) {
                    int index = cart.indexOf(product);
                    if (index >= 0) {
                        int currentQuantity = cartQuantities.get(index);
                        int newQuantity = currentQuantity + count;
                        if (newQuantity <= product.getStock()) {
                            cartQuantities.set(index, newQuantity);
                            JOptionPane.showMessageDialog(this,
                                "\"" + product.getName() + "\" quantity updated to " + newQuantity + ".");
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "Total selected quantity exceeds original stock.");
                        }
                    } else {
                        cart.add(product);
                        cartQuantities.add(count);
                        JOptionPane.showMessageDialog(this,
                            count + " of \"" + product.getName() + "\" added to cart.");
                    }

                    countLabel.setText("0");
                } else {
                    JOptionPane.showMessageDialog(this, "Please select at least one item.");
                }
            });

            addToCartButton.setBackground(new Color(33, 150, 243)); 
            addToCartButton.setForeground(Color.WHITE);
            
            productPanel.add(nameLabel);
            productPanel.add(stockLabel);
            productPanel.add(buttonsPanel);
            productPanel.add(addToCartButton);
            productsPanel.add(productPanel);
        }

        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(e -> showCart());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        viewCartButton.setBackground(new Color(33, 150, 243));  
        viewCartButton.setForeground(Color.WHITE);
        viewCartButton.setOpaque(true);
        viewCartButton.setBorderPainted(false);

        bottomPanel.add(viewCartButton);

        add(new JScrollPane(productsPanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        getContentPane().setBackground(new Color(245, 245, 245)); 
    }
    
    private void showCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }

        final Cart[] cartWindowHolder = new Cart[1];

        cartWindowHolder[0] = new Cart(
            LoginForm.loggedInUser,
            (int) User.getInstance().getCredit(),  
            new ArrayList<>(cart),
            new ArrayList<>(cartQuantities),
            totalCost -> {
                System.out.println("Before purchase balance: " + (int) User.getInstance().getCredit());

                updatedCredit = User.getInstance().getCredit() - totalCost;
                User.getInstance().setCredit(updatedCredit);
                balanceLabel.setText("Balance: " + (int) updatedCredit + " Toman");

                for (int i = 0; i < cart.size(); i++) {
                    Product p = cart.get(i);
                    int qty = cartQuantities.get(i);
                    p.decreaseStock(qty);
                }

                cart.clear();
                cartQuantities.clear();

                refreshProductsPanel();

                cartWindowHolder[0].updateBalance();  
            }
        );

        cartWindowHolder[0].setVisible(true);
        this.dispose();
    }
  
    private void refreshProductsPanel() {
        productsPanel.removeAll();

        for (Product product : products) {
            JPanel productPanel = new JPanel(new GridLayout(1, 4, 10, 0));
            productPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            JLabel nameLabel = new JLabel(product.getName() + " - " + product.getPrice() + " Toman");
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

            JLabel stockLabel = new JLabel("Stock: " + product.getStock());
            stockLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            buttonsPanel.setOpaque(false);

            JButton minusButton = new JButton("-");
            minusButton.setPreferredSize(new Dimension(25, 25));
            minusButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            minusButton.setMargin(new Insets(0, 0, 0, 0));

            JLabel countLabel = new JLabel("0", SwingConstants.CENTER);
            countLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
            countLabel.setPreferredSize(new Dimension(30, 25));
            countLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countLabel.setVerticalAlignment(SwingConstants.CENTER);

            JButton plusButton = new JButton("+");
            plusButton.setPreferredSize(new Dimension(25, 25));
            plusButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            plusButton.setMargin(new Insets(0, 0, 0, 0));

            buttonsPanel.add(minusButton);
            buttonsPanel.add(countLabel);
            buttonsPanel.add(plusButton);

            JButton addToCartButton = new JButton("Add to Cart");

            plusButton.addActionListener(e -> {
                int count = Integer.parseInt(countLabel.getText());
                if (count < product.getStock()) {
                    countLabel.setText(String.valueOf(count + 1));
                } else {
                    JOptionPane.showMessageDialog(this, 
                    		"No more than " + product.getStock() + " items are currently in stock.");
                }
            });

            minusButton.addActionListener(e -> {
                int count = Integer.parseInt(countLabel.getText());
                if (count > 0) {
                    countLabel.setText(String.valueOf(count - 1));
                }
            });

            addToCartButton.addActionListener(e -> {
                int count = Integer.parseInt(countLabel.getText());
                if (count > 0) {
                    int index = cart.indexOf(product);
                    if (index >= 0) {
                        int currentQuantity = cartQuantities.get(index);
                        int newQuantity = currentQuantity + count;
                        if (newQuantity <= product.getStock()) {
                            cartQuantities.set(index, newQuantity);
                            JOptionPane.showMessageDialog(this, "\"" + product.getName() + "\" quantity updated to " + newQuantity + ".");
                        } else {
                            JOptionPane.showMessageDialog(this, "Total selected quantity exceeds available stock.");
                        }
                    } else {
                        cart.add(product);
                        cartQuantities.add(count);
                        JOptionPane.showMessageDialog(this, count + " of \"" + product.getName() + "\" added to cart.");
                    }
                    countLabel.setText("0");
                } else {
                    JOptionPane.showMessageDialog(this, "Please select at least one item.");
                }
            });

            productPanel.add(nameLabel);
            productPanel.add(stockLabel);
            productPanel.add(buttonsPanel);
            productPanel.add(addToCartButton);

            productsPanel.add(productPanel);
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

}

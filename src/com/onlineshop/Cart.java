package com.onlineshop;

import javax.swing.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.border.LineBorder;

public class Cart extends JFrame {

    private JLabel balanceLabel;
    private JLabel totalCostLabel;
    private int totalCost;
    private Consumer<Integer> onPurchase;
    private DefaultTableModel tableModel;
    private JTable cartTable;
    public static Cart openCartInstance = null;
    private ArrayList<Product> cart = new ArrayList<>();
    private ArrayList<Integer> cartQuantities = new ArrayList<>();
    private ArrayList<Product> products;
    private ArrayList<Integer> quantities;

    public Cart(String username, int balance, ArrayList<Product> products, ArrayList<Integer> quantities, Consumer<Integer> onPurchase) {
        this.onPurchase = onPurchase;
        this.products = products;
        this.quantities = quantities;

        setTitle("🛒 Your Shopping Cart");
        setSize(750, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        Font headerFont = new Font("SansSerif", Font.BOLD, 16);
        Font bodyFont = new Font("SansSerif", Font.PLAIN, 14);

        JPanel userInfoPanel = new JPanel(new GridLayout(1, 2));
        userInfoPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel userLabel = new JLabel("👤 User: " + username);
        userLabel.setFont(headerFont);
        userInfoPanel.add(userLabel);

        balanceLabel = new JLabel("💰 Balance: " + (int) User.getInstance().getCredit() + " Toman");
        balanceLabel.setFont(headerFont);
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userInfoPanel.add(balanceLabel);

        add(userInfoPanel, BorderLayout.NORTH);

        String[] columns = {"✓", "Product Name", "Quantity", "Unit Price", "Total Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0;
            }
        };

        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(26);
        cartTable.setFont(bodyFont);
        cartTable.getTableHeader().setFont(headerFont);
        cartTable.setShowGrid(true);
        cartTable.setGridColor(new Color(220, 220, 220));
        cartTable.setSelectionBackground(new Color(232, 242, 254));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        totalCostLabel = new JLabel();
        totalCostLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottomPanel.add(totalCostLabel, BorderLayout.WEST);

        JButton removeButton = new JButton("🗑 Remove Selected");
        JButton useBalanceButton = new JButton("💳 Use Balance");

        removeButton.setFocusPainted(false);
        useBalanceButton.setFocusPainted(false);

        removeButton.setFont(bodyFont);
        useBalanceButton.setFont(bodyFont);

        removeButton.setBackground(new Color(255, 100, 100));
        removeButton.setForeground(Color.WHITE);

        useBalanceButton.setBackground(new Color(60, 179, 113));
        useBalanceButton.setForeground(Color.WHITE);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.add(removeButton);
        buttonsPanel.add(useBalanceButton);

        bottomPanel.add(buttonsPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();

        useBalanceButton.addActionListener(e -> {
            if (totalCost > User.getInstance().getCredit()) {
                JOptionPane.showMessageDialog(this, "❌ Your balance is insufficient.");
            } else {
                onPurchase.accept(totalCost);
                saveOrderToDatabase();
                updateBalance();
                JOptionPane.showMessageDialog(this, "✅ Payment successful!");
                dispose();
            }
        });

        removeButton.addActionListener(e -> {
            ArrayList<Integer> toRemove = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    toRemove.add(i);
                }
            }

            if (toRemove.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please tick at least one item to remove.");
                return;
            }

            for (int i = toRemove.size() - 1; i >= 0; i--) {
                int row = toRemove.get(i);
                products.get(row).increaseStock(quantities.get(row));
                products.remove(row);
                quantities.remove(row);
            }

            refreshTable();
        });

        setVisible(true);
        openCartInstance = this;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        totalCost = 0;

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            int qty = quantities.get(i);
            int unitPrice = p.getPrice();
            int totalPrice = qty * unitPrice;
            totalCost += totalPrice;

            Object[] row = {
                false,
                p.getName(),
                qty,
                String.format("%,d Toman", unitPrice),
                String.format("%,d Toman", totalPrice)
            };
            tableModel.addRow(row);
        }

        totalCostLabel.setText("🧾 Total Cost: " + String.format("%,d Toman", totalCost));

    }

    public void updateBalance() {
        balanceLabel.setText("💰 Balance: " + (int) User.getInstance().getCredit() + " Toman");
    }
    private void saveOrderToDatabase() {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/online_shop", "root", "");  
            String sql = "INSERT INTO orders (username, product_name, quantity, total_price) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                int quantity = quantities.get(i);
                int totalPrice = quantity * product.getPrice();

                stmt.setString(1, LoginForm.loggedInUser);  
                stmt.setString(2, product.getName());
                stmt.setInt(3, quantity);
                stmt.setInt(4, totalPrice);

                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.close();
            System.out.println("Orders saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving orders to the database.");
        }
    }


}

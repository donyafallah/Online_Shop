package com.onlineshop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageShop extends JFrame {
    private List<Product> products;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField increaseField;

    public ManageShop(List<Product> products) {
        this.products = ProductDataBase.getProducts();

        setTitle("Manage Shop Inventory");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
  
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 249, 252));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Product Name", "Stock", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.setRowHeight(28);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(new Color(220, 230, 240));
        productTable.setSelectionBackground(new Color(204, 229, 255));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < productTable.getColumnCount(); i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(245, 249, 252));

        JLabel increaseLabel = new JLabel("Increase Stock by:");
        increaseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottomPanel.add(increaseLabel);

        increaseField = new JTextField(5);
        increaseField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottomPanel.add(increaseField);

        JButton increaseButton = new JButton("Increase Stock");
        increaseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        increaseButton.setBackground(new Color(0, 123, 255));
        increaseButton.setForeground(Color.WHITE);
        increaseButton.setFocusPainted(false);
        bottomPanel.add(increaseButton);

        increaseButton.addActionListener(e -> increaseStock());

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        refreshTable();
        setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Product p : products) {
        	Object[] row = {
        		    p.getName(),
        		    p.getStock(),
        		    String.format("%.2f Toman", (double) p.getPrice())  
        		};

            tableModel.addRow(row);
        }
    }

    private void increaseStock() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first!");
            return;
        }

        String input = increaseField.getText().trim();
        int increaseAmount;

        try {
            increaseAmount = Integer.parseInt(input);
            if (increaseAmount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a positive integer for increase amount.");
            return;
        }
        Product selectedProduct = products.get(selectedRow);
        selectedProduct.increaseStock(increaseAmount);
        refreshTable();
        increaseField.setText("");
        JOptionPane.showMessageDialog(this, "Stock increased successfully!");
    }
}

package com.onlineshop;

public class Product {
    private String name;
    private int stock;

    public Product(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public void increaseStock(int count) {
        stock += count;
    }

    public void decreaseStock(int count) {
        stock -= count;
    }
}

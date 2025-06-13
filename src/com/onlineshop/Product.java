package com.onlineshop;

public abstract class Product {
    protected String name;
    protected int stock;
    protected int price;

    public Product(String name, int stock, int price) {
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public int getPrice() {
        return price;
    }

    public void increaseStock(int amount) {
        this.stock += amount;
    }

    public void decreaseStock(int qty) {
        if (qty <= stock) {
            stock -= qty;
        }
    }

    public abstract String getCategory();  
}

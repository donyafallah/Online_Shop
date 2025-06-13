package com.onlineshop;

public class MobileProduct extends Product {

    public MobileProduct(String name, int stock, int price) {
        super(name, stock, price);
    }

    @Override
    public String getCategory() {
        return "Mobile";
    }
}


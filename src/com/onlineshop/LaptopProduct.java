package com.onlineshop;

public class LaptopProduct extends Product {

    public LaptopProduct(String name, int stock, int price) {
        super(name, stock, price);
    }

    @Override
    public String getCategory() {
        return "Laptop";
    }
	
}

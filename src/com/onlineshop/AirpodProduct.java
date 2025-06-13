package com.onlineshop;

public class AirpodProduct extends Product {

    public AirpodProduct(String name, int stock, int price) {
        super(name, stock, price);
    }

    @Override
    public String getCategory() {
        return "Airpod";
    }
}

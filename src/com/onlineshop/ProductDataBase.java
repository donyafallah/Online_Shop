package com.onlineshop;

import java.util.ArrayList;
import java.util.List;

public class ProductDataBase {
    private static final List<Product> products = new ArrayList<>();

    static {
        products.add(new MobileProduct("Apple Mobile", 3, 550));
        products.add(new MobileProduct("Samsung Mobile", 5, 450));
        products.add(new MobileProduct("Xiaomi Mobile", 4, 300));
        products.add(new MobileProduct("MacBook Laptop", 5, 2000));
        products.add(new MobileProduct("hp Laptop", 2, 1000));
        products.add(new MobileProduct("Asus Laptop", 3, 950));
        products.add(new MobileProduct("Apple Airpod", 2, 200));
        products.add(new MobileProduct("Samsung Airpod", 3, 120));

    }

    public static List<Product> getProducts() {
        return products;
    }
}
    
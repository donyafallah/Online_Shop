package com.onlineshop;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.function.Consumer;
public class User {
    private static User instance;
    private String username;
    private double credit;
    private ArrayList<Consumer<Double>> creditListeners = new ArrayList<>();

    private User(String username, double credit) {
        this.username = username;
        this.credit = credit;
    }
    public static void init(String username, double credit) {
        if (instance == null) {
            instance = new User(username, credit);
        }
    }
    public static User getInstance() {
        if (instance == null) {
            throw new IllegalStateException("User instance is not initialized. Call User.init() first.");
        }
        return instance;
    }

    public void addCreditListener(Consumer<Double> listener) {
        creditListeners.add(listener);
    }

    private void notifyCreditListeners() {
        for (Consumer<Double> listener : creditListeners) {
            listener.accept(this.credit);
            
        }
       
    }

    public String getUsername() {
        return username;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
        notifyCreditListeners();
    }

    public void addCredit(double amount) {
        this.credit += amount;
        notifyCreditListeners();
    }

    public void subtractCredit(double amount) {
        this.credit -= amount;
        notifyCreditListeners();
    }

    public static void clear() {
        instance = null;
    }
}

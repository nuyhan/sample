package com.example.sample;

import java.util.Objects;

public class ProductItem {
    public String name;
    public String expiryDate;
    public int quantity;

    public ProductItem() {
    }

    public ProductItem(String name, String expiryDate, int quantity) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    public String getId() {
        return name;
    }

    public int getNotificationId() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }
}

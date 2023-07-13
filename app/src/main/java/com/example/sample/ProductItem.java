package com.example.sample;

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

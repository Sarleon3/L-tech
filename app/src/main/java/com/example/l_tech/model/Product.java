package com.example.l_tech.model;

public class Product {
    private int id;
    private String name;
    private String price;
    private int imageResId;
    private boolean isFavorite;
    private int quantity;
    private boolean isChecked;

    // Constructor, getters, and setters

    public Product(int id, String name, String price, int imageResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.isFavorite = false;
        this.quantity = 0;
        this.isChecked = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        quantity++;
    }

    public void decrementQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
package com.example.l_tech.Model;


public class Product {
    private String name;
    private String code;
    private double rating;
    private int imageResId;
    private String category;
    private boolean isFavorite;
    private boolean isSelected; // Новое поле для состояния выбора
    private double price;
    private int quantityInStock;  // Количество на складе
    private int quantityInCart;   // Количество в корзине

    // Конструктор
    public Product(String name, String code, double rating, int imageResId, String category, boolean isFavorite, double price, int quantityInStock) {
        this.name = name;
        this.code = code;
        this.rating = rating;
        this.imageResId = imageResId;
        this.category = category;
        this.isFavorite = isFavorite;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.quantityInCart = 0; // По умолчанию количество в корзине 0
        this.isSelected = true; // По умолчанию товар не выбран
    }

    // Геттеры и сеттеры
    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getQuantityInCart() {
        return quantityInCart;
    }

    public void setQuantityInCart(int quantityInCart) {
        this.quantityInCart = quantityInCart;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

package com.example.l_tech.Model;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("productId")
    private int productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("price")
    private double price;

    @SerializedName("rating")
    private int rating;


    @SerializedName("image")
    private String image;


    // **Пустой конструктор нужен для Retrofit**
    public Product() {}

    // Конструктор с параметрами
    public Product(String productName, String image, double price) {
        this.productName = productName;
        this.image = image;
        this.price = price;

    }

    // Геттеры
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getRating() { return rating; }
    public String getImage() { return image; }
}

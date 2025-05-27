package com.example.l_tech.Model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {
    @SerializedName("productId")
    private int productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("price")
    private double price;

    @SerializedName("rating")
    private int rating;

    @SerializedName("description")
    private String description;

    @SerializedName("images")
    private List<String> images;

    @SerializedName("image")
    private String image;

    // **Пустой конструктор нужен для Retrofit**
    public Product() {}

    // Constructor for Parcelable
    protected Product(Parcel in) {
        productId = in.readInt();
        productName = in.readString();
        price = in.readDouble();
        rating = in.readInt();
        description = in.readString();
        images = in.createStringArrayList();
        image = in.readString();
    }

    // Constructor with parameters
    public Product(String productName, String image, double price) {
        this.productName = productName;
        this.image = image;
        this.price = price;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getRating() { return rating; }
    public String getImage() { return image; }
    public List<String> getImages() { return images; }
    public String getDescription() { return description; }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(productName);
        dest.writeDouble(price);
        dest.writeInt(rating);
        dest.writeString(description);
        dest.writeStringList(images);
        dest.writeString(image);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}

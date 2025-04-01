package com.example.l_tech.Model;

public class CartItem {
    private String productId;
    private int quantity;
    private double price;
    private boolean isSelected;  // Поле для чекбокса

    public CartItem(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.isSelected = true;  // По умолчанию, товар выбран
    }


    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

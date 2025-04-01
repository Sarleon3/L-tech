package com.example.l_tech.Model;

import java.util.List;

public class ProductBlock {
    private final ProductBlockType blockType;
    private final String categoryName;
    private final List<Product> products;

    public ProductBlock(ProductBlockType blockType, String categoryName, List<Product> products) {
        this.blockType = blockType;
        this.categoryName = categoryName;
        this.products = products;
    }

    public ProductBlockType getBlockType() {
        return blockType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Product> getProducts() {
        return products;
    }
}

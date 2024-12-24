package com.example.l_tech.Model;
public class ProductType {
    private Long productTypeId;
    private String typeName;

    // Геттеры и сеттеры
    public Long getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

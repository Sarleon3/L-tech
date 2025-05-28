package com.example.l_tech.Model;

import com.google.gson.annotations.SerializedName;

public class AttributeValue {
    @SerializedName("attributeValueId")
    private Long attributeValueId;

    @SerializedName("product")
    private Product product;

    @SerializedName("attributeType")
    private AttributeType attributeType;

    @SerializedName("attributeValues") // Note: This field name seems inconsistent with singular value
    private String attributeValues; // Assuming the value is a String

    // Getters
    public Long getAttributeValueId() {
        return attributeValueId;
    }

    public Product getProduct() {
        return product;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public String getAttributeValues() { // Keeping the name as per JSON, but be aware
        return attributeValues;
    }
} 
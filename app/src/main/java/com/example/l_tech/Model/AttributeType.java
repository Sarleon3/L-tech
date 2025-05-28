package com.example.l_tech.Model;

import com.google.gson.annotations.SerializedName;

public class AttributeType {
    @SerializedName("attributeTypeId")
    private Long attributeTypeId;

    @SerializedName("attributeName")
    private String attributeName;

    // Getters
    public Long getAttributeTypeId() {
        return attributeTypeId;
    }

    public String getAttributeName() {
        return attributeName;
    }
} 
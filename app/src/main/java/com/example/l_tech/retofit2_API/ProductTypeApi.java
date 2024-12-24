package com.example.l_tech.retofit2_API;
import com.example.l_tech.Model.ProductType;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface ProductTypeApi {
    @GET("api/productTypes/allParent")
    Call<List<ProductType>> getParentProductTypes();

    @GET("api/productTypes/children/{parentName}")
    Call<List<ProductType>> getChildProductTypes(@Path("parentName") String parentName);
}
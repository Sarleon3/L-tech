package com.example.l_tech.retofit2_API;

import android.util.Log;

import com.example.l_tech.Model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductApi {

    // API method for fetching products by category
    @GET("api/products/category/{typeName}")
    Call<List<Product>> getProductsByCategory(@Path("typeName") String typeName);


}
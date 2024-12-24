package com.example.l_tech.retofit2_API;

import com.example.l_tech.Model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductApi {
    @GET("api/products/category/{category}")
    Call<List<Product>> getProductsByCategory(@Path("category") String category);
    Call<List<Product>> getProducts();
}

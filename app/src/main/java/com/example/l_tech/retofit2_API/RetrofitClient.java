package com.example.l_tech.retofit2_API;
import java.net.URI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance;
    private Retrofit retrofit;
    private static final String BASE_URL = "http://l-tech-logic-production.up.railway.app/";
    private ProductApi productApi;

    // Private constructor to prevent instantiation
    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        productApi = retrofit.create(ProductApi.class);
    }

    // Public method to get the instance
    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    // Method to get the API interface
    public ProductApi getApi() {
        return productApi;
    }
    // Новый метод для получения Retrofit
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
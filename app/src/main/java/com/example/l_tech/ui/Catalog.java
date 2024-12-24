package com.example.l_tech.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.l_tech.R;
import com.example.l_tech.Thanks;
import com.example.l_tech.retofit2_API.ProductTypeApi;
import com.example.l_tech.retofit2_API.RetrofitClient;
import com.example.l_tech.Model.ProductType;
import com.example.l_tech.Adapter.ProductTypeAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Catalog extends Fragment {

    private RecyclerView recyclerView;
    private ProductTypeAdapter adapter;

    public Catalog() {
        // Required empty public constructor
    }

    public static Catalog newInstance(String param1, String param2) {
        Catalog fragment = new Catalog();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadParentProductTypes();
    }

    private void loadParentProductTypes() {
        ProductTypeApi api = RetrofitClient.getRetrofitInstance().create(ProductTypeApi.class);
        Call<List<ProductType>> call = api.getParentProductTypes();

        call.enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductType>> call, @NonNull Response<List<ProductType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductType> productTypes = response.body();
                    adapter = new ProductTypeAdapter(productTypes, Catalog.this::fetchChildCategories);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("Catalog", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductType>> call, @NonNull Throwable t) {
                Log.e("Catalog", "Failed to fetch data: " + t.getMessage());
            }
        });
    }

    private void fetchChildCategories(String parentName) {
        ProductTypeApi api = RetrofitClient.getRetrofitInstance().create(ProductTypeApi.class);
        Call<List<ProductType>> call = api.getChildProductTypes(parentName);

        call.enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductType>> call, @NonNull Response<List<ProductType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductType> childCategories = response.body();
                    if (childCategories.isEmpty()) {
                        navigateToProducts(parentName);
                    } else {
                        adapter.updateCategories(childCategories);
                    }
                } else {
                    Log.e("Catalog", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductType>> call, @NonNull Throwable t) {
                Log.e("Catalog", "Failed to fetch child categories: " + t.getMessage());
            }
        });
    }

    private void navigateToProducts(String categoryName) {
        Intent intent = new Intent(requireContext(), Thanks.class);
        intent.putExtra("categoryName", categoryName);
        startActivity(intent);
    }

}

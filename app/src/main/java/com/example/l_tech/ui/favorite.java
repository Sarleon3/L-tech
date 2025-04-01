package com.example.l_tech.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.Adapter.ProductWithCartAdapter;
import com.example.l_tech.Model.Product;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.UserDataListener;
import com.example.l_tech.retofit2_API.ProductApi;
import com.example.l_tech.retofit2_API.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class favorite extends Fragment {

    private RecyclerView recyclerViewFavorites;
    private ProductWithCartAdapter adapter;
    private FirebaseAuth auth;
    private List<Product> favoriteProducts = new ArrayList<>();
    public favorite() {
        // Required empty public constructor
    }
    String userId ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2-column grid

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();  // Получаем уникальный ID пользователя
        } else {
            userId = "guest";
        }
        // Get the list of favorite products from Firebase
        loadFavoriteProducts();

        return view;
    }

    private void loadFavoriteProducts() {
        Log.d("Favorites", "Loading favorite products for user: " + userId);
        FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("Favorites", "onDataChange triggered, snapshot exists: " + snapshot.exists());
                        if (snapshot.exists()) {
                            favoriteProducts.clear(); // Clear old data
                            for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                                String productId = favoriteSnapshot.getKey();
                                Log.d("Favorites", "Fetching product details for: " + productId);
                                // Fetch product details from the "products" node
                                fetchProductDetails(productId);
                            }
                        } else {
                            Log.d("Favorites", "No favorite products found for this user");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Favorites", "Error loading favorites: " + error.getMessage());
                    }
                });
    }

    private void fetchProductDetails(String productIds) {
        Log.d("Favorites", "Fetching product details for product IDs: " + productIds);
        ProductApi apiService = RetrofitClient.getInstance().getApi();
        Call<List<Product>> call = apiService.getProductsByIds(productIds);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    for (Product product : products) {
                        favoriteProducts.add(product); // Add product to the list
                        Log.d("Favorites", "Product added: " + product.getProductName());
                    }
                    updateAdapter(); // Notify adapter to update the RecyclerView
                } else {
                    Log.d("Favorites", "Failed to fetch product details");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("Favorites", "Error fetching product details: " + t.getMessage());
            }
        });
    }

    private void updateAdapter() {
        if (adapter == null) {
            adapter = new ProductWithCartAdapter(favoriteProducts, userId, new UserDataListener(userId, new UserDataListener.DataChangeCallback() {
                @Override
                public void onCartUpdated(String cartData) {
                    // Handle cart update
                    Log.d("Favorites", "Cart updated, refreshing RecyclerView.");
                    updateRecyclerView();
                }

                @Override
                public void onFavoritesUpdated(String favoritesData) {
                    // Handle favorites update
                    Log.d("Favorites", "Favorites updated, refreshing RecyclerView.");
                    updateRecyclerView();
                }

                @Override
                public void onOrdersUpdated(String ordersData) {
                    // Handle orders update
                }

                @Override
                public void onReviewsUpdated(String reviewsData) {
                    // Handle reviews update
                }
            }) {
                @Override
                public void isFavorite(String productId, Callback callback) {
                    Log.d("Favorites", "isFavorite вызван для productId: " + productId);

                    for (Product product : favoriteProducts) {
                        if (String.valueOf(product.getProductId()).equals(productId)) {
                            Log.d("Favorites", "Продукт найден в списке, вызываем removeFavorite()");
                            removeFavorite(product);
                            return;
                        }
                    }

                    Log.d("Favorites", "Продукт не найден в списке избранного.");
                }


            });
        } else {
            adapter.notifyDataSetChanged(); // Notify adapter if data has changed
        }
        recyclerViewFavorites.setAdapter(adapter);
    }

    // Method to refresh the RecyclerView
    private void updateRecyclerView() {
        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Refresh data in the RecyclerView
        }
    }
    private void removeFavorite(Product product) {
        int position = favoriteProducts.indexOf(product);
        if (position != -1) {
            Log.d("Favorites", "Удаление из избранного: " + product.getProductId());
            Log.d("Favorites", "Список до удаления: " + favoriteProducts);

            favoriteProducts.remove(position);

            Log.d("Favorites", "Список после удаления: " + favoriteProducts);
            Log.d("Favorites", "Вызывается notifyItemRemoved для позиции: " + position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, favoriteProducts.size());

        } else {
            Log.d("Favorites", "Продукт не найден в списке избранного.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data if necessary
    }
}

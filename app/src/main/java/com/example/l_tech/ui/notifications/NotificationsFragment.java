package com.example.l_tech.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.l_tech.databinding.FragmentNotificationsBinding;
import com.example.l_tech.Model.CartItem;
import com.example.l_tech.Repozitory.UserDataListener;
import com.example.l_tech.Adapter.CartAdapter;
import com.example.l_tech.retofit2_API.ProductApi;
import com.example.l_tech.retofit2_API.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private UserDataListener userDataListener;
    private double totalPrice = 0.0;
    private ProductApi apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Получаем текущего пользователя
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDataListener = new UserDataListener(userId, new UserDataListener.DataChangeCallback() {
            @Override
            public void onCartUpdated(String cartData) {
                updateCart(cartData);
            }

            @Override
            public void onFavoritesUpdated(String favoritesData) {
            }

            @Override
            public void onOrdersUpdated(String ordersData) {
            }

            @Override
            public void onReviewsUpdated(String reviewsData) {
            }
        }) {
            @Override
            public void isFavorite(String productId, Callback callback) {

            }
        };

        // Создаём объект ApiService
        apiService = RetrofitClient.getInstance().getApi();

        // Инициализируем адаптер с передачей apiService
        cartAdapter = new CartAdapter(cartItems, userDataListener, apiService, this);
        binding.cartRecyclerView.setAdapter(cartAdapter);

        binding.checkoutButton.setOnClickListener(v -> proceedToCheckout());

        return binding.getRoot();
    }

    private void updateCart(String cartData) {
        cartItems.clear();
        totalPrice = 0.0;

        if (cartData != null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, CartItem>>(){}.getType();
                Map<String, CartItem> cartMap = gson.fromJson(cartData, type);

                for (Map.Entry<String, CartItem> entry : cartMap.entrySet()) {
                    CartItem item = entry.getValue();
                    String productId = entry.getKey();
                    int quantity = item.getQuantity();
                    double price = item.getPrice();

                    cartItems.add(new CartItem(productId, quantity, price));
                    totalPrice += price * quantity;
                }
            } catch (Exception e) {
                Log.e("Cart", "Ошибка парсинга данных: " + e.getMessage());
            }
        }

        binding.totalPrice.setText("Итоговая сумма: " + totalPrice + " ₽");
        cartAdapter.notifyDataSetChanged();
    }

    private void proceedToCheckout() {
        // Реализация оформления заказа
    }

    public void updateTotalPrice(double price) {
        this.totalPrice = price;
        binding.totalPrice.setText("Итоговая сумма: " + totalPrice + " ₽");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userDataListener.stopListening();
        binding = null;
    }
}

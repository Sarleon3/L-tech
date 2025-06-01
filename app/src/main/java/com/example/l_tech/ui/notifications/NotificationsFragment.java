package com.example.l_tech.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.l_tech.R;
import com.example.l_tech.databinding.FragmentNotificationsBinding;
import com.example.l_tech.Model.CartItem;
import com.example.l_tech.Repozitory.UserDataListener;
import com.example.l_tech.Adapter.CartAdapter;
import com.example.l_tech.retofit2_API.ProductApi;
import com.example.l_tech.retofit2_API.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        // Устанавливаем LayoutManager для RecyclerView
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Инициализация FirebaseAuth
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Проверка авторизации
        if (user == null) {
            // Если пользователь не авторизован, показываем сообщение и переходим на дашборд
            Toast.makeText(getContext(), "Для доступа к корзине необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
            
            // Переключаемся на вкладку дашборда
            if (getActivity() != null) {
                com.google.android.material.bottomnavigation.BottomNavigationView navView = 
                    getActivity().findViewById(R.id.nav_view);
                if (navView != null) {
                    navView.setSelectedItemId(R.id.navigation_dashboard);
                }
            }
            return root;
        }

        // Если пользователь авторизован, продолжаем загрузку корзины
        String userId = user.getUid();
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

        return root;
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
        // Проверяем, есть ли выбранные товары
        boolean hasSelectedItems = false;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                hasSelectedItems = true;
                break;
            }
        }

        if (!hasSelectedItems) {
            Toast.makeText(getContext(), "Выберите товары для оформления заказа", Toast.LENGTH_SHORT).show();
            return;
        }

        // Удаляем выбранные товары из корзины
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                userDataListener.removeFromCart(item.getProductId());
            }
        }

        // Показываем сообщение об успешном оформлении
        Toast.makeText(getContext(), "Заказ успешно оформлен!", Toast.LENGTH_SHORT).show();

        // Обновляем общую сумму
        updateTotalPrice(0.0);
    }

    public void updateTotalPrice(double price) {
        this.totalPrice = price;
        binding.totalPrice.setText("Итоговая сумма: " + totalPrice + " ₽");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userDataListener != null) {
            userDataListener.stopListening();
        }
        binding = null;
    }
}

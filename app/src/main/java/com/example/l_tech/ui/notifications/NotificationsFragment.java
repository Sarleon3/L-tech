package com.example.l_tech.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.l_tech.Adapter.CartAdapter;
import com.example.l_tech.Model.Product;
import com.example.l_tech.Repozitory.ProductRepository;
import com.example.l_tech.Thanks;
import com.example.l_tech.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private CartAdapter cartAdapter;
    private ProductRepository productRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        productRepository = ProductRepository.getInstance();

        // Инициализация адаптера с пустым списком
        cartAdapter = new CartAdapter(getContext(), new ArrayList<Product>());
        binding.cartRecyclerView.setAdapter(cartAdapter);
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Подписка на изменения в корзине
        productRepository.getCartProducts().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if (products != null) {
                    cartAdapter.setCartProducts(products);
                    updateTotalPrice(products);
                }
            }
        });
        binding.checkoutButton.setOnClickListener(v -> {
            productRepository.clearSelectedProducts();  // Очищаем все выбранные товары

            // Переход на экран благодарности
            Intent intent = new Intent(getActivity(), Thanks.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    private void updateTotalPrice(List<Product> products) {
        double totalPrice = 0;
        for (Product product : products) {
            // Учет только выбранных товаров
            if (product.isSelected()) {
                totalPrice += product.getPrice() * product.getQuantityInCart();
            }
        }
        binding.totalPrice.setText("Итоговая сумма: " + totalPrice + " ₽");
    }

    // Метод для удаления выбранных товаров из корзины
}

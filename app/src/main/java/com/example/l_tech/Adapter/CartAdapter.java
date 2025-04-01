package com.example.l_tech.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.l_tech.Model.CartItem;
import com.example.l_tech.Model.Product;
import com.example.l_tech.databinding.ItemCartBinding;
import com.example.l_tech.Repozitory.UserDataListener;
import com.example.l_tech.retofit2_API.ProductApi;
import com.example.l_tech.ui.notifications.NotificationsFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<CartItem> cartItems;
    private final UserDataListener userDataListener;
    private final ProductApi apiService; // Retrofit API interface
    private NotificationsFragment notificationsFragment;


    private double totalPrice = 0.0;

    public CartAdapter(List<CartItem> cartItems, UserDataListener userDataListener, ProductApi apiService,NotificationsFragment notificationsFragment) {
        this.cartItems = cartItems;
        this.userDataListener = userDataListener;
        this.apiService = apiService;
        this.notificationsFragment = notificationsFragment;  // Store the fragment reference
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Загружаем данные продукта с API
        StringBuilder productIds = new StringBuilder();
        for (CartItem cartItem : cartItems) {
            if (productIds.length() > 0) {
                productIds.append(",");
            }
            productIds.append(cartItem.getProductId());
        }

        apiService.getProductsByIds(productIds.toString()).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    for (Product product : products) {
                        try {
                            int itemProductId = Integer.parseInt(item.getProductId());
                            if (product.getProductId() == itemProductId) {
                                holder.binding.cartProductName.setText(product.getProductName());
                                holder.binding.cartProductPrice.setText(product.getPrice() + " ₽");

                                Glide.with(holder.binding.cartProductImage.getContext())
                                        .load(product.getImage())
                                        .into(holder.binding.cartProductImage);
                            }
                        } catch (NumberFormatException e) {
                            Log.e("CartAdapter", "Error parsing product ID: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("CartAdapter", "Error fetching product data: " + t.getMessage());
            }
        });

        holder.binding.cartQuantity.setText(String.valueOf(item.getQuantity()));

        // Устанавливаем состояние чекбокса
        holder.binding.cartCheckBox.setChecked(item.isSelected());

        // Обработчик изменения состояния чекбокса
        holder.binding.cartCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);  // Обновляем состояние чекбокса в модели
            updateTotalPrice();  // Пересчитываем итоговую цену
        });

        // Обработчики кнопок увеличения/уменьшения количества
        holder.binding.incrementButton.setOnClickListener(v -> {
            int count = item.getQuantity() + 1;
            userDataListener.addToCart(item.getProductId(), count, item.getPrice());
            updateTotalPrice();
        });

        holder.binding.decrementButton.setOnClickListener(v -> {
            int count = item.getQuantity();
            if (count > 1) {
                userDataListener.addToCart(item.getProductId(), count - 1, item.getPrice());
            } else {
                userDataListener.removeFromCart(item.getProductId());
            }
            updateTotalPrice();
        });
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void updateTotalPrice() {
        totalPrice = 0.0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                totalPrice += item.getPrice() * item.getQuantity();
            }
        }
        // Обновляем итоговую цену на экране
        if (notificationsFragment != null) {
            notificationsFragment.updateTotalPrice(totalPrice);
        }
    }



    static class CartViewHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;

        CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

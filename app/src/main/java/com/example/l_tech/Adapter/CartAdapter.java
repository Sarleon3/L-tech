package com.example.l_tech.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.R;
import com.example.l_tech.model.Product;
import com.example.l_tech.ui.home.HomeViewModel;

import java.util.List;

    public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

        private List<Product> cartProducts;
        private HomeViewModel homeViewModel;
        private TextView totalPriceTextView;

        public CartAdapter(List<Product> cartProducts, HomeViewModel homeViewModel, TextView totalPriceTextView) {
            this.cartProducts = cartProducts;
            this.homeViewModel = homeViewModel;
            this.totalPriceTextView = totalPriceTextView;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            Product product = cartProducts.get(position);
            holder.cartProductName.setText(product.getName());
            holder.cartProductPrice.setText(product.getPrice());
            holder.cartProductImage.setImageResource(product.getImageResId());

            holder.cartQuantity.setText(String.valueOf(product.getQuantity()));

            holder.cartItemCheckBox.setOnCheckedChangeListener(null); // Clear previous listener to prevent unintended actions

            // Устанавливаем слушатель для CheckBox, чтобы обновлять общую стоимость
            holder.cartItemCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                product.setChecked(isChecked);
                calculateTotalPrice();
            });

            // Обработчики кнопок увеличения и уменьшения количества товара
            holder.incrementButton.setOnClickListener(v -> {
                final int quantity = Integer.parseInt(holder.cartQuantity.getText().toString());
                int newQuantity = quantity + 1;
                holder.cartQuantity.setText(String.valueOf(newQuantity));
                homeViewModel.addToCart(product, newQuantity);
                calculateTotalPrice();
            });

            holder.decrementButton.setOnClickListener(v -> {
                final int quantity = Integer.parseInt(holder.cartQuantity.getText().toString());
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    holder.cartQuantity.setText(String.valueOf(newQuantity));
                    if (newQuantity == 0) {
                        homeViewModel.removeFromCart(product);
                    } else {
                        homeViewModel.addToCart(product, newQuantity);
                    }
                    calculateTotalPrice();
                }
            });

            // Устанавливаем состояние CheckBox в соответствии с выбранным продуктом
            holder.cartItemCheckBox.setChecked(product.isChecked());

            // Вычисляем общую стоимость и обновляем соответствующий TextView
            calculateTotalPrice();
        }

        @Override
        public int getItemCount() {
            return cartProducts.size();
        }

        private void calculateTotalPrice() {
            if (totalPriceTextView != null) {
                double totalPrice = 0;
                for (Product product : cartProducts) {
                    if (product.isChecked()) {
                        try {
                            double price = Double.parseDouble(product.getPrice().replace("$", ""));
                            totalPrice += product.getQuantity() * price;
                        } catch (NumberFormatException e) {
                            // Handle the case where price is not a valid number
                            e.printStackTrace();
                        }
                    }
                }
                totalPriceTextView.setText("Итоговая сумма: " + totalPrice + " ₽");
            }
        }

        public static class CartViewHolder extends RecyclerView.ViewHolder {
            TextView cartProductName, cartProductPrice, cartQuantity;
            ImageView cartProductImage;
            Button incrementButton, decrementButton;
            CheckBox cartItemCheckBox;

            public CartViewHolder(@NonNull View itemView) {
                super(itemView);
                cartProductName = itemView.findViewById(R.id.cartProductName);
                cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
                cartProductImage = itemView.findViewById(R.id.cartProductImage);
                incrementButton = itemView.findViewById(R.id.incrementButton);
                decrementButton = itemView.findViewById(R.id.decrementButton);
                cartQuantity = itemView.findViewById(R.id.cartQuantity);
                cartItemCheckBox = itemView.findViewById(R.id.cartItemCheckBox);
            }
        }
    }
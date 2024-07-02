package com.example.l_tech.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.R;
import com.example.l_tech.model.Product;
import com.example.l_tech.ui.home.HomeViewModel;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private HomeViewModel homeViewModel;

    public ProductAdapter(List<Product> productList, HomeViewModel homeViewModel) {
        this.productList = productList;
        this.homeViewModel = homeViewModel;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());
        holder.productImage.setImageResource(product.getImageResId());

        // Установка начального значения количества из ViewModel
        holder.cartQuantity.setText(String.valueOf(product.getQuantity()));

        // Наблюдаем за изменениями количества в корзине
        LiveData<List<Product>> cartProductsLiveData = homeViewModel.getCartProducts();
        cartProductsLiveData.observeForever(cartProducts -> {
            int cartQuantity = 0;
            for (Product p : cartProducts) {
                if (p.getId() == product.getId()) {
                    cartQuantity = p.getQuantity();
                    break;
                }
            }
            holder.cartQuantity.setText(String.valueOf(cartQuantity));

            // Устанавливаем видимость кнопки и счётчика в зависимости от наличия товара в корзине
            if (cartQuantity > 0) {
                holder.addToCartButton.setVisibility(View.GONE);
                holder.cartQuantityLayout.setVisibility(View.VISIBLE);
            } else {
                holder.addToCartButton.setVisibility(View.VISIBLE);
                holder.cartQuantityLayout.setVisibility(View.GONE);
            }
        });

        // Обработчик нажатия на кнопку "Add to Cart"
        holder.addToCartButton.setOnClickListener(v -> {
            homeViewModel.addToCart(product);
        });

        // Обработчики кнопок увеличения и уменьшения количества товара в корзине
        holder.incrementButton.setOnClickListener(v -> {
            final int quantity = Integer.parseInt(holder.cartQuantity.getText().toString());
            int newQuantity = quantity + 1;
            holder.cartQuantity.setText(String.valueOf(newQuantity));
            homeViewModel.addToCart(product, newQuantity);
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, cartQuantity;
        ImageView productImage;
        Button addToCartButton;
        ImageView incrementButton, decrementButton;
        LinearLayout cartQuantityLayout;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            cartQuantity = itemView.findViewById(R.id.cartQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            cartQuantityLayout = itemView.findViewById(R.id.cartQuantityLayout);
        }
    }
}
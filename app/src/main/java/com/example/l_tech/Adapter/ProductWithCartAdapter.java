package com.example.l_tech.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.Model.Product;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.ProductRepository;

import java.util.List;

public class ProductWithCartAdapter extends RecyclerView.Adapter<ProductWithCartAdapter.ViewHolder> {
    private final List<Product> productList;
    private final Context context;
    private final LiveData<List<Product>> cartProductsLiveData;

    public ProductWithCartAdapter(Context context, List<Product> products) {
        this.context = context;
        this.productList = products;
        this.cartProductsLiveData = ProductRepository.getInstance().getCartProducts();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_with_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productImage.setImageResource(product.getImageResId());
        holder.productNameText.setText(product.getName());
        holder.productRatingText.setText(String.valueOf(product.getRating()));
        holder.productCode.setText(String.valueOf(product.getCode()));
        holder.productPrice.setText(String.format("%.2f"+" ₽", product.getPrice()));

        // Обновляем иконку в зависимости от состояния избранного
        holder.favoriteButton.setImageResource(
                product.isFavorite() ? R.drawable.fill_heart_icon : R.drawable.heart_icon
        );

        // Обработчик для избранного
        holder.favoriteButton.setOnClickListener(v -> {
            boolean newFavoriteStatus = !product.isFavorite();
            ProductRepository.getInstance().updateFavoriteStatus(product, newFavoriteStatus);
            product.setFavorite(newFavoriteStatus);
            holder.favoriteButton.setImageResource(
                    newFavoriteStatus ? R.drawable.fill_heart_icon : R.drawable.heart_icon
            );
            notifyItemChanged(position);
        });

        // Получаем актуальное количество товара в корзине
        int cartQuantity = product.getQuantityInCart();
        holder.cartQuantity.setText(String.valueOf(cartQuantity));

        // Проверка состояния корзины для отображения кнопок
        if (cartQuantity > 0) {
            holder.addToCartButton.setVisibility(View.GONE); // Скрываем кнопку добавления в корзину
            holder.cartQuantityLayout.setVisibility(View.VISIBLE); // Показываем layout с количеством
        } else {
            holder.addToCartButton.setVisibility(View.VISIBLE); // Показываем кнопку добавления в корзину
            holder.cartQuantityLayout.setVisibility(View.GONE); // Скрываем layout с количеством
        }

        // Обработчик для добавления товара в корзину
        holder.addToCartButton.setOnClickListener(v -> {
            if (product.getQuantityInCart() == 0) {
                // Добавляем товар в корзину с количеством 1
                ProductRepository.getInstance().addToCart(product);
                product.setQuantityInCart(1); // Устанавливаем количество товара в корзине
            } else {
                int currentQuantity = product.getQuantityInCart();
                if (currentQuantity < product.getQuantityInStock()) {
                    ProductRepository.getInstance().updateCartQuantity(product, currentQuantity + 1);
                    product.setQuantityInCart(currentQuantity + 1);
                }
            }

            // Обновляем UI после добавления товара
            holder.addToCartButton.setVisibility(View.GONE);
            holder.cartQuantityLayout.setVisibility(View.VISIBLE);
            holder.cartQuantity.setText(String.valueOf(product.getQuantityInCart()));
        });

        // Обработчик кнопки увеличения количества товара
        holder.incrementButton.setOnClickListener(v -> {
            int currentQuantity = product.getQuantityInCart();
            if (currentQuantity < product.getQuantityInStock()) {
                int newQuantity = currentQuantity + 1;
                ProductRepository.getInstance().updateCartQuantity(product, newQuantity);
                holder.cartQuantity.setText(String.valueOf(newQuantity));
                product.setQuantityInCart(newQuantity);
                notifyItemChanged(position);
            }
        });

        // Обработчик кнопки уменьшения количества товара
        holder.decrementButton.setOnClickListener(v -> {
            int currentQuantity = product.getQuantityInCart();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                ProductRepository.getInstance().updateCartQuantity(product, newQuantity);
                holder.cartQuantity.setText(String.valueOf(newQuantity));
                product.setQuantityInCart(newQuantity);
                notifyItemChanged(position);
            } else {
                ProductRepository.getInstance().removeFromCart(product);
                holder.cartQuantityLayout.setVisibility(View.GONE);
                holder.addToCartButton.setVisibility(View.VISIBLE);
                product.setQuantityInCart(0);
                notifyItemChanged(position);
            }
        });

        // Обновляем корзину через LiveData
        cartProductsLiveData.observeForever(cartProducts -> {
            for (Product cartProduct : cartProducts) {
                if (cartProduct.getCode().equals(product.getCode())) {
                    int cartQuantityFromLiveData = cartProduct.getQuantityInCart();
                    holder.cartQuantity.setText(String.valueOf(cartQuantityFromLiveData));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productNameText, productRatingText, productCode, productPrice, cartQuantity;
        Button addToCartButton;
        ImageButton incrementButton, decrementButton, favoriteButton;
        LinearLayout cartQuantityLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favoriteButton = itemView.findViewById(R.id.favoriteButton2);
            productImage = itemView.findViewById(R.id.productImage2);
            productNameText = itemView.findViewById(R.id.productNameText2);
            productRatingText = itemView.findViewById(R.id.productRatingText2);
            productCode = itemView.findViewById(R.id.productCodeText2);
            productPrice = itemView.findViewById(R.id.PriceText2);
            cartQuantity = itemView.findViewById(R.id.cartQuantity);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            cartQuantityLayout = itemView.findViewById(R.id.cartQuantityLayout);
        }
    }
}

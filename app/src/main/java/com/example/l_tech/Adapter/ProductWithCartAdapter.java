package com.example.l_tech.Adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.l_tech.Model.Product;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.UserDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductWithCartAdapter extends RecyclerView.Adapter<ProductWithCartAdapter.ViewHolder> {
    private final List<Product> products;
    private String userId;
    private final UserDataListener userDataListener;

    public ProductWithCartAdapter(List<Product> products, String userId, UserDataListener userDataListener) {
        this.products = products;
        this.userId = userId;
        this.userDataListener = userDataListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_with_cart_item, parent, false);
        return new ViewHolder(view, userDataListener); // передаем userDataListener
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product); // Привязка данных к элементам
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProductFavoriteState(int productId) {
        userDataListener.isFavorite(String.valueOf(productId), isFavorite -> {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getProductId() == productId) {
                    notifyItemChanged(i); // Обновляем только нужный элемент
                    break;
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productCode, productName, productPrice, productRating, cartQuantity;
        private final ImageView productImage, starIcon;
        private final ImageButton favoriteButton, incrementButton, decrementButton;
        private final Button addToCartButton;
        private final View cartQuantityLayout;
        private final UserDataListener userDataListener;

        private ValueEventListener favoritesListener, cartListener;

        public ViewHolder(View itemView, UserDataListener userDataListener) {
            super(itemView);
            this.userDataListener = userDataListener;
            productCode = itemView.findViewById(R.id.productCodeText2);
            productName = itemView.findViewById(R.id.productNameText2);
            productPrice = itemView.findViewById(R.id.PriceText2);
            productRating = itemView.findViewById(R.id.productRatingText2);
            productImage = itemView.findViewById(R.id.productImage2);
            starIcon = itemView.findViewById(R.id.star2);
            favoriteButton = itemView.findViewById(R.id.favoriteButton2);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            cartQuantityLayout = itemView.findViewById(R.id.cartQuantityLayout);
            cartQuantity = itemView.findViewById(R.id.cartQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }

        public void bind(Product product) {
            productCode.setText(String.valueOf(product.getProductId()));
            productName.setText(product.getProductName());
            productPrice.setText(String.format("%.2f ₽", product.getPrice()));
            productRating.setText(String.valueOf(product.getRating()));

            // Загружаем изображение
            Glide.with(itemView.getContext())
                    .load(product.getImage())
                    .into(productImage);

            // Инициализация состояния кнопки "избранное"
            setupFavoritesListener(product);
            // Инициализация состояния корзины
            setupCartListener(product);

            // Добавляем слушатель для избранного
            favoriteButton.setOnClickListener(v -> {
                userDataListener.isFavorite(String.valueOf(product.getProductId()), isFavorite -> {
                    if (isFavorite) {
                        userDataListener.removeFromFavorites(String.valueOf(product.getProductId()));
                        favoriteButton.setImageResource(R.drawable.heart_icon);
                    } else {
                        userDataListener.addToFavorites(String.valueOf(product.getProductId()));
                        favoriteButton.setImageResource(R.drawable.fill_heart_icon);
                    }
                    notifyItemChanged(getAdapterPosition()); // Обновляем UI
                });
            });

            // Обработка клика на кнопку добавления в корзину
            addToCartButton.setOnClickListener(v -> {
                addToCartButton.setVisibility(View.GONE);
                cartQuantityLayout.setVisibility(View.VISIBLE);
                cartQuantity.setText("1");
                userDataListener.addToCart(String.valueOf(product.getProductId()), 1, product.getPrice());
            });

            // Увеличение количества в корзине
            incrementButton.setOnClickListener(v -> {
                int count = Integer.parseInt(cartQuantity.getText().toString());
                cartQuantity.setText(String.valueOf(count + 1));
                userDataListener.addToCart(String.valueOf(product.getProductId()), count + 1, product.getPrice());
            });

            // Уменьшение количества в корзине
            decrementButton.setOnClickListener(v -> {
                int count = Integer.parseInt(cartQuantity.getText().toString());
                if (count > 1) {
                    cartQuantity.setText(String.valueOf(count - 1));
                    userDataListener.addToCart(String.valueOf(product.getProductId()), count - 1, product.getPrice());
                } else {
                    cartQuantityLayout.setVisibility(View.GONE);
                    addToCartButton.setVisibility(View.VISIBLE);
                    userDataListener.removeFromCart(String.valueOf(product.getProductId()));
                }
            });
        }

        // Метод для отслеживания изменений в корзине
        private void setupCartListener(Product product) {
            if (cartListener != null) {
                FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart")
                        .removeEventListener(cartListener);
            }

            cartListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            if (cartSnapshot.getKey().equals(String.valueOf(product.getProductId()))) {
                                // Получаем количество из Firebase и безопасно конвертируем в int
                                Long quantityLong = cartSnapshot.child("quantity").getValue(Long.class);
                                if (quantityLong != null) {
                                    int quantity = quantityLong.intValue();  // Конвертируем Long в int
                                    cartQuantity.setText(String.valueOf(quantity));
                                    cartQuantityLayout.setVisibility(View.VISIBLE);
                                    addToCartButton.setVisibility(View.GONE);
                                }
                                return;
                            }
                        }
                    }
                    cartQuantityLayout.setVisibility(View.GONE);
                    addToCartButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("CartListener", "Ошибка при получении данных из корзины: " + error.getMessage());
                }
            };

            FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart")
                    .addValueEventListener(cartListener);
        }

        private void setupFavoritesListener(Product product) {
            if (favoritesListener != null) {
                FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites")
                        .removeEventListener(favoritesListener);
            }

            favoritesListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                            if (favoriteSnapshot.getKey().equals(String.valueOf(product.getProductId()))) {
                                favoriteButton.setImageResource(R.drawable.fill_heart_icon);
                                return;
                            }
                        }
                    }
                    favoriteButton.setImageResource(R.drawable.heart_icon);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FavoritesListener", "Ошибка: " + error.getMessage());
                }
            };

            FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites")
                    .addValueEventListener(favoritesListener);
        }


    }
}

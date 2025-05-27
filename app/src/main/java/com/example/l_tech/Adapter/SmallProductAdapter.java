package com.example.l_tech.Adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.l_tech.Model.Product;
import com.example.l_tech.Product_veiw;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.UserDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SmallProductAdapter extends RecyclerView.Adapter<SmallProductAdapter.ViewHolder> {
    private final List<Product> products;
    private final String userId;
    private final UserDataListener userDataListener;

    public SmallProductAdapter(List<Product> products, String userId, UserDataListener userDataListener) {
        this.products = products;
        this.userId = userId;
        this.userDataListener = userDataListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_product_item, parent, false);
        return new ViewHolder(view, userDataListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
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
        private final TextView productName, productPrice, productRating, productCode;
        private final ImageView productImage, starIcon;
        private final ImageButton favoriteButton;
        private final UserDataListener userDataListener;

        public ViewHolder(View itemView, UserDataListener userDataListener) {
            super(itemView);
            this.userDataListener = userDataListener;
            productName = itemView.findViewById(R.id.productNameText);
            productPrice = itemView.findViewById(R.id.PriceText);
            productRating = itemView.findViewById(R.id.productRatingText);
            productCode = itemView.findViewById(R.id.productCodeText);
            productImage = itemView.findViewById(R.id.productImage);
            starIcon = itemView.findViewById(R.id.star);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Product product = products.get(position);
                    Intent intent = new Intent(itemView.getContext(), Product_veiw.class);
                    intent.putExtra("product", product);
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void bind(Product product) {
            productCode.setText(String.valueOf(product.getProductId()));
            productName.setText(product.getProductName());
            productPrice.setText(String.format("%.2f ₽", product.getPrice()));
            productRating.setText(String.valueOf(product.getRating()));

            // Загружаем изображение
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImages().get(0))
                        .placeholder(R.drawable.pic2)
                        .error(R.drawable.pic2)
                        .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.pic2);
            }

            // Инициализация состояния кнопки "избранное"
            setupFavoritesListener(product);

            // Обработка клика на кнопку "избранное"
            favoriteButton.setOnClickListener(v -> {
                userDataListener.isFavorite(String.valueOf(product.getProductId()), isFavorite -> {
                    if (isFavorite) {
                        userDataListener.removeFromFavorites(String.valueOf(product.getProductId()));
                        favoriteButton.setImageResource(R.drawable.heart_icon);
                    } else {
                        userDataListener.addToFavorites(String.valueOf(product.getProductId()));
                        favoriteButton.setImageResource(R.drawable.fill_heart_icon);
                    }
                    // Обновляем UI
                    notifyItemChanged(getAdapterPosition());
                });
            });
        }

        private void setupFavoritesListener(Product product) {
            // Создаем слушатель для отслеживания изменений в избранных товарах
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                                    if (favoriteSnapshot.getKey().equals(String.valueOf(product.getProductId()))) {
                                        favoriteButton.setImageResource(R.drawable.fill_heart_icon); // Если в избранном, иконка заполнена
                                        return;
                                    }
                                }
                            }
                            favoriteButton.setImageResource(R.drawable.heart_icon); // Если не в избранном, иконка пустая
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FavoritesListener", "Ошибка: " + error.getMessage());
                        }
                    });
        }
    }
}

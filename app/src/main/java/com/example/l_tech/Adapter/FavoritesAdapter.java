package com.example.l_tech.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.R;
import com.example.l_tech.model.Product;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private List<Product> products;

    public FavoritesAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productImage.setImageResource(product.getImageResId());
        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());
        holder.favoriteButton.setImageResource(product.isFavorite() ? R.drawable.heart_fill : R.drawable.heart_line);

        holder.favoriteButton.setOnClickListener(v -> {
            product.setFavorite(!product.isFavorite());
            notifyItemRemoved(position);
            // Дополнительная логика для удаления из избранного
        });

        // Логика для добавления в корзину, если необходимо
    }

    @Override
    public int getItemCount() {
        if (products != null) {
            return products.size();
        } else {
            return 0; // or handle differently based on your application logic
        }
    }

    static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        ImageButton favoriteButton;

        FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
}
package com.example.l_tech.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.l_tech.Model.Product;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.ProductRepository;

import java.util.List;

public class SmallProductAdapter extends RecyclerView.Adapter<SmallProductAdapter.ViewHolder> {
    private final List<Product> productList;
    private final Context context;

    public SmallProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.productList = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.small_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productImage.setImageResource(product.getImageResId());
        holder.productNameText.setText(product.getName());
        holder.productCode.setText(String.valueOf(product.getCode()));
        holder.productRatingText.setText(String.valueOf(product.getRating()));
        holder.ProductPrice.setText((String.format("%.2f"+" ₽", product.getPrice())));

        holder.favoriteButton.setImageResource(
                product.isFavorite() ? R.drawable.fill_heart_icon : R.drawable.heart_icon
        );

        holder.favoriteButton.setOnClickListener(v -> {
            boolean newFavoriteStatus = !product.isFavorite();
            ProductRepository.getInstance().updateFavoriteStatus(product, newFavoriteStatus);
            product.setFavorite(newFavoriteStatus);

            // Обновляем иконку на кнопке в зависимости от состояния избранного
            holder.favoriteButton.setImageResource(
                    newFavoriteStatus ? R.drawable.fill_heart_icon : R.drawable.heart_icon
            );

            // Уведомляем адаптер об изменении позиции
            notifyItemChanged(position);
        });

    }
    public void updateData(List<Product> newProducts) {
        this.productList.clear();
        this.productList.addAll(newProducts);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productNameText, productRatingText,ProductPrice,productCode;
        ImageButton favoriteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            productCode = itemView.findViewById(R.id.productCodeText);
            productImage = itemView.findViewById(R.id.productImage);
            productNameText = itemView.findViewById(R.id.productNameText);
            productRatingText = itemView.findViewById(R.id.productRatingText);
            ProductPrice = itemView.findViewById(R.id.PriceText);
        }
    }
}

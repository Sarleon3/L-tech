package com.example.l_tech.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.Adapter.ProductWithCartAdapter;
import com.example.l_tech.Adapter.SmallProductAdapter;
import com.example.l_tech.Model.Product;
import com.example.l_tech.Model.ProductBlock;
import com.example.l_tech.Model.ProductBlockType;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.UserDataListener;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "HomeAdapter";  // Тэг для логирования

    private final Context context;
    private final List<ProductBlock> blocks;
    private final String userId;
    private final UserDataListener userDataListener;

    public HomeAdapter(Context context, List<ProductBlock> blocks, String userId, UserDataListener userDataListener) {
        this.context = context;
        this.blocks = blocks;
        this.userId = userId;
        this.userDataListener = userDataListener;

        Log.d(TAG, "HomeAdapter: userDataListener передан: " + (userDataListener != null ? "OK" : "NULL"));
    }

    @Override
    public int getItemViewType(int position) {
        return blocks.get(position).getBlockType() == ProductBlockType.SMALL ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: создаем ViewHolder, viewType = " + viewType);

        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.small_product_block, parent, false);
            return new SmallProductViewHolder(view, userId, userDataListener);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.product_with_cart_block, parent, false);
            return new ProductWithCartViewHolder(view, userId, userDataListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductBlock block = blocks.get(position);
        Log.d(TAG, "onBindViewHolder: связываем данные для позиции " + position + ", категория = " + block.getCategoryName());

        if (holder instanceof SmallProductViewHolder) {
            ((SmallProductViewHolder) holder).bind(block.getCategoryName(), block.getProducts());
        } else if (holder instanceof ProductWithCartViewHolder) {
            ((ProductWithCartViewHolder) holder).bind(block.getCategoryName(), block.getProducts());
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: количество блоков = " + blocks.size());
        return blocks.size();
    }

    public static class SmallProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitle;
        private final RecyclerView recyclerView;
        private final String userId;
        private final UserDataListener userDataListener;

        public SmallProductViewHolder(View itemView, String userId, UserDataListener userDataListener) {
            super(itemView);
            this.userId = userId;
            this.userDataListener = userDataListener;

            Log.d(TAG, "SmallProductViewHolder: userDataListener в конструкторе: " + (userDataListener != null ? "OK" : "NULL"));

            categoryTitle = itemView.findViewById(R.id.small_category_title);
            recyclerView = itemView.findViewById(R.id.product_recycler_view);

            // Устанавливаем горизонтальный LayoutManager
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        public void bind(String categoryName, List<Product> products) {
            Log.d(TAG, "SmallProductViewHolder: привязываем данные, категория = " + categoryName);
            Log.d(TAG, "SmallProductViewHolder: userDataListener в bind: " + (userDataListener != null ? "OK" : "NULL"));
            categoryTitle.setText(categoryName);

            SmallProductAdapter adapter = new SmallProductAdapter(products, userId, userDataListener);
            recyclerView.setAdapter(adapter);

            // Проверка состояния избранного для каждого товара
            for (Product product : products) {
                userDataListener.isFavorite(String.valueOf(product.getProductId()), isFavorite -> {
                    if (isFavorite) {
                        // Обновляем UI в зависимости от состояния
                        // Например, устанавливаем иконку сердца в заполненное
                        adapter.updateProductFavoriteState(product.getProductId());
                    } else {
                        adapter.updateProductFavoriteState(product.getProductId());
                    }
                });
            }
        }
    }

    public static class ProductWithCartViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitle;
        private final RecyclerView recyclerView;
        private final String userId;
        private final UserDataListener userDataListener;

        public ProductWithCartViewHolder(View itemView, String userId, UserDataListener userDataListener) {
            super(itemView);
            this.userId = userId;
            this.userDataListener = userDataListener;

            Log.d(TAG, "ProductWithCartViewHolder: userDataListener в конструкторе: " + (userDataListener != null ? "OK" : "NULL"));

            categoryTitle = itemView.findViewById(R.id.cart_category_title);
            recyclerView = itemView.findViewById(R.id.cart_product_recycler);

            // Устанавливаем горизонтальный LayoutManager
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        public void bind(String categoryName, List<Product> products) {
            Log.d(TAG, "ProductWithCartViewHolder: привязываем данные, категория = " + categoryName);
            Log.d(TAG, "ProductWithCartViewHolder: userDataListener в bind: " + (userDataListener != null ? "OK" : "NULL"));
            categoryTitle.setText(categoryName);

            ProductWithCartAdapter adapter = new ProductWithCartAdapter(products, userId, userDataListener);
            recyclerView.setAdapter(adapter);

            // Проверка состояния избранного для каждого товара
            for (Product product : products) {
                userDataListener.isFavorite(String.valueOf(product.getProductId()), isFavorite -> {
                    if (isFavorite) {
                        // Обновляем UI в зависимости от состояния
                        // Например, устанавливаем иконку сердца в заполненное
                        adapter.updateProductFavoriteState(product.getProductId());
                    } else {
                        adapter.updateProductFavoriteState(product.getProductId());
                    }
                });
            }
        }
    }

}

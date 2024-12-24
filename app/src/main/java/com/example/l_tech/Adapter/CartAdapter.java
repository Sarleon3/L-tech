package com.example.l_tech.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.Model.CartDiffCallback;
import com.example.l_tech.Model.Product;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<Product> cartProducts;
    private ProductRepository productRepository;

    public CartAdapter(Context context, List<Product> cartProducts) {
        this.context = context;
        this.cartProducts = cartProducts;
        productRepository = ProductRepository.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartProducts.get(position);

        holder.productName.setText(product.getName());
        holder.productImage.setImageResource(product.getImageResId());
        holder.productPrice.setText(String.format("%.2f" + " ₽", product.getPrice()));

        // Отображаем текущее количество товара в корзине
        int cartQuantity = product.getQuantityInCart();
        holder.quantity.setText(String.valueOf(cartQuantity));

        holder.incrementButton.setOnClickListener(v -> {
            int currentQuantity = product.getQuantityInCart();
            if (currentQuantity < product.getQuantityInStock()) {
                int newQuantity = currentQuantity + 1;
                productRepository.updateCartQuantity(product, newQuantity); // Используем новый метод

                Log.d("CartAdapter", "Product " + product.getName() + ": Increased quantity to " + product.getQuantityInCart());

                // Обновляем UI
                holder.quantity.setText(String.valueOf(product.getQuantityInCart()));
                notifyItemChanged(position); // Обновляем текущий элемент
            }
        });

        holder.decrementButton.setOnClickListener(v -> {
            int currentQuantity = product.getQuantityInCart();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                productRepository.updateCartQuantity(product, newQuantity); // Используем новый метод

                // Обновляем UI
                holder.quantity.setText(String.valueOf(product.getQuantityInCart()));
                notifyItemChanged(position); // Обновляем текущий элемент
            } else {
                productRepository.updateCartQuantity(product, 0); // Используем новый метод для удаления товара

                // Обновляем UI
                holder.quantity.setText("0");
                notifyItemRemoved(position); // Убираем элемент из списка
            }
        });

        // Обработчик выбора товара
        holder.cartItemCheckBox.setChecked(product.isSelected());
        holder.cartItemCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setSelected(isChecked);
            productRepository.updateProductSelection(product, isChecked);  // Используем новый метод
            updateTotalPrice();
        });

        // Обновляем общую сумму при изменении корзины
        new Handler(Looper.getMainLooper()).post(() -> {
            updateTotalPrice();
        });
    }



    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (Product product : cartProducts) {
            if (product.isSelected()) {
                totalPrice += product.getPrice() * product.getQuantityInCart();
            }
        }
        // Отправка итоговой суммы в UI (например, через коллбек или обновление UI)
    }

    public void setCartProducts(List<Product> products) {
        // Сохраняем старый список
        List<Product> oldList = new ArrayList<>(this.cartProducts);
        this.cartProducts = products;

        // Создаем и применяем DiffUtil
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CartDiffCallback(oldList, products));

        // Применяем изменения
        diffResult.dispatchUpdatesTo(this);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, quantity;
        CheckBox cartItemCheckBox;
        ImageView productImage;
        AppCompatImageButton incrementButton, decrementButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cartProductImage);
            cartItemCheckBox = itemView.findViewById(R.id.cartItemCheckBox);
            productName = itemView.findViewById(R.id.cartProductName);
            productPrice = itemView.findViewById(R.id.cartProductPrice);
            quantity = itemView.findViewById(R.id.cartQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }
}

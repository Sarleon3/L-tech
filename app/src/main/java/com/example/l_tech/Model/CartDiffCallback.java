package com.example.l_tech.Model;
import androidx.recyclerview.widget.DiffUtil;
import com.example.l_tech.Model.Product;
import java.util.List;

public class CartDiffCallback extends DiffUtil.Callback {
    private final List<Product> oldList;
    private final List<Product> newList;

    public CartDiffCallback(List<Product> oldList, List<Product> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Сравниваем элементы по их уникальному коду
        return oldList.get(oldItemPosition).getCode().equals(newList.get(newItemPosition).getCode());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Сравниваем содержимое продуктов
        Product oldProduct = oldList.get(oldItemPosition);
        Product newProduct = newList.get(newItemPosition);
        return oldProduct.getQuantityInCart() == newProduct.getQuantityInCart() &&
                oldProduct.isSelected() == newProduct.isSelected();
    }
}

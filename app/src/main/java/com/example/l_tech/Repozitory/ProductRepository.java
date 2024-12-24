package com.example.l_tech.Repozitory;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.l_tech.Model.Product;
import com.example.l_tech.R;
import com.example.l_tech.retofit2_API.ProductApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static ProductRepository instance;
    private final List<Product> products;
    private ProductApi apiService;
    private final MutableLiveData<List<Product>> cartProducts;
    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();

    private ProductRepository() {
        products = new ArrayList<>();
        cartProducts = new MutableLiveData<>(new ArrayList<>());
        loadProducts();
    }

    public static ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public LiveData<List<Product>> getCartProducts() {
        return cartProducts;
    }
    public LiveData<List<Product>> getProducts() {
        return productsLiveData;
    }


    public void addToCart(Product product) {
        List<Product> currentCart = new ArrayList<>(cartProducts.getValue());
        boolean productFound = false;

        for (Product cartProduct : currentCart) {
            if (cartProduct.getCode().equals(product.getCode())) {
                int currentQuantity = cartProduct.getQuantityInCart();
                if (currentQuantity < cartProduct.getQuantityInStock()) {
                    cartProduct.setQuantityInCart(currentQuantity + 1);
                }
                productFound = true;
                break;
            }
        }

        if (!productFound) {
            if (product.getQuantityInStock() > 0) {
                Product newProduct = new Product(
                        product.getName(),
                        product.getCode(),
                        product.getRating(),
                        product.getImageResId(),
                        product.getCategory(),
                        product.isFavorite(),
                        product.getPrice(),
                        product.getQuantityInStock()
                );
                newProduct.setQuantityInCart(1);
                currentCart.add(newProduct);
            }
        }

        cartProducts.setValue(currentCart);
    }

    public void clearSelectedProducts() {
        List<Product> currentCart = new ArrayList<>(cartProducts.getValue());

        // Убираем из списка только те товары, у которых isSelected = true
        currentCart.removeIf(Product::isSelected);

        // Устанавливаем новый экземпляр списка в LiveData, чтобы гарантировать обновление
        cartProducts.setValue(new ArrayList<>(currentCart));
    }



    public void updateCartQuantity(Product product, int quantity) {
        List<Product> currentCart = new ArrayList<>(cartProducts.getValue());
        boolean productUpdated = false;

        for (int i = 0; i < currentCart.size(); i++) {
            Product cartProduct = currentCart.get(i);

            if (cartProduct.getCode().equals(product.getCode())) {
                if (quantity <= 0) {
                    currentCart.remove(i);
                } else {
                    int updatedQuantity = Math.min(quantity, cartProduct.getQuantityInStock());
                    cartProduct.setQuantityInCart(updatedQuantity);
                }
                productUpdated = true;
                break;
            }
        }

        if (productUpdated) {
            cartProducts.setValue(currentCart); // Убедитесь, что уведомление происходит здесь
        }
    }



    public void removeFromCart(Product product) {
        List<Product> currentCart = new ArrayList<>(cartProducts.getValue());
        currentCart.removeIf(cartProduct -> cartProduct.getCode().equals(product.getCode()));
        cartProducts.setValue(currentCart);
    }

    public void updateFavoriteStatus(Product product, boolean isFavorite) {
        for (Product p : products) {
            if (p.getCode().equals(product.getCode())) {
                p.setFavorite(isFavorite);
                break;
            }
        }
    }
    public List<Product> getFavoriteProducts() {
        List<Product> favoriteProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.isFavorite()) {
                favoriteProducts.add(product);
            }
        }
        return favoriteProducts;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory().equals(category)) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    public void updateProductSelection(Product product, boolean isSelected) {
        List<Product> currentCart = new ArrayList<>(cartProducts.getValue());
        for (Product cartProduct : currentCart) {
            if (cartProduct.getCode().equals(product.getCode())) {
                cartProduct.setSelected(isSelected); // Обновляем только флаг isSelected
                break;
            }
        }
        cartProducts.setValue(currentCart);
    }
    private void loadProducts() {
    }

}
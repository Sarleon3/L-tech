package com.example.l_tech.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.l_tech.R;
import com.example.l_tech.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Product>> productsCategory1;
    private MutableLiveData<List<Product>> productsCategory2;
    private MutableLiveData<List<Product>> cartProducts;

    public HomeViewModel() {
        productsCategory1 = new MutableLiveData<>();
        productsCategory2 = new MutableLiveData<>();
        cartProducts = new MutableLiveData<>();
        loadProducts();
    }

    private void loadProducts() {
        // Загрузка тестовых данных для категорий продуктов
        List<Product> products1 = new ArrayList<>();
        products1.add(new Product(1, "Product 1", "10", R.drawable.media));
        products1.add(new Product(2, "Product 2", "20", R.drawable.media));
        products1.add(new Product(3, "Product 3", "30", R.drawable.media));
        products1.add(new Product(4, "Product 4", "40", R.drawable.media));
        productsCategory1.setValue(products1);

        List<Product> products2 = new ArrayList<>();
        products2.add(new Product(5, "Product 5", "50", R.drawable.media));
        products2.add(new Product(6, "Product 6", "60", R.drawable.media));
        products2.add(new Product(7, "Product 7", "70", R.drawable.media));
        products2.add(new Product(8, "Product 8", "80", R.drawable.media));
        productsCategory2.setValue(products2);

        cartProducts.setValue(new ArrayList<>()); // Инициализация пустой корзины
    }

    public LiveData<List<Product>> getProductsCategory1() {
        return productsCategory1;
    }

    public LiveData<List<Product>> getProductsCategory2() {
        return productsCategory2;
    }

    public LiveData<List<Product>> getCartProducts() {
        return cartProducts;
    }

    public void addToCart(Product product) {
        List<Product> currentCartProducts = cartProducts.getValue();
        if (currentCartProducts == null) {
            currentCartProducts = new ArrayList<>();
        }

        Product existingProduct = findProductById(currentCartProducts, product.getId());
        if (existingProduct != null) {
            existingProduct.setQuantity(existingProduct.getQuantity() + 1);
        } else {
            product.setQuantity(1);
            currentCartProducts.add(product);
        }

        cartProducts.setValue(currentCartProducts);
    }

    public void addToCart(Product product, int newQuantity) {
        List<Product> currentCartProducts = cartProducts.getValue();
        if (currentCartProducts == null) {
            currentCartProducts = new ArrayList<>();
        }

        Product existingProduct = findProductById(currentCartProducts, product.getId());
        if (existingProduct != null) {
            existingProduct.setQuantity(newQuantity);
        } else {
            product.setQuantity(newQuantity);
            currentCartProducts.add(product);
        }

        cartProducts.setValue(currentCartProducts);
    }

    public void removeFromCart(Product product) {
        List<Product> currentCartProducts = cartProducts.getValue();
        if (currentCartProducts != null) {
            Product existingProduct = findProductById(currentCartProducts, product.getId());
            if (existingProduct != null) {
                if (existingProduct.getQuantity() > 1) {
                    existingProduct.setQuantity(existingProduct.getQuantity() - 1);
                } else {
                    currentCartProducts.remove(existingProduct);
                }
                cartProducts.setValue(currentCartProducts);
            }
        }
    }

    public void clearCart() {
        cartProducts.setValue(new ArrayList<>());
    }

    private Product findProductById(List<Product> productList, int id) {
        for (Product p : productList) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}
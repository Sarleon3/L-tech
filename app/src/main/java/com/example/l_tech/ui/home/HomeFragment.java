package com.example.l_tech.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.Model.Product;
import com.example.l_tech.Model.ProductBlock;
import com.example.l_tech.Model.ProductBlockType;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.UserDataListener;
import com.example.l_tech.retofit2_API.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";  // Тэг для логирования

    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private HomeAdapter homeAdapter;
    private List<ProductBlock> blocks = new ArrayList<>();
         String userId ;
    private UserDataListener.DataChangeCallback callback;
    private UserDataListener userDataListener;


    private final List<Pair<String, ProductBlockType>> categoryBlocks = Arrays.asList(
            new Pair<>("Смартфоны", ProductBlockType.SMALL),
            new Pair<>("Смартфоны", ProductBlockType.WITH_CART),
            new Pair<>("Смартфоны", ProductBlockType.SMALL),
            new Pair<>("Смартфоны", ProductBlockType.WITH_CART),
             new Pair<>("Смартфоны", ProductBlockType.WITH_CART)
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: фрагмент загружается");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.containerLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();  // Получаем уникальный ID пользователя
        } else {
            userId = "guest";
        }


        // Создаем callback
        callback = new UserDataListener.DataChangeCallback() {
            @Override
            public void onCartUpdated(String cartData) {
                // Логика обновления корзины
            }

            @Override
            public void onFavoritesUpdated(String favoriteId) {
                // Логика обновления избранного
                Log.d(TAG, "Избранное обновлено: " + favoriteId);
            }

            @Override
            public void onOrdersUpdated(String ordersData) {
                // Логика обновления заказов
            }

            @Override
            public void onReviewsUpdated(String reviewsData) {
                // Логика обновления отзывов
            }
        };
        userDataListener = new UserDataListener(userId, callback) {
            @Override
            public void isFavorite(String productId, Callback callback) {

            }
        };

        loadProducts(); // Загружаем продукты
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void loadProducts() {
        AtomicInteger completedRequests = new AtomicInteger(0);
        Log.d(TAG, "loadProducts: Начинаем загрузку продуктов. Всего категорий: " + categoryBlocks.size());

        for (Pair<String, ProductBlockType> entry : categoryBlocks) {
            String category = entry.first;
            ProductBlockType type = entry.second;
            Log.d(TAG, "Запрос к API: категория = " + category + ", тип блока = " + type);

            RetrofitClient.getInstance().getApi().getProductsByCategory(category)
                    .enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Product> products = response.body();
                                Log.d(TAG, "Успешный ответ от API. Категория: " + category + ". Получено товаров: " + products.size());

                                if (!products.isEmpty()) {
                                    List<Product> sublist = products.subList(0, Math.min(10, products.size()));

                                    // Логирование списка товаров
                                    for (Product product : sublist) {
                                        Log.d(TAG, "Товар: ID=" + product.getProductId() +
                                                ", Name=" + product.getProductName() +
                                                ", Price=" + product.getPrice() +
                                                ", Images=" + product.getImage());
                                    }

                                    blocks.add(new ProductBlock(type, category, sublist));
                                } else {
                                    Log.w(TAG, "Категория: " + category + " - пустой список товаров");
                                }
                            } else {
                                Log.e(TAG, "Ошибка от API. Категория: " + category + ". Код: " + response.code() + ", Сообщение: " + response.message());
                            }

                            if (completedRequests.incrementAndGet() == categoryBlocks.size()) {
                                updateRecyclerView();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {
                            Log.e(TAG, "Ошибка запроса к API. Категория: " + category + ". Ошибка: " + t.getMessage(), t);

                            if (completedRequests.incrementAndGet() == categoryBlocks.size()) {
                                updateRecyclerView();
                            }
                        }
                    });
        }
    }




    private void updateRecyclerView() {
        requireActivity().runOnUiThread(() -> {
            Log.d(TAG, "updateRecyclerView: обновляем список товаров в RecyclerView");
            homeAdapter =  new HomeAdapter(getContext(), blocks, userId, userDataListener);
            recyclerView.setAdapter(homeAdapter);
        });
    }

}

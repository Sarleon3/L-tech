package com.example.l_tech.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.Adapter.ProductWithCartAdapter;
import com.example.l_tech.Model.Product;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.ProductRepository;

import java.util.List;

public class favorite extends Fragment {

    private RecyclerView recyclerViewFavorites;
    private ProductWithCartAdapter adapter;

    public favorite() {
        // Required empty public constructor
    }

    public static favorite newInstance(String param1, String param2) {
        favorite fragment = new favorite();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Сетка с 2 колонками

        // Получаем список избранных товаров
        List<Product> favoriteProducts = ProductRepository.getInstance().getFavoriteProducts();

        // Создаем адаптер и устанавливаем его в RecyclerView
        adapter = new ProductWithCartAdapter(getContext(), favoriteProducts);
        recyclerViewFavorites.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем данные избранных товаров при возвращении к фрагменту
        List<Product> updatedFavoriteProducts = ProductRepository.getInstance().getFavoriteProducts();
    }
}

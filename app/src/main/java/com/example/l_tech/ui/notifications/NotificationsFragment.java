package com.example.l_tech.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.Adapter.FavoritesAdapter;
import com.example.l_tech.R;
import com.example.l_tech.ui.home.HomeViewModel;

public class NotificationsFragment extends Fragment {

        private HomeViewModel homeViewModel;
        private FavoritesAdapter favoritesAdapter;

        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_notifications, container, false);

            RecyclerView favoritesRecyclerView = root.findViewById(R.id.favoritesRecyclerView);
            favoritesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2))
            ;
//            List<Product> favoriteProductsList = new ArrayList<>();
//            favoritesAdapter = new FavoritesAdapter(favoriteProductsList(), product -> {
//                homeViewModel.toggleFavorite(product); // Вызываем метод toggleFavorite в HomeViewModel
//            });
//
            favoritesRecyclerView.setAdapter(favoritesAdapter);

//            homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
//
//            // Наблюдаем за изменениями в списке избранных продуктов
//            homeViewModel.getCartProducts().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
//                @Override
//                public void onChanged(List<Product> products) {
////                    List<Product> favoriteProducts = filterFavoriteProducts(products);
////                    favoritesAdapter.setProducts(favoriteProducts);
//                }
//            });
//
            return root;
        }

//        private List<Product> filterFavoriteProducts(List<Product> products) {
//            List<Product> favoriteProducts = new ArrayList<>();
//            for (Product product : products) {
//                if (product.isFavorite()) {
//                    favoriteProducts.add(product);
//                }
//            }
//            return favoriteProducts;
//        }
    }

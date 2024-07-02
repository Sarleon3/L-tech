package com.example.l_tech.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.l_tech.Adapter.BannerAdapter;
import com.example.l_tech.Adapter.ProductAdapter;
import com.example.l_tech.R;
import com.example.l_tech.model.Banner;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView productRecyclerView1, productRecyclerView2;
    private ViewPager2 viewPager2;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRecyclerView1 = view.findViewById(R.id.productRecyclerView1);
        productRecyclerView2 = view.findViewById(R.id.productRecyclerView2);
        viewPager2 = view.findViewById(R.id.viewPager2);

        productRecyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        productRecyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        homeViewModel.getProductsCategory1().observe(getViewLifecycleOwner(), products -> {
            ProductAdapter productAdapter = new ProductAdapter(products, homeViewModel);
            productRecyclerView1.setAdapter(productAdapter);
        });

        homeViewModel.getProductsCategory2().observe(getViewLifecycleOwner(), products -> {
            ProductAdapter productAdapter = new ProductAdapter(products, homeViewModel);
            productRecyclerView2.setAdapter(productAdapter);
        });

        List<Banner> bannerList = getBannerList(); // Загрузите ваш список баннеров здесь

        BannerAdapter bannerPagerAdapter = new BannerAdapter(bannerList);
        viewPager2.setAdapter(bannerPagerAdapter);
    }

    private List<Banner> getBannerList() {
        // Загрузите тестовые данные для баннеров или любые другие данные
        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner(R.drawable.media));
        banners.add(new Banner(R.drawable.media));
        banners.add(new Banner(R.drawable.media));
        return banners;
    }
}

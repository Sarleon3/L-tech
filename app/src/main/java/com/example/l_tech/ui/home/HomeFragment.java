package com.example.l_tech.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.l_tech.Adapter.BannerAdapter;
import com.example.l_tech.Adapter.ProductWithCartAdapter;
import com.example.l_tech.Adapter.SmallProductAdapter;
import com.example.l_tech.Model.Banner;
import com.example.l_tech.Model.Product;
import com.example.l_tech.R;
import com.example.l_tech.Repozitory.BannerRepozitory;
import com.example.l_tech.Repozitory.ProductRepository;
import com.example.l_tech.databinding.FragmentHomeBinding;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductRepository productRepository;

    // Переменные адаптеров для каждой категории
    private ProductWithCartAdapter categoryAAdapter;
    private SmallProductAdapter categoryBAdapter;
    private ProductWithCartAdapter categoryCAdapter;
    private SmallProductAdapter categoryDAdapter;

    String Category1 = "Category A";
    String Category2 = "Category B";
    String Category3 = "Category C";
    String Category4 = "Category D";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Инициализация репозитория
        productRepository = ProductRepository.getInstance();

        // Настройка ViewPager2 для баннеров
        List<Banner> banners = BannerRepozitory.getBanners();
        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), banners);
        binding.bannerViewPager.setAdapter(bannerAdapter);

        // Загрузка данных для категорий
        List<Product> categoryAProducts = productRepository.getProductsByCategory(Category1);
        List<Product> categoryBProducts = productRepository.getProductsByCategory(Category2);
        List<Product> categoryCProducts = productRepository.getProductsByCategory(Category3);
        List<Product> categoryDProducts = productRepository.getProductsByCategory(Category4);

        // Настройка блока Category A с адаптером
        View categoryAView = inflater.inflate(R.layout.product_with_cart_block, container, false);
        binding.containerLayout.addView(categoryAView);
        categoryAAdapter = new ProductWithCartAdapter(getContext(), categoryAProducts);
        TextView categoryATextView = categoryAView.findViewById(R.id.cart_category_title);
        categoryATextView.setText(Category1);
        RecyclerView categoryARecycler = categoryAView.findViewById(R.id.cart_product_recycler);
        categoryARecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryARecycler.setAdapter(categoryAAdapter);

        // Настройка блока Category B с адаптером
        View categoryBView = inflater.inflate(R.layout.small_product_block , container, false);
        binding.containerLayout.addView(categoryBView);
        categoryBAdapter = new SmallProductAdapter(getContext(), categoryBProducts);
        TextView categoryBTextView = categoryBView.findViewById(R.id.small_category_title);
        categoryBTextView.setText(Category2);
        RecyclerView categoryBRecycler = categoryBView.findViewById(R.id.product_recycler_view);
        categoryBRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryBRecycler.setAdapter(categoryBAdapter);

        // Настройка блока Category C с адаптером
        View categoryCView = inflater.inflate(R.layout.product_with_cart_block, container, false);
        binding.containerLayout.addView(categoryCView);
        categoryCAdapter = new ProductWithCartAdapter(getContext(), categoryCProducts);
        TextView categoryCTextView = categoryCView.findViewById(R.id.cart_category_title);
        categoryCTextView.setText(Category3);
        RecyclerView categoryCRecycler = categoryCView.findViewById(R.id.cart_product_recycler);
        categoryCRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryCRecycler.setAdapter(categoryCAdapter);

        // Настройка блока Category D с адаптером
        View categoryDView = inflater.inflate(R.layout.small_product_block, container, false);
        binding.containerLayout.addView(categoryDView);
        categoryDAdapter = new SmallProductAdapter(getContext(), categoryDProducts);
        TextView categoryDTextView = categoryDView.findViewById(R.id.small_category_title);
        categoryDTextView.setText(Category4);
        RecyclerView categoryDRecycler = categoryDView.findViewById(R.id.product_recycler_view);
        categoryDRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryDRecycler.setAdapter(categoryDAdapter);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.example.l_tech.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l_tech.Adapter.CartAdapter;
import com.example.l_tech.R;
import com.example.l_tech.ui.home.HomeViewModel;

public class BlankFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private HomeViewModel homeViewModel;
    private TextView totalPriceTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalPriceTextView = view.findViewById(R.id.totalPrice);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        homeViewModel.getCartProducts().observe(getViewLifecycleOwner(), products -> {
            cartAdapter = new CartAdapter(products, homeViewModel, totalPriceTextView);
            cartRecyclerView.setAdapter(cartAdapter);
        });

        Button checkoutButton = view.findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> {
            // Добавьте здесь логику для оформления заказа
        });
    }
}

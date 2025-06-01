package com.example.l_tech.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.l_tech.login;
import com.example.l_tech.SettingsActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.l_tech.R;
import com.example.l_tech.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseAuth auth;
    Button log;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Подключаем ViewModel
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        log = root.findViewById(R.id.button7);
        // Инициализация FirebaseAuth
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Обновление UI, если пользователь авторизован
        if (user != null) {
            updateUI(user);
        }

        log.setOnClickListener(v -> {
            i();
        });

        // Добавляем обработчик нажатия на иконку настроек
        ImageView settingsIcon = root.findViewById(R.id.settings_icon);
        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        return root;
    }

   private void i (){
        Intent intent = new Intent(getContext(), login.class);
        startActivity(intent);
   }
    private void updateUI(FirebaseUser user) {
        // Получаем ссылки на элементы
        ImageView profileImage = binding.getRoot().findViewById(R.id.profile_image);
        LinearLayout textContainer = binding.getRoot().findViewById(R.id.text_container);
        ImageView settingsIcon = binding.getRoot().findViewById(R.id.settings_icon);

        TextView w = binding.getRoot().findViewById(R.id.textView5);
        TextView q = binding.getRoot().findViewById(R.id.textView6);
        log = binding.getRoot().findViewById(R.id.button7);
        
        TextView userEmail = binding.getRoot().findViewById(R.id.user_email);

        log.setVisibility(View.GONE);
        w.setVisibility(View.GONE);
        q.setVisibility(View.GONE);

        profileImage.setVisibility(View.VISIBLE);
        textContainer.setVisibility(View.VISIBLE);
        settingsIcon.setVisibility(View.VISIBLE);

        // Заполняем данные пользователя
        userEmail.setText(user.getEmail() != null ? user.getEmail() : "Email не указан");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

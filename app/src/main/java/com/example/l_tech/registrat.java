package com.example.l_tech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class registrat extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Инициализация UI элементов
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);

        // Обработчик нажатия на кнопку "Зарегистрироваться"
        btnRegister.setOnClickListener(view -> registerUser());

        // Обработчик нажатия на кнопку "Вернуться к входу"
        btnBackToLogin.setOnClickListener(view -> {
            finish(); // Закрываем текущую активность и возвращаемся к экрану входа
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Проверка заполнения всех полей
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка длины пароля
        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        // Регистрация пользователя
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(registrat.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        // После успешной регистрации переходим на экран входа
                        Intent intent = new Intent(registrat.this, login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(registrat.this, "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
package com.example.l_tech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.Regist);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);

        // Обработчик нажатия на кнопку "Войти"
        btnLogin.setOnClickListener(view -> loginUser());

        // Обработчик нажатия на кнопку "Зарегистрироваться"
        btnRegister.setOnClickListener(view -> {
            startActivity(new Intent(login.this, registrat.class));
        });

        // Восстановление пароля
        tvForgotPassword.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            if (!email.isEmpty()) {
                resetPassword(email);
            } else {
                Toast.makeText(login.this, "Введите email для сброса пароля", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод для входа пользователя
    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(login.this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(login.this, "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Метод для сброса пароля
    private void resetPassword(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(login.this, "Инструкции по сбросу пароля отправлены на email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(login.this, "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

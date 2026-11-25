package com.example.telfquito_soap_java.view;

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

import com.example.telfquito_soap_java.R;
import com.example.telfquito_soap_java.controller.LoginController;
import com.example.telfquito_soap_java.service.AuthService;

public class LoginActivity extends AppCompatActivity {
    private LoginController loginController;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginController = new LoginController(new AuthService());

        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin(){
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese usuario y contraseña",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Continuar con la autenticación
        loginController.attemptLogin(username, password, new LoginController.LoginCallback() {
            @Override
            public void onLoginSuccess(boolean isAuthenticated) {
                runOnUiThread(() -> {
                    if (isAuthenticated) {
                        startActivity(new Intent(LoginActivity.this, TelefonosActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Usuario o Contraseña Incorrecto",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onLoginError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this,
                            "Error de conexión: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            }
        });

    }

}
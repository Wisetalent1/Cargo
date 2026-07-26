package com.nicargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nicargo.app.R;
import com.nicargo.app.models.LoginResponse;
import com.nicargo.app.utils.AuthTokenManager;
import com.nicargo.app.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private MaterialButton loginButton;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView registerLink;
    private TextView forgotPasswordText;
    private CheckBox rememberCheckbox;
    private LoginViewModel loginViewModel;
    private AuthTokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = AuthTokenManager.getInstance(this);
        
        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateToDashboard();
            return;
        }

        initViews();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        errorMessage = findViewById(R.id.errorMessage);
        registerLink = findViewById(R.id.registerLink);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        rememberCheckbox = findViewById(R.id.rememberCheckbox);
    }

    private void setupViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        
        loginViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                loginButton.setEnabled(false);
                loginButton.setText("Signing in...");
                progressBar.setVisibility(View.VISIBLE);
            } else {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
                progressBar.setVisibility(View.GONE);
            }
        });

        loginViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                errorMessage.setText(error);
                errorMessage.setVisibility(View.VISIBLE);
            } else {
                errorMessage.setVisibility(View.GONE);
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResponse -> {
            if (loginResponse != null && loginResponse.isSuccess()) {
                String token = loginResponse.getToken();
                if (token != null) {
                    tokenManager.saveToken(token);
                }
                
                LoginResponse.User user = loginResponse.getUser();
                if (user != null) {
                    tokenManager.saveUserInfo(
                        user.getName(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getPhone()
                    );
                }
                
                navigateToDashboard();
            }
        });
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> performLogin());
        
        passwordInput.setOnEditorActionListener((v, actionId, event) -> {
            performLogin();
            return true;
        });
        
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        
        forgotPasswordText.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email or username is required");
            return;
        } else {
            emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            return;
        } else {
            passwordLayout.setError(null);
        }

        loginViewModel.login(email, password);
    }

    private void showForgotPasswordDialog() {
        // Implement forgot password dialog
        // For now, show a simple message
        errorMessage.setText("Password reset link will be sent to your email");
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.postDelayed(() -> errorMessage.setVisibility(View.GONE), 3000);
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

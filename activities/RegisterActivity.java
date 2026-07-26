package com.nicargo.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nicargo.app.R;
import com.nicargo.app.models.OTPResponse;
import com.nicargo.app.models.UsernameCheckResponse;
import com.nicargo.app.viewmodels.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {
    // Step 1 Views
    private TextInputEditText emailInput;
    private TextInputLayout emailLayout;
    private MaterialButton sendOtpButton;
    private MaterialButton verifyOtpButton;
    private LinearLayout otpSection;
    private TextInputEditText[] otpInputs = new TextInputEditText[6];
    private TextView otpTimer;
    private TextView resendOtpText;
    private ConstraintLayout step1Container;
    private ConstraintLayout step2Container;
    
    // Step 2 Views
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText usernameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText stateInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private CheckBox termsCheckbox;
    private MaterialButton registerButton;
    
    // Status Views
    private TextView usernameStatus;
    private TextView strengthText;
    private TextView errorMessage;
    private ProgressBar progressBar;
    private TextView step1Indicator;
    private TextView step2Indicator;
    
    private RegisterViewModel registerViewModel;
    private Handler timerHandler = new Handler();
    private int timerSeconds = 600;
    private Runnable timerRunnable;
    private String verifiedEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupViewModel();
        setupListeners();
        setupOTPInputs();
    }

    private void initViews() {
        // Step 1
        emailInput = findViewById(R.id.emailInput);
        emailLayout = findViewById(R.id.emailLayout);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        otpSection = findViewById(R.id.otpSection);
        otpTimer = findViewById(R.id.otpTimer);
        resendOtpText = findViewById(R.id.resendOtpText);
        step1Container = findViewById(R.id.step1Container);
        step2Container = findViewById(R.id.step2Container);
        
        // OTP inputs
        otpInputs[0] = findViewById(R.id.otp1);
        otpInputs[1] = findViewById(R.id.otp2);
        otpInputs[2] = findViewById(R.id.otp3);
        otpInputs[3] = findViewById(R.id.otp4);
        otpInputs[4] = findViewById(R.id.otp5);
        otpInputs[5] = findViewById(R.id.otp6);
        
        // Step 2
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        usernameInput = findViewById(R.id.usernameInput);
        phoneInput = findViewById(R.id.phoneInput);
        stateInput = findViewById(R.id.stateInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        registerButton = findViewById(R.id.registerButton);
        
        // Status
        usernameStatus = findViewById(R.id.usernameStatus);
        strengthText = findViewById(R.id.strengthText);
        errorMessage = findViewById(R.id.errorMessage);
        progressBar = findViewById(R.id.progressBar);
        step1Indicator = findViewById(R.id.step1Indicator);
        step2Indicator = findViewById(R.id.step2Indicator);
        
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupViewModel() {
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        
        registerViewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            sendOtpButton.setEnabled(!isLoading);
            verifyOtpButton.setEnabled(!isLoading);
            registerButton.setEnabled(!isLoading);
        });
        
        registerViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
        
        registerViewModel.getOTPResponse().observe(this, response -> {
            if (response != null) {
                if (response.isSuccess()) {
                    if (response.getDebugOTP() != null) {
                        showError("Debug OTP: " + response.getDebugOTP());
                    } else {
                        showError("Verification code sent to your email");
                    }
                    otpSection.setVisibility(View.VISIBLE);
                    startTimer();
                } else {
                    showError(response.getMessage());
                }
            }
        });
        
        registerViewModel.getVerificationResult().observe(this, response -> {
            if (response != null && response.isSuccess()) {
                verifiedEmail = emailInput.getText().toString().trim();
                goToStep2();
                showError("Email verified successfully!");
            } else if (response != null) {
                showError(response.getMessage());
            }
        });
        
        registerViewModel.getUsernameCheckResult().observe(this, result -> {
            if (result != null) {
                updateUsernameStatus(result);
            }
        });
        
        registerViewModel.getRegistrationResult().observe(this, result -> {
            if (result != null && result.isSuccess()) {
                showError("Registration successful! Redirecting...");
                registerButton.postDelayed(() -> finish(), 2000);
            } else if (result != null) {
                showError(result.getMessage());
            }
        });
    }

    private void setupListeners() {
        sendOtpButton.setOnClickListener(v -> sendOTP());
        verifyOtpButton.setOnClickListener(v -> verifyOTP());
        resendOtpText.setOnClickListener(v -> sendOTP());
        
        registerButton.setOnClickListener(v -> registerUser());
        
        // Username validation with debounce
        usernameInput.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString().trim();
                if (username.length() >= 3) {
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                    runnable = () -> registerViewModel.checkUsername(username);
                    handler.postDelayed(runnable, 300);
                } else {
                    usernameStatus.setText("");
                }
            }
        });
        
        // Password strength
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updatePasswordStrength(s.toString());
            }
        });
        
        // Confirm password
        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordInput.getText().toString();
                if (!s.toString().equals(password) && s.length() > 0) {
                    confirmPasswordInput.setError("Passwords do not match");
                } else {
                    confirmPasswordInput.setError(null);
                }
            }
        });
    }

    private void setupOTPInputs() {
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }
                
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void sendOTP() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email address");
            return;
        }
        emailLayout.setError(null);
        registerViewModel.sendOTP(email);
    }

    private void verifyOTP() {
        StringBuilder otp = new StringBuilder();
        for (TextInputEditText input : otpInputs) {
            String digit = input.getText().toString();
            if (digit.isEmpty()) {
                showError("Please enter the full 6-digit code");
                return;
            }
            otp.append(digit);
        }
        String email = emailInput.getText().toString().trim();
        registerViewModel.verifyOTP(email, otp.toString());
    }

    private void startTimer() {
        timerSeconds = 600;
        updateTimerDisplay();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timerSeconds--;
                updateTimerDisplay();
                if (timerSeconds > 0) {
                    timerHandler.postDelayed(this, 1000);
                } else {
                    otpTimer.setText("Code expired. Please request a new one.");
                    resendOtpText.setVisibility(View.VISIBLE);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
        resendOtpText.setVisibility(View.GONE);
    }

    private void updateTimerDisplay() {
        int minutes = timerSeconds / 60;
        int seconds = timerSeconds % 60;
        otpTimer.setText(String.format("Code expires in: %02d:%02d", minutes, seconds));
    }

    private void goToStep2() {
        step1Container.setVisibility(View.GONE);
        step2Container.setVisibility(View.VISIBLE);
        step1Indicator.setBackgroundResource(R.drawable.step_indicator_completed);
        step1Indicator.setTextColor(getColor(R.color.white));
        step2Indicator.setBackgroundResource(R.drawable.step_indicator_active);
        step2Indicator.setTextColor(getColor(R.color.white));
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void updateUsernameStatus(UsernameCheckResponse result) {
        String username = usernameInput.getText().toString().trim();
        if (result.isAvailable()) {
            usernameStatus.setText("✓ Username available");
            usernameStatus.setTextColor(getColor(R.color.success_green));
        } else {
            usernameStatus.setText("✗ " + result.getMessage());
            usernameStatus.setTextColor(getColor(R.color.error_red));
        }
    }

    private void updatePasswordStrength(String password) {
        if (password.isEmpty()) {
            strengthText.setText("Password strength: None");
            return;
        }
        
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[^A-Za-z0-9].*")) score++;
        
        String strength;
        if (score <= 2) strength = "Weak";
        else if (score <= 3) strength = "Fair";
        else if (score <= 4) strength = "Good";
        else strength = "Strong";
        
        strengthText.setText("Password strength: " + strength);
    }

    private void registerUser() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String state = stateInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        
        // Basic validation
        if (firstName.isEmpty() || firstName.length() < 2) {
            showError("First name must be at least 2 characters");
            return;
        }
        
        if (lastName.isEmpty() || lastName.length() < 2) {
            showError("Last name must be at least 2 characters");
            return;
        }
        
        if (username.isEmpty() || username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }
        
        if (phone.isEmpty() || phone.length() < 7) {
            showError("Please enter a valid phone number");
            return;
        }
        
        if (state.isEmpty()) {
            showError("Please select your state");
            return;
        }
        
        if (password.isEmpty() || password.length() < 8) {
            showError("Password must be at least 8 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        if (!termsCheckbox.isChecked()) {
            showError("You must agree to the terms and conditions");
            return;
        }
        
        registerViewModel.register(
            verifiedEmail,
            firstName,
            lastName,
            username,
            phone,
            state,
            password,
            "+234", // Default country code
            "Nigeria" // Default country name
        );
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.postDelayed(() -> errorMessage.setVisibility(View.GONE), 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }
}

package com.app.pccooker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput, mobileInput, otpInput;
    private Button loginButton, googleSignInButton, sendOtpButton, verifyOtpButton;
    private Button emailTabButton, mobileTabButton;
    private LinearLayout emailLoginSection, mobileLoginSection;
    private TextView registerLink, forgotPasswordLink;
    
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeFirebase();
        setupClickListeners();
        setupPhoneAuthCallbacks();
    }

    private void initializeViews() {
        // Input fields
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        mobileInput = findViewById(R.id.mobileInput);
        otpInput = findViewById(R.id.otpInput);
        
        // Buttons
        loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        emailTabButton = findViewById(R.id.emailTabButton);
        mobileTabButton = findViewById(R.id.mobileTabButton);
        
        // Layouts
        emailLoginSection = findViewById(R.id.emailLoginSection);
        mobileLoginSection = findViewById(R.id.mobileLoginSection);
        
        // Links
        registerLink = findViewById(R.id.registerLink);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupClickListeners() {
        // Tab switching
        emailTabButton.setOnClickListener(v -> switchToEmailTab());
        mobileTabButton.setOnClickListener(v -> switchToMobileTab());
        
        // Email login
        loginButton.setOnClickListener(v -> handleEmailLogin());
        googleSignInButton.setOnClickListener(v -> handleGoogleLogin());
        
        // Mobile OTP
        sendOtpButton.setOnClickListener(v -> handleSendOtp());
        verifyOtpButton.setOnClickListener(v -> handleVerifyOtp());
        
        // Links
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
        
        forgotPasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
        
        // Removed: guestLoginLink.setOnClickListener(v -> {
        //     // Skip login and go directly to AI Assistant
        //     startActivity(new Intent(LoginActivity.this, AIAssistantActivity.class));
        //     finish();
        // });
    }

    private void setupPhoneAuthCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Auto-verification completed
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(com.google.firebase.FirebaseException e) {
                Toast.makeText(LoginActivity.this, "OTP verification failed: " + e.getMessage(), 
                             Toast.LENGTH_LONG).show();
                sendOtpButton.setEnabled(true);
                sendOtpButton.setText("Send OTP");
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                Toast.makeText(LoginActivity.this, "OTP sent to your mobile number", 
                             Toast.LENGTH_SHORT).show();
                
                // Show OTP input
                otpInput.setVisibility(View.VISIBLE);
                verifyOtpButton.setVisibility(View.VISIBLE);
                sendOtpButton.setText("Resend OTP");
                sendOtpButton.setEnabled(true);
            }
        };
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private boolean isValidIndianMobile(String mobile) {
        // Remove +91 if present
        if (mobile.startsWith("+91")) {
            mobile = mobile.substring(3);
        }
        // Remove spaces and dashes
        mobile = mobile.replaceAll("[\\s-]", "");
        // Check length and starting digit
        return mobile.matches("^[6-9][0-9]{9}$");
    }

    private void switchToEmailTab() {
        emailLoginSection.setVisibility(View.VISIBLE);
        mobileLoginSection.setVisibility(View.GONE);
        emailTabButton.setBackgroundResource(R.drawable.tab_left_selector_blue);
        mobileTabButton.setBackgroundResource(R.drawable.tab_right_selector_blue);
    }

    private void switchToMobileTab() {
        emailLoginSection.setVisibility(View.GONE);
        mobileLoginSection.setVisibility(View.VISIBLE);
        mobileTabButton.setBackgroundResource(R.drawable.tab_right_selector_blue);
        emailTabButton.setBackgroundResource(R.drawable.tab_left_selector_blue);
    }

    private void handleEmailLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login with Email");
                    
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login successful!", 
                                     Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + 
                                     task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleGoogleLogin() {
        try {
            googleSignInButton.setEnabled(false);
            googleSignInButton.setText("Connecting...");
            
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            googleSignInButton.setEnabled(true);
            googleSignInButton.setText("Login with Google");
            Toast.makeText(this, "Google Sign-In not available. Please try email login.", Toast.LENGTH_LONG).show();
        }
    }

    private void handleSendOtp() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection. Please check your network and try again.", Toast.LENGTH_LONG).show();
            return;
        }
        String mobile = mobileInput.getText().toString().trim();
        if (!isValidIndianMobile(mobile)) {
            Toast.makeText(this, "Please enter a valid Indian mobile number (10 digits, starts with 6-9)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mobile.startsWith("+91")) {
            mobile = "+91" + mobile;
        }
        sendOtpButton.setEnabled(false);
        sendOtpButton.setText("Sending OTP...");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        sendOtpButton.setEnabled(true);
                        sendOtpButton.setText("Send OTP");
                        Toast.makeText(LoginActivity.this, "OTP auto-verified!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        sendOtpButton.setEnabled(true);
                        sendOtpButton.setText("Send OTP");
                        String msg = "OTP failed. Please try again.";
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            msg = "Invalid phone number format. Please enter a valid Indian mobile number.";
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            msg = "SMS quota exceeded. Please try again later.";
                        } else if (e.getMessage() != null && e.getMessage().toLowerCase().contains("network")) {
                            msg = "Network error. Please check your connection.";
                        } else if (e.getMessage() != null && e.getMessage().toLowerCase().contains("internal error")) {
                            msg = "Internal server error. Please check your Firebase setup (SHA-1/SHA-256, Phone Auth enabled, Google Play Services, real device).";
                        } else if (e.getMessage() != null && e.getMessage().toLowerCase().contains("app not authorized")) {
                            msg = "App not authorized. Please add your app's SHA-1 and SHA-256 fingerprints to Firebase Console.";
                        } else if (e.getMessage() != null && e.getMessage().toLowerCase().contains("missing api key")) {
                            msg = "Missing API key. Please check your google-services.json and Firebase project settings.";
                        } else if (e.getMessage() != null) {
                            msg = e.getMessage();
                        }
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        sendOtpButton.setEnabled(true);
                        sendOtpButton.setText("Send OTP");
                        otpInput.setVisibility(View.VISIBLE);
                        verifyOtpButton.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "OTP sent! Please check your SMS.", Toast.LENGTH_SHORT).show();
                        // Save verificationId/token for later use
                        LoginActivity.this.mVerificationId = verificationId;
                        LoginActivity.this.resendToken = token;
                    }
                });
    }

    private void handleVerifyOtp() {
        String otp = otpInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(otp) || otp.length() != 6) {
            Toast.makeText(this, "Please enter 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        
        verifyOtpButton.setEnabled(false);
        verifyOtpButton.setText("Verifying...");
        
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    verifyOtpButton.setEnabled(true);
                    verifyOtpButton.setText("Verify OTP");
                    
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login successful!", 
                                     Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "OTP verification failed: " + 
                                     task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                googleSignInButton.setEnabled(true);
                googleSignInButton.setText("Login with Google");
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), 
                             Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    googleSignInButton.setEnabled(true);
                    googleSignInButton.setText("Login with Google");
                    
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Google login successful!", 
                                     Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Google authentication failed: " + 
                                     task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        // Commented out for development - uncomment when ready for production
        /*
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, AIAssistantActivity.class));
            finish();
        }
        */
    }
}
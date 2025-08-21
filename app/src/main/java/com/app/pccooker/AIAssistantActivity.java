package com.app.pccooker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.app.pccooker.utils.AIPromptParser;
import com.app.pccooker.utils.AIPromptParser.Requirements;
import com.app.pccooker.models.ComponentModel;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.regex.Pattern;

public class AIAssistantActivity extends AppCompatActivity {
    private static final int VOICE_REQUEST_CODE = 101;
    private EditText aiPromptInput;
    private ImageButton aiMicButton, aiCameraButton;
    private Button aiBuildButton;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_ai_assistant);

        try {
            initializeViews();
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing AI Assistant", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        aiPromptInput = findViewById(R.id.aiPromptInput);
        aiMicButton = findViewById(R.id.aiMicButton);
        aiCameraButton = findViewById(R.id.aiCameraButton);
        aiBuildButton = findViewById(R.id.aiBuildButton);
    }

    private void setupClickListeners() {
        aiMicButton.setOnClickListener(v -> startVoiceInput());
        aiCameraButton.setOnClickListener(v -> {
            Toast.makeText(this, "Image input coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        aiBuildButton.setOnClickListener(v -> processAIPrompt());
    }

    private void processAIPrompt() {
        if (isProcessing) {
            Toast.makeText(this, "Please wait, processing your request...", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String prompt = aiPromptInput.getText().toString().trim();
            if (prompt.isEmpty()) {
                Toast.makeText(this, "Please enter your requirements!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            System.out.println("Processing prompt: " + prompt);
            
            // Enhanced prompt parsing with fallback
            Requirements req = parsePromptWithFallback(prompt);
            System.out.println("Parsed requirements - Use case: " + req.useCase + ", Budget: " + req.budget);
            
            if (!isRelevantPrompt(req)) {
                Toast.makeText(this, "Sorry, I can only help you build your perfect PC. Please describe your PC requirements!", Toast.LENGTH_LONG).show();
                return;
            }
            
            double budget = req.budget > 0 ? req.budget : 50000; // Default budget if not specified
            String useCase = req.useCase != null ? req.useCase : "general";
            
            System.out.println("Starting AI component selection - Budget: " + budget + ", Use case: " + useCase);
            
            // Show loading message and disable button
            isProcessing = true;
            aiBuildButton.setEnabled(false);
            aiBuildButton.setText("Processing...");
            Toast.makeText(this, "AI is selecting the best components for you...", Toast.LENGTH_SHORT).show();
            
            // Add timeout for Firebase operations
            new android.os.Handler().postDelayed(() -> {
                if (isProcessing) {
                    isProcessing = false;
                    aiBuildButton.setEnabled(true);
                    aiBuildButton.setText("Let's Build");
                    Toast.makeText(AIAssistantActivity.this, "Request timed out. Please try again.", Toast.LENGTH_LONG).show();
                }
            }, 30000); // 30 second timeout
            
            ComponentManager.getInstance(this).autoSelectComponents(budget, useCase, new ComponentManager.OnAutoSelectCallback() {
                @Override
                public void onSuccess(List<ComponentModel> components) {
                    runOnUiThread(() -> {
                        isProcessing = false;
                        aiBuildButton.setEnabled(true);
                        aiBuildButton.setText("Let's Build");
                        
                        if (components == null || components.isEmpty()) {
                            Toast.makeText(AIAssistantActivity.this, "No suitable components found for your requirements. Please try a different budget or requirements.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        
                        try {
                            Intent intent = new Intent(AIAssistantActivity.this, BuildSummaryActivity.class);
                            intent.putExtra("selected_components", new ArrayList<>(components));
                            intent.putExtra("ai_use_case", useCase);
                            intent.putExtra("ai_budget", budget);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AIAssistantActivity.this, "Error opening build summary. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                
                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        isProcessing = false;
                        aiBuildButton.setEnabled(true);
                        aiBuildButton.setText("Let's Build");
                        
                        // Provide helpful error messages
                        String userFriendlyMessage = getFriendlyErrorMessage(message);
                        Toast.makeText(AIAssistantActivity.this, userFriendlyMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            isProcessing = false;
            aiBuildButton.setEnabled(true);
            aiBuildButton.setText("Let's Build");
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private Requirements parsePromptWithFallback(String prompt) {
        try {
            return AIPromptParser.parsePrompt(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback parsing for basic prompts
            Requirements fallback = new Requirements();
            fallback.useCase = "general";
            fallback.budget = 50000;
            fallback.language = "en";
            
            String lower = prompt.toLowerCase();
            
            // Enhanced budget detection for fallback
            Pattern[] patterns = {
                Pattern.compile("(?:₹|rs\\.?|rupees|budget|under|within)[\\s]*([0-9,]+)"),
                Pattern.compile("([0-9,]+)[\\s]*(?:₹|rs\\.?|rupees|thousand|k|lakh)"),
                Pattern.compile("([0-9]+)(?:thousand|k)")
            };
            
            for (Pattern p : patterns) {
                java.util.regex.Matcher m = p.matcher(lower);
                if (m.find()) {
                    try {
                        String num = m.group(1).replace(",", "");
                        int parsedBudget = Integer.parseInt(num);
                        
                        // Handle thousands
                        if (lower.contains("thousand") || lower.contains("k")) {
                            if (parsedBudget < 1000) parsedBudget *= 1000;
                        } else if (lower.contains("lakh")) {
                            parsedBudget *= 100000;
                        }
                        
                        fallback.budget = parsedBudget;
                        break;
                    } catch (Exception ignored) {}
                }
            }
            
            // Basic use case detection
            if (lower.contains("gaming") || lower.contains("game")) {
                fallback.useCase = "gaming";
            } else if (lower.contains("editing") || lower.contains("edit")) {
                fallback.useCase = "editing";
            } else if (lower.contains("work") || lower.contains("office")) {
                fallback.useCase = "work";
            }
            
            return fallback;
        }
    }

    private String getFriendlyErrorMessage(String technicalMessage) {
        if (technicalMessage.contains("network") || technicalMessage.contains("connection")) {
            return "Network connection issue. Please check your internet and try again.";
        } else if (technicalMessage.contains("timeout")) {
            return "Request timed out. Please try again.";
        } else if (technicalMessage.contains("permission")) {
            return "Permission denied. Please check app permissions.";
        } else if (technicalMessage.contains("firebase")) {
            return "Database connection issue. Please try again later.";
        } else {
            return "Something went wrong. Please try again.";
        }
    }

    private boolean isRelevantPrompt(Requirements req) {
        try {
            // More flexible prompt validation
            if (req == null) return false;
            
            // Allow any use case that's not empty
            if (req.useCase == null || req.useCase.isEmpty()) {
                req.useCase = "general";
            }
            
            // Check if it's a PC-related prompt
            String prompt = aiPromptInput.getText().toString().toLowerCase();
            return prompt.contains("pc") || prompt.contains("computer") || prompt.contains("gaming") || 
                   prompt.contains("editing") || prompt.contains("work") || prompt.contains("student") ||
                   prompt.contains("build") || prompt.contains("components") || prompt.contains("parts");
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Default to allowing the prompt
        }
    }

    private void startVoiceInput() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "te-IN,en-IN");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your PC requirements");
            startActivityForResult(intent, VOICE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Voice input not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == VOICE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null && !results.isEmpty()) {
                    aiPromptInput.setText(results.get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing voice input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isProcessing = false;
    }
} 
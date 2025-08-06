package com.app.pccooker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.pccooker.utils.AdvancedAIPromptParser;
import com.app.pccooker.utils.AdvancedAIPromptParser.AdvancedRequirements;
import com.app.pccooker.utils.AdvancedAIPromptParser.ConversationContext;
import com.app.pccooker.adapters.AIConversationAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EnhancedAIAssistantActivity extends AppCompatActivity {
    private static final int VOICE_REQUEST_CODE = 101;
    
    private EditText aiPromptInput;
    private ImageButton aiMicButton, aiCameraButton, aiHistoryButton, aiClearButton;
    private Button aiBuildButton, aiSuggestButton, aiHelpButton;
    private TextView aiStatusText, aiContextText;
    private RecyclerView aiConversationRecyclerView;
    private AIConversationAdapter conversationAdapter;
    
    private boolean isProcessing = false;
    private String currentUserId;
    private ConversationContext currentContext;
    private List<ConversationMessage> conversationHistory = new ArrayList<>();

    public static class ConversationMessage {
        public String message;
        public boolean isUser;
        public long timestamp;
        public String responseType; // "build", "suggestion", "help", "error"

        public ConversationMessage(String message, boolean isUser, String responseType) {
            this.message = message;
            this.isUser = isUser;
            this.timestamp = System.currentTimeMillis();
            this.responseType = responseType;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_enhanced_ai_assistant);

        try {
            initializeViews();
            setupClickListeners();
            initializeAI();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing Enhanced AI Assistant", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        aiPromptInput = findViewById(R.id.aiPromptInput);
        aiMicButton = findViewById(R.id.aiMicButton);
        aiCameraButton = findViewById(R.id.aiCameraButton);
        aiHistoryButton = findViewById(R.id.aiHistoryButton);
        aiClearButton = findViewById(R.id.aiClearButton);
        aiBuildButton = findViewById(R.id.aiBuildButton);
        aiSuggestButton = findViewById(R.id.aiSuggestButton);
        aiHelpButton = findViewById(R.id.aiHelpButton);
        aiStatusText = findViewById(R.id.aiStatusText);
        aiContextText = findViewById(R.id.aiContextText);
        aiConversationRecyclerView = findViewById(R.id.aiConversationRecyclerView);
        
        // Setup RecyclerView for conversation history
        conversationAdapter = new AIConversationAdapter(conversationHistory);
        aiConversationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        aiConversationRecyclerView.setAdapter(conversationAdapter);
    }

    private void setupClickListeners() {
        aiMicButton.setOnClickListener(v -> startVoiceInput());
        aiCameraButton.setOnClickListener(v -> {
            Toast.makeText(this, "Image input coming soon!", Toast.LENGTH_SHORT).show();
        });
        aiHistoryButton.setOnClickListener(v -> showConversationHistory());
        aiClearButton.setOnClickListener(v -> clearConversation());
        aiBuildButton.setOnClickListener(v -> processAIPrompt("build"));
        aiSuggestButton.setOnClickListener(v -> processAIPrompt("suggest"));
        aiHelpButton.setOnClickListener(v -> showAIHelp());
    }

    private void initializeAI() {
        // Generate unique user ID for this session
        currentUserId = UUID.randomUUID().toString();
        currentContext = AdvancedAIPromptParser.getConversationContext(currentUserId);
        
        // Add welcome message
        addConversationMessage("Hello! I'm your AI PC Builder Assistant. I can help you build the perfect PC based on your requirements. Just tell me what you need!", false, "help");
        
        updateStatus("Ready to help you build your PC!");
        updateContext("New session started");
    }

    private void processAIPrompt(String action) {
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
            
            // Add user message to conversation
            addConversationMessage(prompt, true, action);
            
            // Clear input
            aiPromptInput.setText("");
            
            // Show processing state
            isProcessing = true;
            updateStatus("AI is analyzing your requirements...");
            setButtonsEnabled(false);
            
                    // Enhanced prompt parsing
        AdvancedRequirements req = AdvancedAIPromptParser.parseAdvancedPrompt(prompt, currentUserId);
            
            // Add AI response based on action
            String aiResponse = generateAIResponse(req, action);
            addConversationMessage(aiResponse, false, action);
            
            // If building, proceed with component selection
            if (action.equals("build")) {
                proceedWithBuild(req);
            } else if (action.equals("suggest")) {
                showSuggestions(req);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            addConversationMessage("Sorry, I encountered an error. Please try again.", false, "error");
            updateStatus("Error occurred");
        } finally {
            isProcessing = false;
            setButtonsEnabled(true);
        }
    }

    private String generateAIResponse(AdvancedRequirements req, String action) {
        StringBuilder response = new StringBuilder();
        
        if (action.equals("build")) {
            response.append("🎯 **Building your PC with these specifications:**\n\n");
            response.append("📋 **Use Case:** ").append(capitalizeFirst(req.useCase)).append("\n");
            response.append("💰 **Budget:** ₹").append(String.format("%,d", req.budget)).append("\n");
            response.append("⚡ **Priority:** ").append(capitalizeFirst(req.priority)).append("\n");
            
            if (!req.specificComponents.isEmpty()) {
                response.append("🔧 **Components mentioned:** ").append(String.join(", ", req.specificComponents)).append("\n");
            }
            
            if (!req.brandPreference.equals("any")) {
                response.append("🏷️ **Brand preference:** ").append(capitalizeFirst(req.brandPreference)).append("\n");
            }
            
            response.append("\n🚀 **Starting component selection...**");
            
        } else if (action.equals("suggest")) {
            response.append("💡 **Here are my suggestions for your PC build:**\n\n");
            response.append("🎮 **For ").append(req.useCase).append(" use case:**\n");
            
            switch (req.useCase) {
                case "gaming":
                    response.append("• Focus on GPU and CPU performance\n");
                    response.append("• Consider 16GB+ RAM for modern games\n");
                    response.append("• SSD for faster game loading\n");
                    response.append("• Good cooling for sustained performance\n");
                    break;
                case "editing":
                    response.append("• High-performance CPU (Intel i7/AMD Ryzen 7+)\n");
                    response.append("• 32GB+ RAM for video editing\n");
                    response.append("• Fast NVMe SSD for project files\n");
                    response.append("• Professional GPU for rendering\n");
                    break;
                case "work":
                    response.append("• Reliable CPU with good multitasking\n");
                    response.append("• 16GB RAM for productivity\n");
                    response.append("• Fast SSD for quick boot times\n");
                    response.append("• Quiet operation for office environment\n");
                    break;
                case "student":
                    response.append("• Budget-friendly but capable CPU\n");
                    response.append("• 8GB+ RAM for study applications\n");
                    response.append("• SSD for fast system performance\n");
                    response.append("• Reliable components for long-term use\n");
                    break;
                default:
                    response.append("• Balanced performance for general use\n");
                    response.append("• 8GB+ RAM for smooth operation\n");
                    response.append("• SSD for better user experience\n");
                    response.append("• Reliable components\n");
            }
            
            response.append("\n💰 **Budget optimization tips:**\n");
            if (req.budget < 50000) {
                response.append("• Consider used/refurbished components\n");
                response.append("• Focus on essential components first\n");
                response.append("• Plan for future upgrades\n");
            } else if (req.budget < 100000) {
                response.append("• Good balance of performance and value\n");
                response.append("• Consider mid-range components\n");
                response.append("• Room for some premium features\n");
            } else {
                response.append("• Premium components available\n");
                response.append("• Focus on high-end performance\n");
                response.append("• Consider future-proofing\n");
            }
        }
        
        return response.toString();
    }

    private void proceedWithBuild(AdvancedRequirements req) {
        updateStatus("Selecting components for your build...");
        
        // Add timeout for Firebase operations
        new android.os.Handler().postDelayed(() -> {
            if (isProcessing) {
                isProcessing = false;
                setButtonsEnabled(true);
                addConversationMessage("Request timed out. Please try again.", false, "error");
                updateStatus("Request timed out");
            }
        }, 30000);
        
        ComponentManager.getInstance(this).autoSelectComponents(req.budget, req.useCase, new ComponentManager.OnAutoSelectCallback() {
            @Override
            public void onSuccess(List<ComponentModel> components) {
                runOnUiThread(() -> {
                    isProcessing = false;
                    setButtonsEnabled(true);
                    
                    if (components == null || components.isEmpty()) {
                        addConversationMessage("No suitable components found for your requirements. Please try a different budget or requirements.", false, "error");
                        updateStatus("No components found");
                        return;
                    }
                    
                    // Add success message
                    addConversationMessage("✅ **Build completed!** Found " + components.size() + " components for your requirements.", false, "build");
                    
                    try {
                        Intent intent = new Intent(EnhancedAIAssistantActivity.this, BuildSummaryActivity.class);
                        intent.putExtra("selected_components", new ArrayList<>(components));
                        intent.putExtra("ai_use_case", req.useCase);
                        intent.putExtra("ai_budget", req.budget);
                        intent.putExtra("ai_priority", req.priority);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        addConversationMessage("Error opening build summary. Please try again.", false, "error");
                    }
                });
            }
            
            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    isProcessing = false;
                    setButtonsEnabled(true);
                    
                    String userFriendlyMessage = getFriendlyErrorMessage(message);
                    addConversationMessage(userFriendlyMessage, false, "error");
                    updateStatus("Error occurred");
                });
            }
        });
    }

    private void showSuggestions(AdvancedRequirements req) {
        updateStatus("Suggestions provided");
        // Suggestions are already shown in the AI response
    }

    private void showAIHelp() {
        String helpMessage = "🤖 **AI Assistant Help:**\n\n" +
            "**Commands you can use:**\n" +
            "• \"Build me a gaming PC under ₹80,000\"\n" +
            "• \"I need a PC for video editing around ₹1,20,000\"\n" +
            "• \"Suggest components for a student PC\"\n" +
            "• \"Help me build a work PC\"\n\n" +
            "**Features:**\n" +
            "• 🎤 Voice input support\n" +
            "• 💬 Conversation memory\n" +
            "• 🎯 Smart requirement parsing\n" +
            "• 💡 Intelligent suggestions\n" +
            "• 🔧 Component recommendations\n\n" +
            "**Tips:**\n" +
            "• Be specific about your budget\n" +
            "• Mention your primary use case\n" +
            "• Include any brand preferences\n" +
            "• Ask for suggestions if unsure";
        
        addConversationMessage(helpMessage, false, "help");
        updateStatus("Help provided");
    }

    private void showConversationHistory() {
        if (conversationHistory.isEmpty()) {
            Toast.makeText(this, "No conversation history yet", Toast.LENGTH_SHORT).show();
            return;
        }
        
        StringBuilder history = new StringBuilder("📜 **Conversation History:**\n\n");
        for (ConversationMessage msg : conversationHistory) {
            String prefix = msg.isUser ? "👤 You: " : "🤖 AI: ";
            history.append(prefix).append(msg.message).append("\n\n");
        }
        
        addConversationMessage(history.toString(), false, "help");
        updateStatus("History displayed");
    }

    private void clearConversation() {
        conversationHistory.clear();
        conversationAdapter.notifyDataSetChanged();
        AdvancedAIPromptParser.clearConversationContext(currentUserId);
        currentContext = AdvancedAIPromptParser.getConversationContext(currentUserId);
        
        addConversationMessage("Conversation cleared. How can I help you build your PC?", false, "help");
        updateStatus("Conversation cleared");
        updateContext("New conversation started");
    }

    private void addConversationMessage(String message, boolean isUser, String responseType) {
        ConversationMessage msg = new ConversationMessage(message, isUser, responseType);
        conversationHistory.add(msg);
        conversationAdapter.notifyItemInserted(conversationHistory.size() - 1);
        aiConversationRecyclerView.smoothScrollToPosition(conversationHistory.size() - 1);
    }

    private void updateStatus(String status) {
        aiStatusText.setText("Status: " + status);
    }

    private void updateContext(String context) {
        aiContextText.setText("Context: " + context);
    }

    private void setButtonsEnabled(boolean enabled) {
        aiBuildButton.setEnabled(enabled);
        aiSuggestButton.setEnabled(enabled);
        aiHelpButton.setEnabled(enabled);
        aiMicButton.setEnabled(enabled);
        aiCameraButton.setEnabled(enabled);
        aiHistoryButton.setEnabled(enabled);
        aiClearButton.setEnabled(enabled);
        aiPromptInput.setEnabled(enabled);
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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
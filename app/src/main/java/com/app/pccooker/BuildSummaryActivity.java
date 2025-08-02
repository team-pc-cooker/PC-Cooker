package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.app.pccooker.ComponentModel;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

public class BuildSummaryActivity extends AppCompatActivity {
    private LinearLayout summaryContainer;
    private Button confirmButton;
    private List<ComponentModel> selectedComponents;
    private String useCase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Premium);
        setContentView(R.layout.activity_build_summary);

        summaryContainer = findViewById(R.id.summaryContainer);
        confirmButton = findViewById(R.id.confirmBuildButton);

        selectedComponents = (ArrayList<ComponentModel>) getIntent().getSerializableExtra("selected_components");
        if (selectedComponents == null || selectedComponents.isEmpty()) {
            Toast.makeText(this, "No components selected!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        useCase = getIntent().getStringExtra("ai_use_case");
        showSummary(selectedComponents);

        confirmButton.setOnClickListener(v -> {
            try {
                // Add components to cart using CartManager
                CartManager cartManager = CartManager.getInstance(this);
                for (ComponentModel component : selectedComponents) {
                    cartManager.addToCart(component);
                }
                
                Toast.makeText(this, "Components added to cart successfully!", Toast.LENGTH_SHORT).show();
                
                // Navigate to MainActivity with cart flag
                Intent intent = new Intent(BuildSummaryActivity.this, MainActivity.class);
                intent.putExtra("from_ai_summary", true);
                intent.putExtra("show_cart", true);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error adding components to cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSummary(List<ComponentModel> components) {
        summaryContainer.removeAllViews();
        double totalPrice = 0;
        
        for (ComponentModel component : components) {
            // Create card layout for each component
            androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 8, 0, 8);
            card.setLayoutParams(cardParams);
            card.setRadius(12f);
            card.setCardElevation(4f);
            card.setCardBackgroundColor(0xFFFFFFFF);
            
            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);
            cardContent.setPadding(16, 16, 16, 16);
            
            // Component name and price
            TextView titleTv = new TextView(this);
            titleTv.setText(component.getCategory());
            titleTv.setTextColor(getResources().getColor(R.color.text_secondary));
            titleTv.setTextSize(12f);
            titleTv.setAllCaps(true);
            cardContent.addView(titleTv);
            
            TextView nameTv = new TextView(this);
            nameTv.setText(component.getName());
            nameTv.setTextColor(getResources().getColor(R.color.text_primary));
            nameTv.setTextSize(16f);
            nameTv.setTypeface(null, android.graphics.Typeface.BOLD);
            cardContent.addView(nameTv);
            
            TextView priceTv = new TextView(this);
            priceTv.setText("₹" + String.format("%.0f", component.getPrice()));
            priceTv.setTextColor(getResources().getColor(R.color.sky_blue_dark));
            priceTv.setTextSize(18f);
            priceTv.setTypeface(null, android.graphics.Typeface.BOLD);
            cardContent.addView(priceTv);
            
            // AI explanation
            String explanation = ComponentManager.getInstance(this).getAIChoiceExplanation(component.getCategory(), useCase);
            TextView explainTv = new TextView(this);
            explainTv.setText(explanation);
            explainTv.setTextColor(getResources().getColor(R.color.text_secondary));
            explainTv.setTextSize(14f);
            LinearLayout.LayoutParams explainParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            explainParams.setMargins(0, 8, 0, 12);
            explainTv.setLayoutParams(explainParams);
            cardContent.addView(explainTv);
            
            // Edit button
            Button editBtn = new Button(this);
            editBtn.setText("Change Selection");
            editBtn.setTextSize(14f);
            editBtn.setBackgroundColor(getResources().getColor(R.color.sky_blue_primary));
            editBtn.setTextColor(0xFFFFFFFF);
            editBtn.setOnClickListener(v -> showAlternativesDialog(component));
            cardContent.addView(editBtn);
            
            card.addView(cardContent);
            summaryContainer.addView(card);
            
            totalPrice += component.getPrice();
        }
        
        // Add total price card
        androidx.cardview.widget.CardView totalCard = new androidx.cardview.widget.CardView(this);
        LinearLayout.LayoutParams totalCardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        totalCardParams.setMargins(0, 16, 0, 8);
        totalCard.setLayoutParams(totalCardParams);
        totalCard.setRadius(12f);
        totalCard.setCardElevation(8f);
        totalCard.setCardBackgroundColor(getResources().getColor(R.color.sky_blue_primary));
        
        LinearLayout totalContent = new LinearLayout(this);
        totalContent.setOrientation(LinearLayout.HORIZONTAL);
        totalContent.setPadding(20, 20, 20, 20);
        totalContent.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        TextView totalLabel = new TextView(this);
        totalLabel.setText("Total Build Cost:");
        totalLabel.setTextColor(0xFFFFFFFF);
        totalLabel.setTextSize(18f);
        totalLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        totalContent.addView(totalLabel);
        
        TextView totalAmount = new TextView(this);
        totalAmount.setText("₹" + String.format("%.0f", totalPrice));
        totalAmount.setTextColor(0xFFFFFFFF);
        totalAmount.setTextSize(24f);
        totalAmount.setTypeface(null, android.graphics.Typeface.BOLD);
        totalContent.addView(totalAmount);
        
        totalCard.addView(totalContent);
        summaryContainer.addView(totalCard);
    }

    private void showAlternativesDialog(ComponentModel currentComponent) {
        double budget = currentComponent.getPrice() * 1.2; // Show alternatives up to 20% more expensive
        ComponentManager.getInstance(this).fetchAlternatives(currentComponent.getCategory(), budget, new ComponentManager.OnSearchCallback() {
            @Override
            public void onSuccess(List<ComponentModel> alternatives) {
                runOnUiThread(() -> {
                    if (alternatives.isEmpty()) {
                        Toast.makeText(BuildSummaryActivity.this, "No alternatives found.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BuildSummaryActivity.this, android.R.layout.simple_list_item_1);
                    for (ComponentModel alt : alternatives) {
                        adapter.add(alt.getName() + " (₹" + alt.getPrice() + ")");
                    }
                    new AlertDialog.Builder(BuildSummaryActivity.this)
                        .setTitle("Choose Alternative for " + currentComponent.getCategory())
                        .setAdapter(adapter, (dialog, which) -> {
                            ComponentModel selected = alternatives.get(which);
                            // Replace in summary
                            for (int i = 0; i < selectedComponents.size(); i++) {
                                if (selectedComponents.get(i).getCategory().equals(currentComponent.getCategory())) {
                                    selectedComponents.set(i, selected);
                                    break;
                                }
                            }
                            showSummary(selectedComponents);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(BuildSummaryActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }
} 
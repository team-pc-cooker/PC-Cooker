package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class BuildPCFragment extends Fragment {

    private RecyclerView categoryRecyclerView;
    private EditText budgetInput;
    private Button autoSelectButton;
    private TextView budgetText;

    private final List<String> categories = Arrays.asList(
            "PROCESSOR", "GRAPHIC CARD", "RAM", "MOTHERBOARD", "STORAGE", "POWER SUPPLY", "CABINET", "COOLING", "MONITOR", "KEYBOARD", "MOUSE"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_build_pc, container, false);
        
        // Initialize views
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        budgetInput = view.findViewById(R.id.budgetInput);
        autoSelectButton = view.findViewById(R.id.autoSelectButton);
        budgetText = view.findViewById(R.id.budgetText);
        
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Setup auto-select button
        autoSelectButton.setOnClickListener(v -> {
            String budgetStr = budgetInput.getText().toString().trim();
            if (budgetStr.isEmpty()) {
                budgetInput.setError("Please enter your budget");
                return;
            }
            
            try {
                int budget = Integer.parseInt(budgetStr);
                if (budget < 20000) {
                    Toast.makeText(getContext(), "Minimum budget should be ₹20,000", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Auto-select components based on budget
                autoSelectComponents(budget);
                
            } catch (NumberFormatException e) {
                budgetInput.setError("Please enter a valid amount");
            }
        });

        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            String budgetStr = budgetInput.getText().toString().trim();
            double maxBudget = budgetStr.isEmpty() ? 50000 : Double.parseDouble(budgetStr);
            ComponentSelectionFragment fragment = ComponentSelectionFragment.newInstance(category, maxBudget);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        categoryRecyclerView.setAdapter(adapter);

        return view;
    }
    
    private void autoSelectComponents(int budget) {
        ComponentManager componentManager = ComponentManager.getInstance(requireContext());
        
        componentManager.autoSelectComponents(budget, new ComponentManager.OnAutoSelectCallback() {
            @Override
            public void onSuccess(List<ComponentModel> components) {
                // Add components to cart
                for (ComponentModel component : components) {
                    componentManager.selectComponent(component.getCategory(), component);
                }
                
                // Navigate to cart
                requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CartFragment())
                    .addToBackStack(null)
                    .commit();
                
                Toast.makeText(getContext(), 
                    "Auto-selected " + components.size() + " components for ₹" + budget, 
                    Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

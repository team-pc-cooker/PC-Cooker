package com.app.pccooker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SellPCFragment extends Fragment {

    private Spinner computerTypeSpinner, yearSpinner, conditionSpinner;
    private EditText modelInput, specificationsInput, contactInput;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private final String[] computerTypes = {"Select Computer Type", "Desktop"};
    private final String[] years = {"Select Year", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "Older"};
    private final String[] conditions = {"Select Condition", "Excellent", "Good", "Fair", "Poor"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_pc, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        computerTypeSpinner = view.findViewById(R.id.sellComputerTypeSpinner);
        yearSpinner = view.findViewById(R.id.sellYearSpinner);
        conditionSpinner = view.findViewById(R.id.conditionSpinner);
        modelInput = view.findViewById(R.id.sellModelInput);
        specificationsInput = view.findViewById(R.id.specificationsInput);
        contactInput = view.findViewById(R.id.contactInput);
        submitButton = view.findViewById(R.id.submitSellButton);

        // Setup spinners
        setupSpinners();

        // Setup submit button
        submitButton.setOnClickListener(v -> submitSellRequest());

        return view;
    }

    private void setupSpinners() {
        // Custom array adapters with black text on white background
        ArrayAdapter<String> computerAdapter = new ArrayAdapter<String>(requireContext(), 
            android.R.layout.simple_spinner_item, computerTypes) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setBackgroundColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };
        computerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        computerTypeSpinner.setAdapter(computerAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(requireContext(), 
            android.R.layout.simple_spinner_item, years) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setBackgroundColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(requireContext(), 
            android.R.layout.simple_spinner_item, conditions) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setBackgroundColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);
    }

    private void submitSellRequest() {
        // Validate inputs
        if (computerTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select computer type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (yearSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select year", Toast.LENGTH_SHORT).show();
            return;
        }

        if (conditionSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select condition", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(modelInput.getText().toString().trim())) {
            modelInput.setError("Please enter computer model");
            return;
        }

        if (TextUtils.isEmpty(specificationsInput.getText().toString().trim())) {
            specificationsInput.setError("Please enter specifications");
            return;
        }

        if (TextUtils.isEmpty(contactInput.getText().toString().trim())) {
            contactInput.setError("Please enter contact number");
            return;
        }

        // Get current user
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(getContext(), "Please login to submit sell request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create sell request data
        Map<String, Object> sellRequest = new HashMap<>();
        sellRequest.put("computerType", computerTypeSpinner.getSelectedItem().toString());
        sellRequest.put("year", yearSpinner.getSelectedItem().toString());
        sellRequest.put("model", modelInput.getText().toString().trim());
        sellRequest.put("condition", conditionSpinner.getSelectedItem().toString());
        sellRequest.put("specifications", specificationsInput.getText().toString().trim());
        sellRequest.put("contactNumber", contactInput.getText().toString().trim());
        sellRequest.put("userId", userId);
        sellRequest.put("status", "Pending");
        sellRequest.put("timestamp", System.currentTimeMillis());

        // Save to Firestore
        db.collection("sellRequests")
                .add(sellRequest)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Sell request submitted successfully! We'll contact you soon.", Toast.LENGTH_LONG).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        computerTypeSpinner.setSelection(0);
        yearSpinner.setSelection(0);
        conditionSpinner.setSelection(0);
        modelInput.setText("");
        specificationsInput.setText("");
        contactInput.setText("");
    }
} 
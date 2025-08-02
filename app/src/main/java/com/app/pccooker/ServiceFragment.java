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

public class ServiceFragment extends Fragment {

    private Spinner computerTypeSpinner, yearSpinner, issueTypeSpinner;
    private EditText modelInput, issueDescriptionInput;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private final String[] computerTypes = {"Select Computer Type", "Desktop"};
    private final String[] years = {"Select Year", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "Older"};
    private final String[] issueTypes = {"Select Issue Type", "Hardware Issue", "Software Issue", "Both"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        computerTypeSpinner = view.findViewById(R.id.computerTypeSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        issueTypeSpinner = view.findViewById(R.id.issueTypeSpinner);
        modelInput = view.findViewById(R.id.modelInput);
        issueDescriptionInput = view.findViewById(R.id.issueDescriptionInput);
        submitButton = view.findViewById(R.id.submitServiceButton);

        // Setup spinners
        setupSpinners();

        // Setup submit button
        submitButton.setOnClickListener(v -> submitServiceRequest());

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

        ArrayAdapter<String> issueAdapter = new ArrayAdapter<String>(requireContext(), 
            android.R.layout.simple_spinner_item, issueTypes) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setBackgroundColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };
        issueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issueTypeSpinner.setAdapter(issueAdapter);
    }

    private void submitServiceRequest() {
        // Validate inputs
        if (computerTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select computer type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (yearSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select year", Toast.LENGTH_SHORT).show();
            return;
        }

        if (issueTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select issue type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(modelInput.getText().toString().trim())) {
            modelInput.setError("Please enter computer model");
            return;
        }

        if (TextUtils.isEmpty(issueDescriptionInput.getText().toString().trim())) {
            issueDescriptionInput.setError("Please describe the issue");
            return;
        }

        // Get current user
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(getContext(), "Please login to submit service request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create service request data
        Map<String, Object> serviceRequest = new HashMap<>();
        serviceRequest.put("computerType", computerTypeSpinner.getSelectedItem().toString());
        serviceRequest.put("year", yearSpinner.getSelectedItem().toString());
        serviceRequest.put("model", modelInput.getText().toString().trim());
        serviceRequest.put("issueType", issueTypeSpinner.getSelectedItem().toString());
        serviceRequest.put("issueDescription", issueDescriptionInput.getText().toString().trim());
        serviceRequest.put("userId", userId);
        serviceRequest.put("status", "Pending");
        serviceRequest.put("timestamp", System.currentTimeMillis());

        // Save to Firestore
        db.collection("serviceRequests")
                .add(serviceRequest)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Service request submitted successfully!", Toast.LENGTH_LONG).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        computerTypeSpinner.setSelection(0);
        yearSpinner.setSelection(0);
        issueTypeSpinner.setSelection(0);
        modelInput.setText("");
        issueDescriptionInput.setText("");
    }
} 
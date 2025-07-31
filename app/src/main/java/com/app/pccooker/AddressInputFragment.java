package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.pccooker.models.AddressModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddressInputFragment extends Fragment {

    private EditText inputName, inputMobile, inputAddress, inputLandmark, inputPincode, inputCustomLabel;
    private Spinner stateSpinner, citySpinner;
    private RadioGroup labelGroup;
    private RadioButton radioHome, radioWork, radioOther;
    private Button btnSaveAddress, btnProceedToPayment;
    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_input, container, false);

        initViews(view);
        setupFirebase();
        setupSpinners();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        inputName = view.findViewById(R.id.inputName);
        inputMobile = view.findViewById(R.id.inputMobile);
        inputAddress = view.findViewById(R.id.inputAddress);
        inputLandmark = view.findViewById(R.id.inputLandmark);
        inputPincode = view.findViewById(R.id.inputPincode);
        inputCustomLabel = view.findViewById(R.id.inputCustomLabel);
        stateSpinner = view.findViewById(R.id.stateSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        labelGroup = view.findViewById(R.id.addressLabelGroup);
        radioHome = view.findViewById(R.id.radioHome);
        radioWork = view.findViewById(R.id.radioWork);
        radioOther = view.findViewById(R.id.radioOther);
        btnSaveAddress = view.findViewById(R.id.btnSaveAddress);
        btnProceedToPayment = view.findViewById(R.id.btnProceedToPayment);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

    private void setupSpinners() {
        // Setup state spinner
        String[] states = {"Select State", "Andhra Pradesh", "Telangana", "Karnataka", "Tamil Nadu", "Kerala"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        
        // Setup city spinner
        String[] cities = {"Select City", "Hyderabad", "Vijayawada", "Guntur", "Visakhapatnam", "Warangal"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }

    private void setupClickListeners() {
        radioOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            inputCustomLabel.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnSaveAddress.setOnClickListener(v -> saveAddress());
        btnProceedToPayment.setOnClickListener(v -> proceedToPayment());
    }

    private void saveAddress() {
        String name = inputName.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String landmark = inputLandmark.getText().toString().trim();
        String pincode = inputPincode.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty() || pincode.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String label = "Home"; // Default
        if (radioWork.isChecked()) {
            label = "Work";
        } else if (radioOther.isChecked()) {
            String customLabel = inputCustomLabel.getText().toString().trim();
            label = customLabel.isEmpty() ? "Other" : customLabel;
        }

        String state = stateSpinner.getSelectedItem() != null ? stateSpinner.getSelectedItem().toString() : "Select State";
        String city = citySpinner.getSelectedItem() != null ? citySpinner.getSelectedItem().toString() : "Select City";

        if (state.equals("Select State") || city.equals("Select City")) {
            Toast.makeText(getContext(), "Please select state and city", Toast.LENGTH_SHORT).show();
            return;
        }

        AddressModel addressModel = new AddressModel();
        addressModel.setName(name);
        addressModel.setMobile(mobile);
        addressModel.setAddress(address + (landmark.isEmpty() ? "" : ", " + landmark));
        addressModel.setPincode(pincode);
        addressModel.setState(state);
        addressModel.setCity(city);
        addressModel.setLabel(label);

        if (userId != null) {
            db.collection("users").document(userId).collection("addresses")
                    .add(addressModel)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Address saved successfully", Toast.LENGTH_SHORT).show();
                        btnProceedToPayment.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to save address", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void proceedToPayment() {
        // Navigate to payment fragment
        PaymentFragment paymentFragment = new PaymentFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, paymentFragment)
                .addToBackStack(null)
                .commit();
    }
}
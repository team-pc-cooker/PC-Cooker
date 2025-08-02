package com.app.pccooker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.pccooker.models.AddressModel;
import com.app.pccooker.utils.PincodeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddAddressFragment extends Fragment {

    private EditText inputName, inputMobile, inputAddress, inputLandmark, inputPincode, inputCustomLabel;
    private Spinner stateSpinner, citySpinner;
    private RadioGroup labelGroup;
    private RadioButton radioHome, radioWork, radioOther;
    private Button saveAddressButton;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_address, container, false);

        // Initialize views
        inputName = view.findViewById(R.id.inputName);
        inputMobile = view.findViewById(R.id.inputMobile);
        inputAddress = view.findViewById(R.id.inputAddress);
        inputLandmark = view.findViewById(R.id.inputLandmark);
        inputPincode = view.findViewById(R.id.inputPincode);
        inputCustomLabel = view.findViewById(R.id.inputCustomLabel);
        
        stateSpinner = view.findViewById(R.id.stateSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        
        labelGroup = view.findViewById(R.id.labelGroup);
        radioHome = view.findViewById(R.id.radioHome);
        radioWork = view.findViewById(R.id.radioWork);
        radioOther = view.findViewById(R.id.radioOther);
        
        saveAddressButton = view.findViewById(R.id.saveAddressButton);

        // Initialize Firebase
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        setupSpinners();
        setupListeners();
        setupValidation();

        return view;
    }

    private void setupSpinners() {
        // Setup state spinner (only Andhra Pradesh)
        String[] states = {"Andhra Pradesh"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_dropdown_item, states);
        stateSpinner.setAdapter(stateAdapter);

        // Setup city spinner (will be populated based on pincode)
        String[] defaultCities = {"Select City"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_dropdown_item, defaultCities);
        citySpinner.setAdapter(cityAdapter);
    }

    private void setupListeners() {
        // Custom label visibility
        labelGroup.setOnCheckedChangeListener((group, checkedId) -> {
            inputCustomLabel.setVisibility(checkedId == R.id.radioOther ? View.VISIBLE : View.GONE);
        });

        // Pincode validation
        inputPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    validatePincode();
                }
            }
        });

        // Mobile number validation
        inputMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateMobileNumber();
            }
        });

        saveAddressButton.setOnClickListener(v -> saveAddress());
    }

    private void setupValidation() {
        // Real-time mobile validation
        inputMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String mobile = s.toString();
                if (mobile.length() == 10) {
                    if (!mobile.matches("^[6-9]\\d{9}$")) {
                        inputMobile.setError("Please enter a valid Indian mobile number");
                    } else {
                        inputMobile.setError(null);
                    }
                }
            }
        });
    }

    private void validatePincode() {
        String pincode = inputPincode.getText().toString().trim();
        if (pincode.length() == 6) {
            PincodeUtils.lookupPincode(pincode, requireContext(), (city, state) -> {
                if (city != null && state != null) {
                    if ("OTHER_STATE".equals(city)) {
                        // Show message for other states
                        Toast.makeText(requireContext(), 
                            "Sorry! We are currently not operating our services in " + state + ". We only serve Andhra Pradesh.", 
                            Toast.LENGTH_LONG).show();
                        // Reset spinners to default
                        stateSpinner.setSelection(0);
                        citySpinner.setAdapter(new ArrayAdapter<>(requireContext(), 
                                android.R.layout.simple_spinner_dropdown_item, new String[]{"Select City"}));
                    } else {
                        // Valid Andhra Pradesh pincode
                        stateSpinner.setSelection(0); // Andhra Pradesh
                        
                        // Update city spinner
                        String[] cities = {city};
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), 
                                android.R.layout.simple_spinner_dropdown_item, cities);
                        citySpinner.setAdapter(cityAdapter);
                        citySpinner.setSelection(0);
                        
                        Toast.makeText(requireContext(), 
                            "City: " + city + ", State: " + state, 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), 
                        "Invalid pincode or service not available in this area", 
                        Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void validateMobileNumber() {
        String mobile = inputMobile.getText().toString().trim();
        if (mobile.length() == 10) {
            if (!mobile.matches("^[6-9]\\d{9}$")) {
                inputMobile.setError("Please enter a valid Indian mobile number");
            } else {
                inputMobile.setError(null);
            }
        }
    }

    private void saveAddress() {
        String name = inputName.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String landmark = inputLandmark.getText().toString().trim();
        String pincode = inputPincode.getText().toString().trim();

        // Validation
        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty() || pincode.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mobile number validation
        if (!Patterns.PHONE.matcher(mobile).matches() || !mobile.matches("^[6-9]\\d{9}$")) {
            inputMobile.setError("Enter valid Indian mobile number");
            return;
        }

        // Pincode validation
        if (pincode.length() != 6) {
            inputPincode.setError("Invalid Pincode");
            return;
        }

        String label = "Home"; // Default
        if (radioWork.isChecked()) {
            label = "Work";
        } else if (radioOther.isChecked()) {
            String customLabel = inputCustomLabel.getText().toString().trim();
            label = customLabel.isEmpty() ? "Other" : customLabel;
        }

        String state = stateSpinner.getSelectedItem() != null ? stateSpinner.getSelectedItem().toString() : "Andhra Pradesh";
        String city = citySpinner.getSelectedItem() != null ? citySpinner.getSelectedItem().toString() : "Select City";

        if (city.equals("Select City")) {
            Toast.makeText(requireContext(), "Please select a valid city", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create address model
        AddressModel addressModel = new AddressModel();
        addressModel.setName(name);
        addressModel.setMobile(mobile);
        addressModel.setAddress(address + (landmark.isEmpty() ? "" : ", " + landmark));
        addressModel.setPincode(pincode);
        addressModel.setState(state);
        addressModel.setCity(city);
        addressModel.setLabel(label);

        // Save to Firebase
        if (userId != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("addresses")
                    .add(addressModel)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(requireContext(), "Address saved successfully", Toast.LENGTH_SHORT).show();
                        // Navigate back to address selection
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to save address: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "Please login to save address", Toast.LENGTH_SHORT).show();
        }
    }
} 
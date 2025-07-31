
package com.app.pccooker;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.pccooker.models.AddressModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddAddressActivity extends AppCompatActivity {

    private EditText inputName, inputMobile, inputAddress, inputPincode, inputCustomLabel;
    private Spinner stateSpinner, citySpinner;
    private RadioGroup labelGroup;
    private RadioButton radioHome, radioWork, radioOther;
    private Button btnSaveAddress;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_address_input);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Address");
        }

        initViews();
        setupFirebase();
        setupClickListeners();
    }

    private void initViews() {
        try {
            inputName = findViewById(R.id.inputName);
            inputMobile = findViewById(R.id.inputMobile);
            inputAddress = findViewById(R.id.inputAddress);
            inputPincode = findViewById(R.id.inputPincode);
            inputCustomLabel = findViewById(R.id.inputCustomLabel);
            stateSpinner = findViewById(R.id.stateSpinner);
            citySpinner = findViewById(R.id.citySpinner);
            labelGroup = findViewById(R.id.addressLabelGroup);
            radioHome = findViewById(R.id.radioHome);
            radioWork = findViewById(R.id.radioWork);
            radioOther = findViewById(R.id.radioOther);
            btnSaveAddress = findViewById(R.id.btnSaveAddress);
            
            // Initialize spinners with default data
            setupSpinners();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSpinners() {
        // Setup state spinner
        String[] states = {"Select State", "Andhra Pradesh", "Telangana"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        
        // Setup city spinner
        String[] cities = {"Select City"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

    private void setupClickListeners() {
        radioOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            inputCustomLabel.setVisibility(isChecked ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        btnSaveAddress.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String name = inputName.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String pincode = inputPincode.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty() || pincode.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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

        AddressModel addressModel = new AddressModel();
        addressModel.setName(name);
        addressModel.setMobile(mobile);
        addressModel.setAddress(address);
        addressModel.setPincode(pincode);
        addressModel.setState(state);
        addressModel.setCity(city);
        addressModel.setLabel(label);

        if (userId != null) {
            db.collection("users").document(userId).collection("addresses")
                    .add(addressModel)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save address", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

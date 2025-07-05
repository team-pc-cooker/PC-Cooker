package com.app.pccooker;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.app.pccooker.models.AddressModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText nameInput, mobileInput, addressInput, pincodeInput;
    private Spinner stateSpinner, citySpinner;
    private Button saveBtn;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private AddressModel editingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        nameInput = findViewById(R.id.inputName);
        mobileInput = findViewById(R.id.inputMobile);
        addressInput = findViewById(R.id.inputAddress);
        pincodeInput = findViewById(R.id.inputPincode);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        saveBtn = findViewById(R.id.btnSaveAddress);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        editingAddress = getIntent().getParcelableExtra("addressData");

        if (editingAddress != null) {
            nameInput.setText(editingAddress.getName());
            mobileInput.setText(editingAddress.getMobile());
            addressInput.setText(editingAddress.getAddress());
            pincodeInput.setText(editingAddress.getPincode());
            // You'll need to set spinner selections here if needed
            saveBtn.setText("Update Address");
        }

        saveBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                if (editingAddress == null) {
                    addAddress();
                } else {
                    updateAddress();
                }
            }
        });
    }

    private boolean validateInputs() {
        String name = nameInput.getText().toString().trim();
        String mobile = mobileInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String pincode = pincodeInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(address) || TextUtils.isEmpty(pincode)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.PHONE.matcher(mobile).matches() || !mobile.matches("^[6-9]\\d{9}$")) {
            mobileInput.setError("Enter valid mobile number");
            return false;
        }

        return true;
    }

    private void addAddress() {
        String uid = auth.getUid();
        if (uid == null) return;

        Map<String, Object> addressMap = getAddressMap();
        db.collection("users").document(uid).collection("addresses")
                .add(addressMap)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Address saved!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving address", Toast.LENGTH_SHORT).show());
    }

    private void updateAddress() {
        String uid = auth.getUid();
        if (uid == null || editingAddress.getId() == null) return;

        Map<String, Object> addressMap = getAddressMap();
        db.collection("users").document(uid).collection("addresses")
                .document(editingAddress.getId())
                .set(addressMap)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Address updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating address", Toast.LENGTH_SHORT).show());
    }

    private Map<String, Object> getAddressMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", nameInput.getText().toString().trim());
        map.put("mobile", mobileInput.getText().toString().trim());
        map.put("address", addressInput.getText().toString().trim());
        map.put("pincode", pincodeInput.getText().toString().trim());
        map.put("state", stateSpinner.getSelectedItem().toString());
        map.put("city", citySpinner.getSelectedItem().toString());
        map.put("label", "Home"); // Default or fetch from RadioGroup if applicable
        return map;
    }
}

package com.app.pccooker;

import android.os.Bundle;
import android.text.*;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.pccooker.models.AddressModel;
import com.app.pccooker.utils.PincodeUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.*;

public class EditAddressActivity extends AppCompatActivity {

    private EditText inputName, inputMobile, inputAddress, inputPincode, inputCustomLabel;
    private Spinner stateSpinner, citySpinner;
    private RadioGroup labelGroup;
    private RadioButton radioHome, radioWork, radioOther;
    private Button updateBtn;

    private AddressModel currentAddress;

    private CheckBox checkboxDefault;


    private FirebaseFirestore db;
    private final String[] states = {"Select State", "Andhra Pradesh", "Telangana", "Tamil Nadu"};
    private final Map<String, List<String>> stateToCities = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        currentAddress = getIntent().getParcelableExtra("addressData");
        if (currentAddress == null) {
            Toast.makeText(this, "Address load error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        bindViews();
        loadCityDataFromAssets();
        populateSpinners();
        setupListeners();
        fillAddressData();
    }

    private void bindViews() {



        inputName = findViewById(R.id.editName);
        inputMobile = findViewById(R.id.editMobile);
        inputAddress = findViewById(R.id.editAddress);
        inputPincode = findViewById(R.id.editPincode);
        inputCustomLabel = findViewById(R.id.editCustomLabel);
        stateSpinner = findViewById(R.id.editStateSpinner);
        citySpinner = findViewById(R.id.editCitySpinner);
        labelGroup = findViewById(R.id.editLabelGroup);
        radioHome = findViewById(R.id.editRadioHome);
        radioWork = findViewById(R.id.editRadioWork);
        radioOther = findViewById(R.id.editRadioOther);
        updateBtn = findViewById(R.id.btnUpdateAddress);
        checkboxDefault = findViewById(R.id.checkboxDefault);

    }

    private void loadCityDataFromAssets() {
        try {
            InputStream is = getAssets().open("andhra_cities.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONObject obj = new JSONObject(new String(buffer));
            for (String state : states) {
                if (obj.has(state)) {
                    JSONArray array = obj.getJSONArray(state);
                    List<String> cities = new ArrayList<>();
                    cities.add("Select City");
                    for (int i = 0; i < array.length(); i++) {
                        cities.add(array.getString(i));
                    }
                    stateToCities.put(state, cities);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSpinners() {
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
        stateSpinner.setAdapter(stateAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedState = states[pos];
                List<String> cities = stateToCities.get(selectedState);
                if (cities != null) {
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(EditAddressActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                    citySpinner.setAdapter(cityAdapter);

                    if (currentAddress != null && currentAddress.getCity() != null) {
                        setSpinnerSelection(citySpinner, currentAddress.getCity());
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fillAddressData() {
        inputName.setText(currentAddress.getName());
        inputMobile.setText(currentAddress.getMobile());
        inputAddress.setText(currentAddress.getAddress());
        inputPincode.setText(currentAddress.getPincode());

        checkboxDefault.setChecked(currentAddress.isDefault()); // Ensure this method exists in AddressModel


        setSpinnerSelection(stateSpinner, currentAddress.getState());

        String label = currentAddress.getLabel();
        if ("Home".equalsIgnoreCase(label)) labelGroup.check(R.id.editRadioHome);
        else if ("Work".equalsIgnoreCase(label)) labelGroup.check(R.id.editRadioWork);
        else {
            labelGroup.check(R.id.editRadioOther);
            inputCustomLabel.setText(label);
            inputCustomLabel.setVisibility(View.VISIBLE);
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null || value == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (value.equalsIgnoreCase(adapter.getItem(i).toString())) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setupListeners() {
        labelGroup.setOnCheckedChangeListener((group, checkedId) -> {
            inputCustomLabel.setVisibility(checkedId == R.id.editRadioOther ? View.VISIBLE : View.GONE);
        });

        inputPincode.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    validatePincode(s.toString());
                }
            }
        });

        updateBtn.setOnClickListener(v -> {
            if (!validate()) return;

            String label;
            int checkedId = labelGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.editRadioHome) label = "Home";
            else if (checkedId == R.id.editRadioWork) label = "Work";
            else label = inputCustomLabel.getText().toString().trim();

            Map<String, Object> updated = new HashMap<>();
            updated.put("name", inputName.getText().toString().trim());
            updated.put("mobile", inputMobile.getText().toString().trim());
            updated.put("address", inputAddress.getText().toString().trim());
            updated.put("pincode", inputPincode.getText().toString().trim());
            updated.put("state", stateSpinner.getSelectedItem().toString());
            updated.put("city", citySpinner.getSelectedItem() != null ? citySpinner.getSelectedItem().toString() : "");
            updated.put("label", label);
            updated.put("isDefault", checkboxDefault.isChecked());


            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null || currentAddress == null || currentAddress.getId() == null) return;

            db.collection("users").document(uid)
                    .collection("addresses").document(currentAddress.getId())
                    .set(updated)
                    .addOnSuccessListener(unused -> {
                        Snackbar.make(updateBtn, "Address updated", Snackbar.LENGTH_SHORT).show();

                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
        });
    }

    private void validatePincode(String pincode) {
        PincodeUtils.lookupPincode(pincode, this, (city, state) -> {
            if (city != null && state != null) {
                setSpinnerSelection(stateSpinner, state);
                List<String> cities = stateToCities.get(state);
                if (cities != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditAddressActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                    citySpinner.setAdapter(adapter);
                    setSpinnerSelection(citySpinner, city);
                }
            } else {
                Toast.makeText(this, "Invalid Pincode", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validate() {
        if (TextUtils.isEmpty(inputName.getText()) ||
                TextUtils.isEmpty(inputMobile.getText()) ||
                TextUtils.isEmpty(inputAddress.getText()) ||
                TextUtils.isEmpty(inputPincode.getText())) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.PHONE.matcher(inputMobile.getText()).matches() ||
                !inputMobile.getText().toString().matches("^[6-9]\\d{9}$")) {
            inputMobile.setError("Enter valid mobile");
            return false;
        }

        if (inputPincode.getText().toString().length() != 6) {
            inputPincode.setError("Invalid Pincode");
            return false;
        }

        return true;
    }
}

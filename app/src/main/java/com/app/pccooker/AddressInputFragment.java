package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
import android.util.Patterns;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.pccooker.utils.PincodeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import org.json.*;

import java.io.InputStream;
import java.util.*;

public class AddressInputFragment extends Fragment {

    private EditText nameInput, mobileInput, addressInput, pincodeInput, inputCustomLabel, inputLandmark;
    private Spinner stateSpinner, citySpinner;
    private Button saveButton, viewSavedBtn;
    private RadioGroup labelGroup;
    private RadioButton radioHome, radioWork, radioOther;

    private final String[] states = {"Select State", "Andhra Pradesh", "Telangana"};
    private final Map<String, List<String>> stateToCities = new HashMap<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_input, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameInput = view.findViewById(R.id.inputName);
        mobileInput = view.findViewById(R.id.inputMobile);
        addressInput = view.findViewById(R.id.inputAddress);
        pincodeInput = view.findViewById(R.id.inputPincode);
        stateSpinner = view.findViewById(R.id.stateSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        inputCustomLabel = view.findViewById(R.id.inputCustomLabel);
        inputLandmark = view.findViewById(R.id.inputLandmark);
        saveButton = view.findViewById(R.id.btnSaveAddress);
        viewSavedBtn = view.findViewById(R.id.btnViewSavedAddresses);

        labelGroup = view.findViewById(R.id.addressLabelGroup);
        radioHome = view.findViewById(R.id.radioHome);
        radioWork = view.findViewById(R.id.radioWork);
        radioOther = view.findViewById(R.id.radioOther);

        loadCityDataFromAssets();

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, states);
        stateSpinner.setAdapter(stateAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedState = states[pos];
                List<String> cities = stateToCities.get(selectedState);
                if (cities != null) {
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities);
                    citySpinner.setAdapter(cityAdapter);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        labelGroup.setOnCheckedChangeListener((group, checkedId) -> {
            inputCustomLabel.setVisibility(checkedId == R.id.radioOther ? View.VISIBLE : View.GONE);
        });

        pincodeInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.length() == 6) validatePincode();
            }
        });

        saveButton.setOnClickListener(v -> {
            if (validateInputs()) saveAddressToFirestore();
        });

        return view;
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (spinner.getAdapter() != null) {
            for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
                if (spinner.getAdapter().getItem(i).toString().equalsIgnoreCase(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

        view.findViewById(R.id.btnViewSavedAddresses).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ManageAddressFragment())  // ensure this ID exists
                    .addToBackStack(null)
                    .commit();
        });

        checkIfUserHasSavedAddresses();

        return view;
    }

    private void loadCityDataFromAssets() {
        try {
            InputStream is = requireContext().getAssets().open("andhra_cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            for (String state : states) {
                if (obj.has(state)) {
                    JSONArray cityArray = obj.getJSONArray(state);
                    List<String> cities = new ArrayList<>();
                    cities.add("Select City");
                    for (int i = 0; i < cityArray.length(); i++) {
                        cities.add(cityArray.getString(i));
                    }
                    stateToCities.put(state, cities);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validatePincode() {
        String pincode = pincodeInput.getText().toString().trim();
        if (pincode.length() == 6) {
            PincodeUtils.lookupPincode(pincode, getContext(), (city, state) -> {
                if (city != null && state != null) {
                    if ("OTHER_STATE".equals(city)) {
                        // Show message for other states
                        Toast.makeText(getContext(), 
                            "Sorry! We are currently not operating our services in " + state + ". We only serve Andhra Pradesh.", 
                            Toast.LENGTH_LONG).show();
                        // Reset spinners to default
                        stateSpinner.setSelection(0);
                        citySpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"Select City"}));
                    } else {
                        // Valid Andhra Pradesh pincode
                        // Step 1: Set the state spinner to Andhra Pradesh
                        setSpinnerSelection(stateSpinner, "Andhra Pradesh");

                        // Step 2: Post delayed to wait for city spinner to load
                        stateSpinner.postDelayed(() -> {
                            List<String> cities = stateToCities.get("Andhra Pradesh");
                            if (cities != null) {
                                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities);
                                citySpinner.setAdapter(cityAdapter);
                                int index = cities.indexOf(city);
                                if (index != -1) {
                                    citySpinner.setSelection(index);
                                } else {
                                    // If exact city not found, try to find closest match
                                    for (int i = 0; i < cities.size(); i++) {
                                        if (cities.get(i).toLowerCase().contains(city.toLowerCase()) || 
                                            city.toLowerCase().contains(cities.get(i).toLowerCase())) {
                                            citySpinner.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        }, 100); // delay ensures city spinner is updated after state
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid Pincode. Please enter a valid 6-digit pincode.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(nameInput.getText()) ||
                TextUtils.isEmpty(mobileInput.getText()) ||
                TextUtils.isEmpty(addressInput.getText()) ||
                TextUtils.isEmpty(pincodeInput.getText()) ||
                TextUtils.isEmpty(inputLandmark.getText())) {
            Toast.makeText(getContext(), "Please fill all fields including landmark", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.PHONE.matcher(mobileInput.getText()).matches() ||
                !mobileInput.getText().toString().matches("^[6-9]\\d{9}$")) {
            mobileInput.setError("Enter valid mobile");
            return false;
        }

        if (pincodeInput.getText().toString().length() != 6) {
            pincodeInput.setError("Invalid Pincode");
            return false;
        }

        // Validate Other label
        if (radioOther.isChecked() && TextUtils.isEmpty(inputCustomLabel.getText())) {
            inputCustomLabel.setError("Please enter custom label");
            Toast.makeText(getContext(), "Please enter a custom label for 'Other' option", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveAddressToFirestore() {
        String userId = mAuth.getUid();
        if (userId == null) return;

        String tempLabel = "";  // default value
        int checkedId = labelGroup.getCheckedRadioButtonId();

        if (checkedId == R.id.radioHome) {
            tempLabel = "Home";
        } else if (checkedId == R.id.radioWork) {
            tempLabel = "Work";
        } else if (checkedId == R.id.radioOther) {
            tempLabel = inputCustomLabel.getText().toString().trim();
            if (tempLabel.isEmpty()) {
                inputCustomLabel.setError("Enter label");
                return;
            }
        }

        final String label = tempLabel;  // safely final for lambda use


        String addressStr = addressInput.getText().toString().trim();

        db.collection("users").document(userId).collection("addresses")
                .whereEqualTo("address", addressStr)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        Toast.makeText(getContext(), "Address already saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", nameInput.getText().toString().trim());
                        data.put("mobile", mobileInput.getText().toString().trim());
                        data.put("address", addressStr);
                        data.put("pincode", pincodeInput.getText().toString().trim());
                        data.put("state", stateSpinner.getSelectedItem().toString());
                        data.put("city", citySpinner.getSelectedItem() != null ? citySpinner.getSelectedItem().toString() : "");
                        data.put("label", label);
                        data.put("landmark", inputLandmark.getText().toString().trim());

                        db.collection("users").document(userId)
                                .collection("addresses")
                                .add(data)
                                .addOnSuccessListener(doc -> {
                                    Toast.makeText(getContext(), "Address saved", Toast.LENGTH_SHORT).show();
                                    clearInputs();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to save address", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void clearInputs() {
        nameInput.setText("");
        mobileInput.setText("");
        addressInput.setText("");
        pincodeInput.setText("");
        stateSpinner.setSelection(0);
        citySpinner.setSelection(0);
        labelGroup.check(R.id.radioHome);
        inputCustomLabel.setText("");
        inputLandmark.setText("");
        inputCustomLabel.setVisibility(View.GONE);
    }

    private void checkIfUserHasSavedAddresses() {
        String userId = mAuth.getUid();
        if (userId == null) return;
        db.collection("users").document(userId).collection("addresses")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        viewSavedBtn.setVisibility(View.VISIBLE);
                    } else {
                        viewSavedBtn.setVisibility(View.GONE);
                    }
                });
    }
}
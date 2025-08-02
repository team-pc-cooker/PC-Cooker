package com.app.pccooker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.AddressModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddressSelectionFragment extends Fragment {

    private RecyclerView addressRecyclerView;
    private LinearLayout emptyAddressLayout;
    private Button addNewAddressButton;
    private Button proceedToPaymentButton;
    private AddressAdapter addressAdapter;
    private List<AddressModel> addressList = new ArrayList<>();
    private AddressModel selectedAddress = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_selection, container, false);

        // Initialize views
        addressRecyclerView = view.findViewById(R.id.addressRecyclerView);
        emptyAddressLayout = view.findViewById(R.id.emptyAddressLayout);
        addNewAddressButton = view.findViewById(R.id.addNewAddressButton);
        proceedToPaymentButton = view.findViewById(R.id.proceedToPaymentButton);

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set click listeners
        addNewAddressButton.setOnClickListener(v -> addNewAddress());
        proceedToPaymentButton.setOnClickListener(v -> proceedToPayment());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("addresses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    addressList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        AddressModel address = document.toObject(AddressModel.class);
                        if (address != null) {
                            address.setId(document.getId());
                            addressList.add(address);
                        }
                    }

                    if (addressList.isEmpty()) {
                        emptyAddressLayout.setVisibility(View.VISIBLE);
                        addressRecyclerView.setVisibility(View.GONE);
                        proceedToPaymentButton.setVisibility(View.GONE);
                    } else {
                        emptyAddressLayout.setVisibility(View.GONE);
                        addressRecyclerView.setVisibility(View.VISIBLE);
                        proceedToPaymentButton.setVisibility(View.VISIBLE);
                        
                        addressAdapter = new AddressAdapter(requireContext(), addressList, 
                                new AddressAdapter.OnAddressActionListener() {
                            @Override
                            public void onAddressSelected(AddressModel address) {
                                selectedAddress = address;
                                updateProceedButton();
                            }

                            @Override
                            public void onAddressEdit(AddressModel address) {
                                // Navigate to edit address
                                EditAddressActivity.startEditAddress(requireContext(), address);
                            }

                            @Override
                            public void onAddressDelete(AddressModel address) {
                                deleteAddress(address);
                            }
                        });
                        addressRecyclerView.setAdapter(addressAdapter);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load addresses: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void addNewAddress() {
        // Navigate to add address activity
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AddAddressFragment())
                .addToBackStack("address_selection_to_add")
                .commit();
    }

    private void proceedToPayment() {
        if (selectedAddress == null) {
            Toast.makeText(getContext(), "Please select a delivery address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to payment fragment with selected address
        PaymentFragment paymentFragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString("address_id", selectedAddress.getId());
        paymentFragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, paymentFragment)
                .addToBackStack("address_to_payment")
                .commit();
    }

    private void deleteAddress(AddressModel address) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("addresses")
                .document(address.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Address deleted successfully", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete address: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProceedButton() {
        if (selectedAddress != null) {
            proceedToPaymentButton.setEnabled(true);
            proceedToPaymentButton.setText("Proceed to Payment");
        } else {
            proceedToPaymentButton.setEnabled(false);
            proceedToPaymentButton.setText("Select Address First");
        }
    }
} 
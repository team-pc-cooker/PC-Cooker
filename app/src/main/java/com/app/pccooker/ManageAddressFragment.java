package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.app.pccooker.models.AddressModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class ManageAddressFragment extends Fragment {

    private RecyclerView addressRecyclerView;
    private AddressAdapter adapter;
    private List<AddressModel> addressList = new ArrayList<>();
    private FirebaseFirestore db;
    private String userId;  // ✅ Declare userId here

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_address, container, false);

        addressRecyclerView = view.findViewById(R.id.addressRecyclerView);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button addAddressBtn = view.findViewById(R.id.addAddressBtn);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        adapter = new AddressAdapter(requireContext(), addressList, this::deleteAddress);
        addressRecyclerView.setAdapter(adapter);

        adapter.setOnEditClickListener(address -> {
            Intent intent = new Intent(getContext(), EditAddressActivity.class);
            intent.putExtra("addressData", address);
            startActivityForResult(intent, 100);
        });

        loadAddresses();

        addAddressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddAddressActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadAddresses() {
        if (userId == null) return;

        db.collection("users").document(userId).collection("addresses")
                .get()
                .addOnSuccessListener(query -> {
                    addressList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        AddressModel address = doc.toObject(AddressModel.class);
                        if (address != null) {
                            address.setId(doc.getId());  // For edit/delete support
                            addressList.add(address);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load addresses", Toast.LENGTH_SHORT).show());
    }

    private void deleteAddress(AddressModel address) {
        if (userId == null || address.getId() == null) return;

        db.collection("users").document(userId).collection("addresses")
                .document(address.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Address deleted", Toast.LENGTH_SHORT).show();
                    loadAddresses(); // Refresh list
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to delete address", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            loadAddresses();  // Refresh list after edit
        }
    }
}
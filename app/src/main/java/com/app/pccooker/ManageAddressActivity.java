package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.models.AddressModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class ManageAddressActivity extends AppCompatActivity {

    private RecyclerView addressRecyclerView;
    private Button addAddressBtn;
    private List<AddressModel> addressList = new ArrayList<>();
    private AddressAdapter adapter;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        addressRecyclerView = findViewById(R.id.addressRecyclerView);
        addAddressBtn = findViewById(R.id.addAddressBtn);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        adapter = new AddressAdapter(this, addressList, this::deleteAddress);



        addressRecyclerView.setAdapter(adapter);

        loadAddresses();

        addAddressBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, AddAddressActivity.class);
            startActivity(i);
        });
    }

    private void loadAddresses() {
        db.collection("users").document(uid).collection("addresses")
                .get()
                .addOnSuccessListener(query -> {
                    addressList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        AddressModel model = doc.toObject(AddressModel.class);
                        if (model != null) {
                            model.setId(doc.getId()); // Set Firestore doc ID
                            addressList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    
                    if (addressList.isEmpty()) {
                        Toast.makeText(this, "No saved addresses found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load addresses", Toast.LENGTH_SHORT).show());
    }

    private void deleteAddress(AddressModel address) {
        db.collection("users").document(uid).collection("addresses")
                .document(address.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Address deleted", Toast.LENGTH_SHORT).show();
                    loadAddresses(); // Refresh list
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete address", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }
}

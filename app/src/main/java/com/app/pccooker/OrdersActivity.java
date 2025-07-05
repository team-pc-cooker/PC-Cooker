package com.app.pccooker;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.app.pccooker.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView ordersRecyclerView;
    OrderAdapter adapter;
    List<OrderModel> orderList = new ArrayList<>();
    FirebaseFirestore db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(orderList);
        ordersRecyclerView.setAdapter(adapter);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid).collection("orders")
                .get().addOnSuccessListener(query -> {
                    orderList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String id = doc.getId();
                        String status = doc.getString("status");
                        orderList.add(new OrderModel(id, status));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch orders", Toast.LENGTH_SHORT).show());
    }
}

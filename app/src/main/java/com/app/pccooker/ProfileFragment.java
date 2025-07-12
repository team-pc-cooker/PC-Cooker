package com.app.pccooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileUserName, profileUserEmail;
    private LinearLayout layoutMyOrders, layoutManageAddress, layoutHelpSupport, layoutLogout;

    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profileImage);
        profileUserName = view.findViewById(R.id.profileUserName);
        profileUserEmail = view.findViewById(R.id.profileUserEmail);

        layoutMyOrders = view.findViewById(R.id.layoutMyOrders);
        layoutManageAddress = view.findViewById(R.id.layoutManageAddress);
        layoutHelpSupport = view.findViewById(R.id.layoutHelpSupport);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        // 🔐 Get current user from FirebaseAuth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            profileUserEmail.setText(currentUser.getEmail());

            String name = currentUser.getDisplayName();
            profileUserName.setText(name != null ? name : "User");

            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_profile);
            }
        }

        // 🚀 Set click actions
        layoutMyOrders.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrderHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        });


        layoutManageAddress.setOnClickListener(v -> {
            Fragment addressFragment = new AddressInputFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, addressFragment)
                    .addToBackStack(null)
                    .commit();
        });


        layoutHelpSupport.setOnClickListener(v -> {
            // TODO: Replace with your Help activity or show dialog
            startActivity(new Intent(getActivity(), HelpActivity.class));
        });

        layoutLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}

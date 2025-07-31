package com.app.pccooker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    private ImageView profileImage, editProfilePicture;
    private TextView profileUserName, profileUserEmail;
    private LinearLayout layoutMyOrders, layoutManageAddress, layoutHelpSupport, layoutLogout;
    private LinearLayout layoutSettings, layoutTheme, layoutNotifications, layoutPrivacy;
    private LinearLayout layoutAbout, layoutContactUs, layoutTerms, layoutFeedback;

    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;

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

        // Initialize views
        profileImage = view.findViewById(R.id.profileImage);
        editProfilePicture = view.findViewById(R.id.editProfilePicture);
        profileUserName = view.findViewById(R.id.profileUserName);
        profileUserEmail = view.findViewById(R.id.profileUserEmail);
        
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Main options
        layoutMyOrders = view.findViewById(R.id.layoutMyOrders);
        layoutManageAddress = view.findViewById(R.id.layoutManageAddress);
        layoutHelpSupport = view.findViewById(R.id.layoutHelpSupport);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        // Settings options
        layoutSettings = view.findViewById(R.id.layoutSettings);
        layoutTheme = view.findViewById(R.id.layoutTheme);
        layoutNotifications = view.findViewById(R.id.layoutNotifications);
        layoutPrivacy = view.findViewById(R.id.layoutPrivacy);

        // About options
        layoutAbout = view.findViewById(R.id.layoutAbout);
        layoutContactUs = view.findViewById(R.id.layoutContactUs);
        layoutTerms = view.findViewById(R.id.layoutTerms);
        layoutFeedback = view.findViewById(R.id.layoutFeedback);

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
        
        // Profile picture edit click handler
        if (editProfilePicture != null) {
            editProfilePicture.setOnClickListener(v -> showImagePickerDialog());
        }
        profileImage.setOnClickListener(v -> showImagePickerDialog());

        // 🚀 Set click actions for main options
        layoutMyOrders.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrderHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        layoutManageAddress.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AddressSelectionFragment())
                    .addToBackStack(null)
                    .commit();
        });

        layoutHelpSupport.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HelpFragment())
                    .addToBackStack(null)
                    .commit();
        });

        layoutLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // Navigate to login
            Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(loginIntent);
        });

        // Settings options
        layoutSettings.setOnClickListener(v -> {
            // TODO: Navigate to settings fragment
            showToast("Settings coming soon!");
        });

        layoutTheme.setOnClickListener(v -> {
            showThemeSelectionDialog();
        });

        layoutNotifications.setOnClickListener(v -> {
            // TODO: Navigate to notification settings
            showToast("Notification settings coming soon!");
        });

        layoutPrivacy.setOnClickListener(v -> {
            // TODO: Navigate to privacy settings
            showToast("Privacy settings coming soon!");
        });

        // About options
        layoutAbout.setOnClickListener(v -> {
            showAboutDialog();
        });

        layoutContactUs.setOnClickListener(v -> {
            showContactUsDialog();
        });

        layoutTerms.setOnClickListener(v -> {
            showTermsDialog();
        });

        layoutFeedback.setOnClickListener(v -> {
            showFeedbackDialog();
        });
    }

    private void showToast(String message) {
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void showThemeSelectionDialog() {
        String[] themes = {"Light Theme", "Dark Theme", "Auto (System)"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Select Theme")
                .setItems(themes, (dialog, which) -> {
                    // TODO: Implement theme selection
                    showToast("Theme: " + themes[which] + " selected!");
                })
                .show();
    }

    private void showAboutDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("About PC Cooker")
                .setMessage("PC Cooker v1.0\n\nYour one-stop solution for custom PC components.\n\n" +
                        "Features:\n" +
                        "• Browse PC components\n" +
                        "• AI-powered recommendations\n" +
                        "• Secure payment gateway\n" +
                        "• Fast delivery across Andhra Pradesh\n\n" +
                        "© 2024 PC Cooker. All rights reserved.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showContactUsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Contact Us")
                .setMessage("📞 Customer Care: +91 98765 43210\n" +
                        "📧 Email: support@pccooker.com\n" +
                        "🏢 Address: Tech Park, Hyderabad, AP\n\n" +
                        "🕒 Working Hours:\n" +
                        "Monday - Saturday: 9:00 AM - 8:00 PM\n" +
                        "Sunday: 10:00 AM - 6:00 PM")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showTermsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Terms & Conditions")
                .setMessage("By using PC Cooker, you agree to:\n\n" +
                        "1. Provide accurate information\n" +
                        "2. Make timely payments\n" +
                        "3. Accept delivery terms\n" +
                        "4. Follow return policies\n\n" +
                        "Full terms available at: pccooker.com/terms")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showFeedbackDialog() {
        android.widget.EditText feedbackInput = new android.widget.EditText(requireContext());
        feedbackInput.setHint("Share your feedback with us...");
        feedbackInput.setMinLines(3);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Send Feedback")
                .setView(feedbackInput)
                .setPositiveButton("Send", (dialog, which) -> {
                    String feedback = feedbackInput.getText().toString();
                    if (!feedback.trim().isEmpty()) {
                        // TODO: Send feedback to Firebase
                        showToast("Thank you for your feedback!");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Update Profile Picture")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if (checkCameraPermission()) {
                                openCamera();
                            } else {
                                requestCameraPermission();
                            }
                            break;
                        case 1:
                            if (checkStoragePermission()) {
                                openGallery();
                            } else {
                                requestStoragePermission();
                            }
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }
    
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }
    
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
    }
    
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    showToast("Camera permission denied");
                }
                break;
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    showToast("Storage permission denied");
                }
                break;
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (data != null && data.getExtras() != null) {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            uploadImageToFirebase(imageBitmap);
                        }
                    }
                    break;
                case REQUEST_IMAGE_PICK:
                    if (data != null && data.getData() != null) {
                        Uri imageUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            uploadImageToFirebase(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            showToast("Failed to load image");
                        }
                    }
                    break;
            }
        }
    }
    
    private void uploadImageToFirebase(Bitmap bitmap) {
        if (currentUser == null) {
            showToast("Please login first");
            return;
        }
        
        // Show uploading toast
        showToast("Uploading profile picture...");
        
        // Compress bitmap
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();
        
        // Create storage reference
        StorageReference profileRef = storageRef.child("profile_images/" + currentUser.getUid() + ".jpg");
        
        // Upload to Firebase Storage
        profileRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update user profile
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();
                        
                        currentUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Update UI
                                        Glide.with(ProfileFragment.this)
                                                .load(uri)
                                                .into(profileImage);
                                        showToast("Profile picture updated successfully!");
                                    } else {
                                        showToast("Failed to update profile");
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to upload image: " + e.getMessage());
                });
    }
}

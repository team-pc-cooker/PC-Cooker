package com.app.pccooker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.adapters.MediaPreviewAdapter;
import com.app.pccooker.utils.MediaUploadManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellPCFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_VIDEO_PICK = 1002;
    private static final int PERMISSION_REQUEST_CODE = 1003;

    private Spinner computerTypeSpinner, yearSpinner, conditionSpinner;
    private EditText modelInput, specificationsInput, contactInput;
    private Button submitButton, uploadPhotoButton, uploadVideoButton;
    private LinearLayout mediaPreviewContainer;
    private RecyclerView mediaPreviewRecyclerView;
    private ProgressBar uploadProgressBar;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private MediaUploadManager mediaUploadManager;
    private MediaPreviewAdapter mediaAdapter;
    private List<MediaPreviewAdapter.MediaItem> selectedMedia = new ArrayList<>();

    private final String[] computerTypes = {"Select Computer Type", "Desktop"};
    private final String[] years = {"Select Year", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "Older"};
    private final String[] conditions = {"Select Condition", "Excellent", "Good", "Fair", "Poor"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_pc, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mediaUploadManager = new MediaUploadManager(requireContext());

        // Initialize views
        initializeViews(view);
        setupSpinners();
        setupMediaRecyclerView();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        computerTypeSpinner = view.findViewById(R.id.sellComputerTypeSpinner);
        yearSpinner = view.findViewById(R.id.sellYearSpinner);
        conditionSpinner = view.findViewById(R.id.conditionSpinner);
        modelInput = view.findViewById(R.id.sellModelInput);
        specificationsInput = view.findViewById(R.id.specificationsInput);
        contactInput = view.findViewById(R.id.contactInput);
        submitButton = view.findViewById(R.id.submitSellButton);
        uploadPhotoButton = view.findViewById(R.id.uploadPhotoButton);
        uploadVideoButton = view.findViewById(R.id.uploadVideoButton);
        mediaPreviewContainer = view.findViewById(R.id.mediaPreviewContainer);
        mediaPreviewRecyclerView = view.findViewById(R.id.mediaPreviewRecyclerView);
    }

    private void setupMediaRecyclerView() {
        mediaAdapter = new MediaPreviewAdapter(requireContext(), selectedMedia, new MediaPreviewAdapter.OnMediaActionListener() {
            @Override
            public void onRemoveMedia(int position) {
                selectedMedia.remove(position);
                updateMediaPreviewVisibility();
                mediaAdapter.notifyDataSetChanged();
            }
        });
        
        mediaPreviewRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        mediaPreviewRecyclerView.setAdapter(mediaAdapter);
    }

    private void setupClickListeners() {
        uploadPhotoButton.setOnClickListener(v -> checkPermissionAndPickImage());
        uploadVideoButton.setOnClickListener(v -> checkPermissionAndPickVideo());
        submitButton.setOnClickListener(v -> submitSellRequest());
    }

    private void checkPermissionAndPickImage() {
        if (checkStoragePermission()) {
            pickImage();
        } else {
            requestStoragePermission();
        }
    }

    private void checkPermissionAndPickVideo() {
        if (checkStoragePermission()) {
            pickVideo();
        } else {
            requestStoragePermission();
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(requireContext(), 
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            PERMISSION_REQUEST_CODE);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_VIDEO_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (selectedUri != null) {
                String mediaType = (requestCode == REQUEST_IMAGE_PICK) ? "Photo" : "Video";
                String path = selectedUri.toString();
                
                MediaPreviewAdapter.MediaItem mediaItem = new MediaPreviewAdapter.MediaItem(selectedUri, mediaType, path);
                selectedMedia.add(mediaItem);
                
                updateMediaPreviewVisibility();
                mediaAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateMediaPreviewVisibility() {
        if (selectedMedia.isEmpty()) {
            mediaPreviewContainer.setVisibility(View.GONE);
        } else {
            mediaPreviewContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setupSpinners() {
        // Custom array adapters with black text on white background
        ArrayAdapter<String> computerAdapter = new ArrayAdapter<String>(requireContext(), 
            android.R.layout.simple_spinner_item, computerTypes) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setBackgroundColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };
        computerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        computerTypeSpinner.setAdapter(computerAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(requireContext(), 
            android.R.layout.simple_spinner_item, years) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setBackgroundColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(requireContext(), 
            android.R.layout.simple_spinner_item, conditions) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setBackgroundColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);
    }

    private void submitSellRequest() {
        // Validate inputs
        if (computerTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select computer type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (yearSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select year", Toast.LENGTH_SHORT).show();
            return;
        }

        if (conditionSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select condition", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(modelInput.getText().toString().trim())) {
            modelInput.setError("Please enter computer model");
            return;
        }

        if (TextUtils.isEmpty(specificationsInput.getText().toString().trim())) {
            specificationsInput.setError("Please enter specifications");
            return;
        }

        if (TextUtils.isEmpty(contactInput.getText().toString().trim())) {
            contactInput.setError("Please enter contact number");
            return;
        }

        // Get current user
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(getContext(), "Please login to submit sell request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        submitButton.setEnabled(false);
        submitButton.setText("Uploading Media...");

        // Upload media files first
        List<Uri> mediaUris = new ArrayList<>();
        for (MediaPreviewAdapter.MediaItem item : selectedMedia) {
            mediaUris.add(item.getUri());
        }

        mediaUploadManager.uploadMediaFiles(mediaUris, userId, new MediaUploadManager.OnMediaUploadListener() {
            @Override
            public void onUploadSuccess(List<String> downloadUrls) {
                // Create sell request data with media URLs
                Map<String, Object> sellRequest = new HashMap<>();
                sellRequest.put("computerType", computerTypeSpinner.getSelectedItem().toString());
                sellRequest.put("year", yearSpinner.getSelectedItem().toString());
                sellRequest.put("model", modelInput.getText().toString().trim());
                sellRequest.put("condition", conditionSpinner.getSelectedItem().toString());
                sellRequest.put("specifications", specificationsInput.getText().toString().trim());
                sellRequest.put("contactNumber", contactInput.getText().toString().trim());
                sellRequest.put("userId", userId);
                sellRequest.put("status", "Pending");
                sellRequest.put("timestamp", System.currentTimeMillis());
                sellRequest.put("mediaUrls", downloadUrls);

                // Save to Firestore
                db.collection("sellRequests")
                        .add(sellRequest)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Sell request submitted successfully! We'll contact you soon.", Toast.LENGTH_LONG).show();
                            clearForm();
                            resetSubmitButton();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show();
                            resetSubmitButton();
                        });
            }

            @Override
            public void onUploadFailure(String error) {
                Toast.makeText(getContext(), "Failed to upload media: " + error, Toast.LENGTH_SHORT).show();
                resetSubmitButton();
            }

            @Override
            public void onUploadProgress(int progress) {
                submitButton.setText("Uploading Media... " + progress + "%");
            }
        });
    }

    private void resetSubmitButton() {
        submitButton.setEnabled(true);
        submitButton.setText("Submit Sell Request");
    }

    private void clearForm() {
        computerTypeSpinner.setSelection(0);
        yearSpinner.setSelection(0);
        conditionSpinner.setSelection(0);
        modelInput.setText("");
        specificationsInput.setText("");
        contactInput.setText("");
        selectedMedia.clear();
        updateMediaPreviewVisibility();
        mediaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Storage permission required to upload media", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 
package com.app.pccooker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class HelpFragment extends Fragment {

    private EditText searchEditText;
    private ChipGroup chipGroup;
    private LinearLayout faqContainer;
    private RecyclerView faqRecyclerView;
    private List<FAQItem> allFaqs;
    private List<FAQItem> filteredFaqs;
    
    // Contact views
    private TextView whatsapp1, whatsapp2, emailText;
    private TextView copyWhatsapp1, copyWhatsapp2, copyEmail;
    private TextView reportIssueButton;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        initializeViews(view);
        setupFAQData();
        setupSearchFunctionality();
        setupChipFilters();
        setupContactActions();
        setupReportIssue();

        return view;
    }

    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        chipGroup = view.findViewById(R.id.chipGroup);
        faqContainer = view.findViewById(R.id.faqContainer);
        
        // Contact views
        whatsapp1 = view.findViewById(R.id.whatsapp1);
        whatsapp2 = view.findViewById(R.id.whatsapp2);
        emailText = view.findViewById(R.id.emailText);
        copyWhatsapp1 = view.findViewById(R.id.copyWhatsapp1);
        copyWhatsapp2 = view.findViewById(R.id.copyWhatsapp2);
        copyEmail = view.findViewById(R.id.copyEmail);
        reportIssueButton = view.findViewById(R.id.reportIssueButton);
        
        // Create RecyclerView for FAQs
        faqRecyclerView = new RecyclerView(getContext());
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        faqContainer.addView(faqRecyclerView);
    }

    private void setupFAQData() {
        allFaqs = new ArrayList<>();
        allFaqs.add(new FAQItem("How can I build a custom PC in PC Cooker?", 
            "You can select each component manually from categories or use the auto-suggest option based on your budget.", "General"));
        allFaqs.add(new FAQItem("What payment options are available?", 
            "We support UPI, Cards, Net Banking, and Wallets through Razorpay.", "Payments"));
        allFaqs.add(new FAQItem("How do I track my order?", 
            "After placing an order, you can track it from the 'Orders' section in the bottom navigation.", "Orders"));
        allFaqs.add(new FAQItem("How do I claim warranty?", 
            "Please contact us via email with your Order Bill and Component Photos. Warranty is 1 year from delivery date (except CPUs/GPUs used for crypto mining).", "Warranty"));
        allFaqs.add(new FAQItem("Can I cancel my order?", 
            "Yes, you can cancel before it is shipped. Refund will be processed within 5-7 working days.", "Orders"));
        allFaqs.add(new FAQItem("Do you ship to my location?", 
            "We currently ship to all major cities in India. Check your address during checkout.", "Orders"));
        allFaqs.add(new FAQItem("What if my component arrives damaged?", 
            "Take photos immediately and contact us within 24 hours. We'll arrange replacement or refund.", "Warranty"));
        allFaqs.add(new FAQItem("How long does delivery take?", 
            "Standard delivery: 3-5 business days. Express delivery: 1-2 business days.", "Orders"));

        filteredFaqs = new ArrayList<>(allFaqs);
        displayFAQs();
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFAQs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupChipFilters() {
        String[] categories = {"All", "General", "Orders", "Payments", "Warranty"};
        
        for (String category : categories) {
            Chip chip = new Chip(getContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            
            if (category.equals("All")) {
                chip.setChecked(true);
            }
            
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    filterByCategory(category);
                }
            });
            
            chipGroup.addView(chip);
        }
    }

    private void filterFAQs(String query) {
        filteredFaqs.clear();
        if (query.isEmpty()) {
            filteredFaqs.addAll(allFaqs);
        } else {
            for (FAQItem faq : allFaqs) {
                if (faq.question.toLowerCase().contains(query.toLowerCase()) ||
                    faq.answer.toLowerCase().contains(query.toLowerCase())) {
                    filteredFaqs.add(faq);
                }
            }
        }
        displayFAQs();
    }

    private void filterByCategory(String category) {
        if (category.equals("All")) {
            filteredFaqs = new ArrayList<>(allFaqs);
        } else {
            filteredFaqs.clear();
            for (FAQItem faq : allFaqs) {
                if (faq.category.equals(category)) {
                    filteredFaqs.add(faq);
                }
            }
        }
        displayFAQs();
    }

    private void displayFAQs() {
        FAQAdapter faqAdapter = new FAQAdapter(filteredFaqs);
        faqRecyclerView.setAdapter(faqAdapter);
    }

    private void setupContactActions() {
        // WhatsApp 1
        whatsapp1.setOnClickListener(v -> openWhatsApp("+917897892656"));
        copyWhatsapp1.setOnClickListener(v -> copyToClipboard("+917897892656", "WhatsApp number copied!"));
        
        // WhatsApp 2
        whatsapp2.setOnClickListener(v -> openWhatsApp("+917989592441"));
        copyWhatsapp2.setOnClickListener(v -> copyToClipboard("+917989592441", "WhatsApp number copied!"));
        
        // Email
        emailText.setOnClickListener(v -> openEmail());
        copyEmail.setOnClickListener(v -> copyToClipboard("help.pccooker@gmail.com", "Email copied!"));
    }

    private void setupReportIssue() {
        reportIssueButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:help.pccooker@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PC Cooker - Issue Report");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI found an issue in PC Cooker app:\n\nIssue Description:\n\nDevice Info:\nAndroid Version: " + android.os.Build.VERSION.RELEASE + "\nApp Version: 1.0.0\n\nPlease fix this issue.\n\nThank you!");
            startActivity(Intent.createChooser(emailIntent, "Report Issue via Email"));
        });
    }

    private void openWhatsApp(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/" + phoneNumber + "?text=Hello, I need help with PC Cooker"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:help.pccooker@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PC Cooker Support Request");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI need help with:\n\n");
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    private void copyToClipboard(String text, String message) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // FAQ Item class
    private static class FAQItem {
        String question;
        String answer;
        String category;

        FAQItem(String question, String answer, String category) {
            this.question = question;
            this.answer = answer;
            this.category = category;
        }
    }

    // FAQ Adapter
    private class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {
        private List<FAQItem> faqItems;

        FAQAdapter(List<FAQItem> faqItems) {
            this.faqItems = faqItems;
        }

        @NonNull
        @Override
        public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
            return new FAQViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
            FAQItem item = faqItems.get(position);
            holder.questionText.setText(item.question);
            holder.answerText.setText(item.answer);
            
            // Initially hide answer
            holder.answerText.setVisibility(View.GONE);
            holder.expandIcon.setImageResource(R.drawable.ic_expand_more);
            
            holder.itemView.setOnClickListener(v -> {
                if (holder.answerText.getVisibility() == View.VISIBLE) {
                    holder.answerText.setVisibility(View.GONE);
                    holder.expandIcon.setImageResource(R.drawable.ic_expand_more);
                } else {
                    holder.answerText.setVisibility(View.VISIBLE);
                    holder.expandIcon.setImageResource(R.drawable.ic_expand_less);
                }
            });
        }

        @Override
        public int getItemCount() {
            return faqItems.size();
        }

        class FAQViewHolder extends RecyclerView.ViewHolder {
            TextView questionText;
            TextView answerText;
            ImageView expandIcon;

            FAQViewHolder(@NonNull View itemView) {
                super(itemView);
                questionText = itemView.findViewById(R.id.questionText);
                answerText = itemView.findViewById(R.id.answerText);
                expandIcon = itemView.findViewById(R.id.expandIcon);
            }
        }
    }
}

package com.app.pccooker;

import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;

public class HelpFragment extends Fragment {

    private RecyclerView faqRecyclerView;
    private Button contactSupportButton;
    private Button emailSupportButton;
    private Button callSupportButton;
    private Button liveChatButton;
    private LinearLayout troubleshootingSection;

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
        setupFAQSection();
        setupContactButtons();
        setupTroubleshootingSection();

        return view;
    }

    private void initializeViews(View view) {
        faqRecyclerView = view.findViewById(R.id.faqRecyclerView);
        contactSupportButton = view.findViewById(R.id.contactSupportButton);
        emailSupportButton = view.findViewById(R.id.emailSupportButton);
        callSupportButton = view.findViewById(R.id.callSupportButton);
        liveChatButton = view.findViewById(R.id.liveChatButton);
        troubleshootingSection = view.findViewById(R.id.troubleshootingSection);

        faqRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupFAQSection() {
        List<FAQItem> faqItems = new ArrayList<>();
        faqItems.add(new FAQItem("How do I build a PC?", 
            "Go to the 'Build PC' section, set your budget, and select components category by category. Our AI will help you choose compatible parts."));
        faqItems.add(new FAQItem("How do I add items to cart?", 
            "Browse components and tap the 'Add to Cart' button. You can also use our AI assistant to get recommendations."));
        faqItems.add(new FAQItem("What payment methods do you accept?", 
            "We currently support Cashfree payments. More payment options coming soon!"));
        faqItems.add(new FAQItem("How do I track my order?", 
            "Go to your profile and check the 'Order History' section to track your orders."));
        faqItems.add(new FAQItem("Can I return or exchange components?", 
            "Yes, we offer a 7-day return policy for unused components in original packaging."));
        faqItems.add(new FAQItem("Do you provide warranty?", 
            "All components come with manufacturer warranty. We also provide additional support."));
        faqItems.add(new FAQItem("How do I contact customer support?", 
            "Use the contact buttons below or email us at support@pccooker.com"));
        faqItems.add(new FAQItem("Do you ship to my location?", 
            "We currently ship to all major cities in India. Check your address during checkout."));

        FAQAdapter faqAdapter = new FAQAdapter(faqItems);
        faqRecyclerView.setAdapter(faqAdapter);
    }

    private void setupContactButtons() {
        contactSupportButton.setOnClickListener(v -> {
            // Open contact form or support page
            Toast.makeText(getContext(), "Opening support form...", Toast.LENGTH_SHORT).show();
        });

        emailSupportButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@pccooker.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PC Cooker Support Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI need help with:\n\n");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        callSupportButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+91-1800-PC-COOKER"));
            startActivity(callIntent);
        });

        liveChatButton.setOnClickListener(v -> {
            // Open live chat or WhatsApp
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            whatsappIntent.setData(Uri.parse("https://wa.me/919876543210?text=Hello, I need help with PC Cooker"));
            startActivity(whatsappIntent);
        });
    }

    private void setupTroubleshootingSection() {
        // Add common troubleshooting tips
        addTroubleshootingTip("App not loading?", "Check your internet connection and try restarting the app.");
        addTroubleshootingTip("Can't add to cart?", "Make sure you're logged in and try refreshing the page.");
        addTroubleshootingTip("Payment failed?", "Check your payment details and ensure sufficient balance.");
        addTroubleshootingTip("Order not showing?", "Refresh the order history or contact support if it's been more than 24 hours.");
    }

    private void addTroubleshootingTip(String problem, String solution) {
        LinearLayout tipLayout = new LinearLayout(getContext());
        tipLayout.setOrientation(LinearLayout.VERTICAL);
        tipLayout.setPadding(16, 8, 16, 8);

        TextView problemText = new TextView(getContext());
        problemText.setText(problem);
        problemText.setTextSize(16);
        problemText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        problemText.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView solutionText = new TextView(getContext());
        solutionText.setText(solution);
        solutionText.setTextSize(14);
        solutionText.setTextColor(getResources().getColor(android.R.color.black));
        solutionText.setPadding(0, 4, 0, 0);

        tipLayout.addView(problemText);
        tipLayout.addView(solutionText);
        troubleshootingSection.addView(tipLayout);
    }

    // FAQ Item class
    private static class FAQItem {
        String question;
        String answer;

        FAQItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
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
            
            holder.itemView.setOnClickListener(v -> {
                if (holder.answerText.getVisibility() == View.VISIBLE) {
                    holder.answerText.setVisibility(View.GONE);
                } else {
                    holder.answerText.setVisibility(View.VISIBLE);
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

            FAQViewHolder(@NonNull View itemView) {
                super(itemView);
                questionText = itemView.findViewById(R.id.questionText);
                answerText = itemView.findViewById(R.id.answerText);
            }
        }
    }
}

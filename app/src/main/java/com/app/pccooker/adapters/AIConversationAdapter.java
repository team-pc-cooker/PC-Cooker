package com.app.pccooker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.pccooker.R;
import com.app.pccooker.models.ChatMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AIConversationAdapter extends RecyclerView.Adapter<AIConversationAdapter.MessageViewHolder> {
    
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;
    
    private List<ChatMessage> messages;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    public AIConversationAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }
    
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        return message.isFromUser() ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }
    
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = viewType == VIEW_TYPE_USER ? 
            R.layout.item_message_user : R.layout.item_message_ai;
        
        View view = LayoutInflater.from(parent.getContext())
            .inflate(layoutRes, parent, false);
        return new MessageViewHolder(view, viewType);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }
    
    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
    
    public void clearMessages() {
        int size = messages.size();
        messages.clear();
        notifyItemRangeRemoved(0, size);
    }
    
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private TextView timeText;
        private TextView responseTypeText;
        private int viewType;
        
        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
            responseTypeText = itemView.findViewById(R.id.responseTypeText);
        }
        
        public void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            
            // Format timestamp
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(message.getTimestamp()));
            timeText.setText(time);
            
            // Set response type indicator for AI messages
            if (viewType == VIEW_TYPE_AI && responseTypeText != null) {
                String typeEmoji = getResponseTypeEmoji(message.getResponseType());
                responseTypeText.setText(typeEmoji);
                responseTypeText.setVisibility(View.VISIBLE);
            } else if (responseTypeText != null) {
                responseTypeText.setVisibility(View.GONE);
            }
        }
        
        private String getResponseTypeEmoji(String responseType) {
            if (responseType == null) return "🤖";
            
            switch (responseType) {
                case "build":
                    return "🚀";
                case "suggest":
                    return "💡";
                case "help":
                    return "❓";
                case "error":
                    return "⚠️";
                default:
                    return "🤖";
            }
        }
    }
} 
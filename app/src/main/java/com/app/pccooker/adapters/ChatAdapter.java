package com.app.pccooker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.pccooker.R;
import com.app.pccooker.models.ChatMessage;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;
    
    private List<ChatMessage> chatMessages;
    
    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }
    
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        return message.isFromUser() ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_ai, parent, false);
            return new AIMessageViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof AIMessageViewHolder) {
            ((AIMessageViewHolder) holder).bind(message);
        }
    }
    
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        
        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
        
        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
        }
    }
    
    static class AIMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        
        AIMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
        
        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
        }
    }
} 
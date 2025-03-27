package com.example.ecommerce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    
    private List<ChatMessage> chatMessages;
    private SimpleDateFormat timeFormat;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        this.timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.getType() == ChatMessage.TYPE_USER) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private TextView tvTime;
        private View messageContainer;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            messageContainer = itemView.findViewById(R.id.message_container);
            
            // Apply styling for user message
            messageContainer.setBackgroundResource(R.drawable.bg_user_message);
            // Align to right
            ViewGroup.LayoutParams params = messageContainer.getLayoutParams();
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) params).setMarginStart(100);
                ((ViewGroup.MarginLayoutParams) params).setMarginEnd(8);
            }
        }

        public void bind(ChatMessage message) {
            tvMessage.setText(message.getMessage());
            
            String timeStr = timeFormat.format(new Date(message.getTimestamp()));
            tvTime.setText(timeStr);
        }
    }

    class BotMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private TextView tvTime;
        private View messageContainer;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            messageContainer = itemView.findViewById(R.id.message_container);
            
            // Apply styling for bot message
            messageContainer.setBackgroundResource(R.drawable.bg_bot_message);
            // Align to left
            ViewGroup.LayoutParams params = messageContainer.getLayoutParams();
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) params).setMarginStart(8);
                ((ViewGroup.MarginLayoutParams) params).setMarginEnd(100);
            }
        }

        public void bind(ChatMessage message) {
            tvMessage.setText(message.getMessage());
            
            String timeStr = timeFormat.format(new Date(message.getTimestamp()));
            tvTime.setText(timeStr);
        }
    }
}

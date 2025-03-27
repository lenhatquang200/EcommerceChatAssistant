package com.example.ecommerce;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.adapters.ChatAdapter;
import com.example.ecommerce.api.ClaudeApi;
import com.example.ecommerce.api.ClaudeApiService;
import com.example.ecommerce.db.DatabaseHelper;
import com.example.ecommerce.models.ChatMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private Button btnSend;
    private ProgressBar progressBar;
    
    private DatabaseHelper dbHelper;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private ClaudeApiService claudeApiService;
    
    // Executor service for background tasks
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize Claude API service
        claudeApiService = ClaudeApi.getApiService();
        
        // Initialize executor service
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        rvChat = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progress_bar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Support");

        // Set up RecyclerView
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        
        // Load chat messages from database
        loadChatMessages();
        
        // Add welcome message if chat is empty
        if (chatMessages.isEmpty()) {
            addBotMessage("Hello! I'm your virtual assistant. How can I help you today?");
        }

        // Set up send button
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadChatMessages() {
        // Get chat messages from database
        chatMessages = dbHelper.getChatMessages();
        
        // Set up adapter
        chatAdapter = new ChatAdapter(chatMessages);
        rvChat.setAdapter(chatAdapter);
        
        // Scroll to bottom
        if (!chatMessages.isEmpty()) {
            rvChat.scrollToPosition(chatMessages.size() - 1);
        }
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // Add user message to chat
        addUserMessage(message);
        
        // Clear message input
        etMessage.setText("");
        
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        
        // Get response from Claude API
        getResponseFromClaude(message);
    }

    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setType(ChatMessage.TYPE_USER);
        
        // Add to database
        long id = dbHelper.addChatMessage(chatMessage);
        chatMessage.setId((int) id);
        
        // Add to list and update UI
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChat.scrollToPosition(chatMessages.size() - 1);
    }

    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setType(ChatMessage.TYPE_BOT);
        
        // Add to database
        long id = dbHelper.addChatMessage(chatMessage);
        chatMessage.setId((int) id);
        
        // Add to list and update UI
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChat.scrollToPosition(chatMessages.size() - 1);
    }

    private void getResponseFromClaude(String userMessage) {
        // Create the request for Claude API
        Call<ResponseBody> call = claudeApiService.getClaudeResponse(userMessage);
        
        // Execute the request asynchronously
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Handle the response
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseMessage = response.body().string();
                        // Add bot message to chat
                        addBotMessage(responseMessage);
                    } catch (IOException e) {
                        handleApiError("Error parsing Claude response: " + e.getMessage());
                    }
                } else {
                    handleApiError("Error getting response from Claude: " + response.code());
                }
                
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleApiError("Network error: " + t.getMessage());
                
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void handleApiError(String errorMessage) {
        // Log error
        System.err.println(errorMessage);
        
        // Add fallback bot message
        addBotMessage("I'm having trouble connecting to our servers. Please try again later or contact customer support at support@example.com.");
        
        // Show toast
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown executor service
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

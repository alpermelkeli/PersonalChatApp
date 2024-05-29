package com.alpermelkeli.personalchatapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.alpermelkeli.personalchatapp.databinding.ActivityMainBinding;
import com.alpermelkeli.personalchatapp.databinding.ActivityMessageBinding;

public class MessageActivity extends AppCompatActivity {
    ActivityMessageBinding binding;
    private Handler mainHandler;
    private ChatClient chatClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mainHandler = new Handler(Looper.getMainLooper());

        binding.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatClient = new ChatClient(mainHandler, binding.messageView);
                new Thread(chatClient).start();
            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.messageInput.getText().toString();
                if (chatClient != null && !message.isEmpty()) {
                    chatClient.sendMessage(message);
                    binding.messageInput.setText("");
                }
            }
        });

        binding.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatClient != null) {
                    chatClient.stop();
                }
            }
        });
    }
}

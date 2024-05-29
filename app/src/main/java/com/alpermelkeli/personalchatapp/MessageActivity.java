package com.alpermelkeli.personalchatapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import com.alpermelkeli.personalchatapp.Model.Message;
import com.alpermelkeli.personalchatapp.adapter.MessageAdapter;
import com.alpermelkeli.personalchatapp.databinding.ActivityMessageBinding;
import java.util.ArrayList;
import java.util.List;
public class MessageActivity extends AppCompatActivity {
    ActivityMessageBinding binding;
    private final List<Message> messageList = new ArrayList<>();
    private Handler mainHandler;
    private final MessageAdapter messageAdapter = new MessageAdapter(messageList);

    private ChatClient chatClient;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        recyclerView = binding.recyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(messageAdapter);

        mainHandler = new Handler(Looper.getMainLooper());

        binding.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatClient = new ChatClient(mainHandler, messageList,messageAdapter,recyclerView);
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

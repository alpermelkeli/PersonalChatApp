package com.alpermelkeli.personalchatapp;

import java.io.*;
import java.net.Socket;
import android.os.Handler;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alpermelkeli.personalchatapp.Model.Message;
import com.alpermelkeli.personalchatapp.adapter.MessageAdapter;

import java.util.List;
public class ChatClient implements Runnable {
    private static final String SERVER_ADDRESS = "34.27.179.203"; // Sunucu adresini buraya yaz
    private static final int SERVER_PORT = 5050;
    private Handler mainHandler;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private List<Message> messageList;
    private volatile boolean running = true;
    private PrintWriter out;

    public ChatClient(Handler mainHandler, List<Message> messageList, MessageAdapter messageAdapter, RecyclerView recyclerView) {
        this.mainHandler = mainHandler;
        this.messageList = messageList;
        this.messageAdapter = messageAdapter;
        this.recyclerView = recyclerView;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            new Thread(new Reader(socket)).start();

            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (out != null && running) {
                    out.println(message);
                }
            }
        }).start();
    }

    private class Reader implements Runnable {
        private Socket socket;

        public Reader(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while (running && (message = in.readLine()) != null) {
                    final String finalMessage = message;
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            messageList.add(new Message(finalMessage));
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void stop() {
        running = false;
    }
}

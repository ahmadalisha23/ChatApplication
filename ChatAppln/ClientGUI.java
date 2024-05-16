import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientGUI extends Frame {
    private static final String SERVER_ADDRESS = "192.168.110.206";
    private static final int SERVER_PORT = 8080;
    private TextArea senderTextArea;
    private TextArea receiverTextArea;
    private TextArea connectedUsersTextArea;
    private PrintWriter out;
    private Set<String> connectedUsersSet;

    public ClientGUI() {
        setTitle("Chat Application using Sockets");
        setSize(600, 600);
        setLayout(null);

        // Header Label
        Label headerLabel = new Label("Chat Application using Sockets", Label.CENTER);
        headerLabel.setBounds(0, 30, 600, 30);
        add(headerLabel);

        // Sender's Dialog Box
        Label senderLabel = new Label("Sender's or Your Message:", Label.LEFT);
        senderLabel.setBounds(20, 80, 200, 20);
        add(senderLabel);

        senderTextArea = new TextArea();
        senderTextArea.setBounds(20, 110, 400, 150);
        add(senderTextArea);

        // Send Button (below sender's message)
        Button sendButton = new Button("Send");
        sendButton.setBounds(20, 270, 120, 30);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Action to send message
                String message = senderTextArea.getText();
                sendMessage(message);
                senderTextArea.setText(""); // Clear the sender's text area after sending
            }
        });
        add(sendButton);

        // Receiver's Dialog Box
        Label receiverLabel = new Label("Member's Replies:", Label.LEFT);
        receiverLabel.setBounds(20, 320, 200, 20);
        add(receiverLabel);

        receiverTextArea = new TextArea();
        receiverTextArea.setBounds(20, 350, 400, 150);
        receiverTextArea.setEditable(false); // Read-only
        add(receiverTextArea);

        // Connected Users Dialog Box
        Label connectedUsersLabel = new Label("Connected Users:", Label.LEFT);
        connectedUsersLabel.setBounds(450, 80, 200, 20);
        add(connectedUsersLabel);

        connectedUsersTextArea = new TextArea();
        connectedUsersTextArea.setBounds(450, 110, 120, 250);
        connectedUsersTextArea.setEditable(false); // Read-only
        add(connectedUsersTextArea);

        connectedUsersSet = new HashSet<>();

        // Connect to server
        connectToServer();

        // Listen for connected users
        new Thread(() -> {
            try {
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    String connectedUsers = in.readLine();
                    if (connectedUsers != null) {
                        updateConnectedUsers(connectedUsers);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Set the frame visible
        setVisible(true);
    }

    private void sendMessage(String message) {
        // Action to send message
        out.println(message);
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to the chat server!");
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        receiverTextArea.append(serverResponse + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private void updateConnectedUsers(String users) {
    String[] userMessages = users.split("\n");
    for (String userMessage : userMessages) {
        String[] parts = userMessage.split(":");
        if (parts.length > 0) {
            String name = parts[0].trim();
            if (!connectedUsersSet.contains(name)) {
                connectedUsersSet.add(name);
                connectedUsersTextArea.append(name + "\n");
            }
        }
    }
}





    public static void main(String[] args) {
        new ClientGUI();
    }
}

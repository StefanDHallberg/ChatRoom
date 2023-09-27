package UseCase;

import Entities.Message;
import Entities.TextMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServiceImpl implements ChatService {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatServiceImpl(Socket socket) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
    }

    public void sendNameUpdate(String newName) {
        out.println("NAME_UPDATE " + newName);  // Sends a message to the server to indicate a name change
    }

    public void sendMessage(String content) {
        TextMessage message = new TextMessage(content);
        out.println(message.getContent());
    }

    public Message receiveMessage() {
        try {
            String receivedText = in.readLine();
            if (receivedText.startsWith("SET_TITLE ")) { //this is simply to change title.
                // Handle the special SET_TITLE message
                int clientNumber = Integer.parseInt(receivedText.substring(10));
//                System.out.println("THis is the ReceiveMessage. " + receivedText);
                return new TextMessage("SET_TITLE " + clientNumber);
            } else {
                // First part is always the client number and the rest is the message
                int firstSpaceIndex = receivedText.indexOf(" ");
                if (firstSpaceIndex == -1) {
                    return new TextMessage(receivedText);  // If no space found, return the whole string
                }
                String clientNumber = receivedText.substring(0, firstSpaceIndex);
                String content = receivedText.substring(firstSpaceIndex + 1);
                return new TextMessage(clientNumber + " " + content);  // Reconstruct the message
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

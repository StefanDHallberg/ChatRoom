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
    private int clientNr;

    public ChatServiceImpl(int clientNr) throws IOException {
        this(new Socket("localhost", 8080), clientNr);
    }

    public ChatServiceImpl(Socket socket, int clientNr) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.clientNr = clientNr;
    }

    public void sendMessage(int senderClientNr, String content) {
        TextMessage message = new TextMessage(content, senderClientNr);
        out.println(message.getContent() + " " + message.getSenderClientNr());
    }
    public Message receiveMessage() {
        try {
            String receivedText = in.readLine();
            int spaceIndex = receivedText.indexOf(" ");

            if (spaceIndex == -1) {
                // Handle the case where no space is found
                return new TextMessage(receivedText);
            }

            String content = receivedText.substring(0, spaceIndex);
            int senderClientNr = Integer.parseInt(receivedText.substring(spaceIndex + 1));

            return new TextMessage(content, senderClientNr);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

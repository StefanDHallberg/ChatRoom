package UseCase;

import Entities.ServerMessage;
import Entities.ServerTextMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerChatServiceImpl implements ServerChatService {
    private ConcurrentMap<Socket, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    public void addClient(Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        clientWriters.put(clientSocket, out);
    }

    public void broadcastMessage(ServerMessage message) {
        for (PrintWriter out : clientWriters.values()) {
            out.println(message.getContent() + " " + message.getClientNumber());
        }
    }

    public ServerMessage receiveClientMessage(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String receivedText = in.readLine();
        int spaceIndex = receivedText.indexOf(" ");

        if (spaceIndex == -1) {
            // Handle the case where no space is found
            return new ServerTextMessage(receivedText, -1);  // -1 or some default value for clientNumber
        }

        String content = receivedText.substring(0, spaceIndex);
        int senderClientNr = Integer.parseInt(receivedText.substring(spaceIndex + 1));

        return new ServerTextMessage(content, senderClientNr);
    }
}
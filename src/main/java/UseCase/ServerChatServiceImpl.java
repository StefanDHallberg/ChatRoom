// Du mangler mosce en Javadoc dokumentation her 
package UseCase;

import Entities.Client;
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
    private ConcurrentMap<Socket, Client> clients = new ConcurrentHashMap<>();

    public void addClient(Socket clientSocket, int clientNumber) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        clientWriters.put(clientSocket, out);
        Client client = new Client(clientNumber, "Client " + clientNumber);
        clients.put(clientSocket, client);

        // Send a message to set the client's title
        out.println("SET_TITLE " + clientNumber);
    }

    public void handleNameUpdate(Socket clientSocket, String newName) {
        Client client = clients.get(clientSocket);
        if (client != null) {
            // Remove the "NAME_UPDATE" from the new name
            if (newName.startsWith("NAME_UPDATE ")) {
                newName = newName.substring("NAME_UPDATE ".length());
            }

            client.setClientName(newName);
//            System.out.println("Debug: Updated name for socket: " + clientSocket + ", new name: " + newName); //quick ghetto debugging
        }
    }


    public void broadcastMessage(ServerMessage message) { //broadcast messages to clients.
        Client sender = clients.get(message.getClientSocket());
        String senderName = sender != null ? sender.getClientName() : "Unknown";
        for (PrintWriter out : clientWriters.values()) {
            out.println(senderName + ": " + message.getContent());
        }
    }

    public ServerMessage receiveClientMessage(Socket clientSocket) throws IOException {
        Client client = clients.getOrDefault(clientSocket, new Client(1, "Unknown"));
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String received = in.readLine();
        ServerMessage message = new ServerTextMessage(received, client.getClientNumber(), client.getClientName(), clientSocket);

        if (received.startsWith("NAME_UPDATE")) {
            message.setNameUpdate(true);
//            System.out.println("Debug: Message include name update."); //quick ghetto debugging
        }

        return message;
    }
}

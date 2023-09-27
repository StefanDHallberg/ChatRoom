package Entities;

import java.net.Socket;

public class ServerTextMessage implements ServerMessage {
    private String content;
    private int clientNumber;
    private boolean isNameUpdate;
    private String clientName;
    private Socket clientSocket;

    public ServerTextMessage(String content, int clientNumber, String clientName, Socket clientSocket) {
        this.content = content;
        this.clientNumber = clientNumber;
        this.clientName = clientName;
        this.clientSocket = clientSocket;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public int getClientNumber() {
        return clientNumber;
    }
    public boolean isNameUpdate() {
        return isNameUpdate;
    }
    public void setNameUpdate(boolean nameUpdate) {
        isNameUpdate = nameUpdate;
    }
    @Override
    public Socket getClientSocket() {
        return clientSocket;
    }
}

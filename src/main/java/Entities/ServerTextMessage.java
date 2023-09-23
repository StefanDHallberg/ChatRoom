package Entities;

public class ServerTextMessage implements ServerMessage {
    private String content;
    private int clientNumber;

    public ServerTextMessage(String content, int clientNumber) {
        this.content = content;
        this.clientNumber = clientNumber;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public int getClientNumber() {
        return clientNumber;
    }
}

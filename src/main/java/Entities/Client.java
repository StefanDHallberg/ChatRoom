package Entities;

public class Client {
    private int clientNumber;
    private String clientName;

    public Client(int clientNumber, String clientName) {
        this.clientNumber = clientNumber;
        this.clientName = clientName;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}

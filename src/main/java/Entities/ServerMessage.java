package Entities;

import java.net.Socket;

public interface ServerMessage {
    String getContent();
    int getClientNumber();
    boolean isNameUpdate();
    void setNameUpdate(boolean nameUpdate);

    Socket getClientSocket();
}
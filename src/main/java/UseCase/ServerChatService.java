package UseCase;

import Entities.ServerMessage;

import java.io.IOException;
import java.net.Socket;

public interface ServerChatService {
    void broadcastMessage(ServerMessage message);
    ServerMessage receiveClientMessage(Socket clientSocket) throws IOException;
}
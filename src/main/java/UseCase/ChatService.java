package UseCase;

import Entities.Message;

import java.io.IOException;

public interface ChatService {

    Message receiveMessage() throws IOException;
    void sendMessage(String content);

    void sendNameUpdate(String newName);

}

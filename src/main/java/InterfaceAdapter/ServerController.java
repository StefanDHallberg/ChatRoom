package InterfaceAdapter;

import Entities.ServerMessage;
import UseCase.ServerChatService;
import UseCase.ServerChatServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {
    @FXML private TextArea serverLog;

    public void initialize() {//code that needs to run/initialize after all of its properties have been set. So to check if there's a new connection.
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                ServerChatService chatService = new ServerChatServiceImpl();
                Platform.runLater(() -> serverLog.appendText("Connected Server: " + serverSocket + "\n"));

                while (true) {//to accept client Connection
                    Socket clientSocket = serverSocket.accept();
                    ((ServerChatServiceImpl) chatService).addClient(clientSocket);

                    new Thread(() -> {//to receive and broadcast the messages across all connections.
                        try {
                            while (true) {
                                ServerMessage receivedMessage = chatService.receiveClientMessage(clientSocket);
                                chatService.broadcastMessage(receivedMessage);
                                Platform.runLater(() -> serverLog.appendText("Client " + receivedMessage.getClientNumber() + ": " + receivedMessage.getContent() + "\n"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
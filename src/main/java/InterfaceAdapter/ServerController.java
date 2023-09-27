package InterfaceAdapter;

import Entities.ServerMessage;
import UseCase.ServerChatService;
import UseCase.ServerChatServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.util.Enumeration;

public class ServerController {
    @FXML private TextArea serverLog;
    private boolean isDarkTheme = true;
    private int clientCounter = 0;

    public void initialize() {
        new Thread(() -> {
            try { // Using personal set environment variables for privacy.
//                String serverIp = System.getenv("SERVER_IP");//hard code your ip if you want.
//                String serverPort = System.getenv("SERVER_PORT");//dedicate a port.
//
//                if (serverIp == null || serverPort == null) {
//                    Platform.runLater(() -> serverLog.appendText("Environment variables SERVER_IP and/or SERVER_PORT are not set.\n"));
//                    return;
//                }
//                int port = Integer.parseInt(serverPort);
//                InetAddress inetAddress = InetAddress.getByName(serverIp);
//                ServerSocket serverSocket = new ServerSocket(port, 0, inetAddress);  // Using the environment variable

                String serverIp = getLocalIPv4Address();  // automatically get the local IPv4 address
                String serverPort = "8080";  // Just any open port.

                int port = Integer.parseInt(serverPort);
                InetAddress inetAddress = InetAddress.getByName(serverIp);
                ServerSocket serverSocket = new ServerSocket(port, 0, inetAddress);


                if (serverIp == null || serverPort == null) {
                    Platform.runLater(() -> serverLog.appendText("Failed to get local IP address and/or server port.\n"));
                    return;
                }

                ServerChatService chatService = new ServerChatServiceImpl();
                Platform.runLater(() -> serverLog.appendText("Connected Server: " + serverSocket + "\n"));
                Platform.runLater(() -> {
                    try {
                        serverLog.appendText("Server IP: " + InetAddress.getLocalHost().getHostAddress() + "\n");
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                });
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    int newClientNumber = ++clientCounter;  // Increment the client counter
                    ((ServerChatServiceImpl) chatService).addClient(clientSocket, newClientNumber);  // Use the new client number


                    new Thread(() -> {
                        try {
                            while (true) {
                                ServerMessage receivedMessage = chatService.receiveClientMessage(clientSocket);
                                if (receivedMessage.isNameUpdate()) {
                                    chatService.handleNameUpdate(clientSocket, receivedMessage.getContent());
                                } else {
                                    chatService.broadcastMessage(receivedMessage);
                                }
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
    private String getLocalIPv4Address() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void switchTheme() {
        Scene scene = serverLog.getScene();
        if (isDarkTheme) {
            scene.getStylesheets().remove(getClass().getResource("ServerGUIDark.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("ServerGUI.css").toExternalForm());
            isDarkTheme = false;
        } else {
            scene.getStylesheets().remove(getClass().getResource("ServerGUI.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("ServerGUIDark.css").toExternalForm());
            isDarkTheme = true;
        }
    }

}

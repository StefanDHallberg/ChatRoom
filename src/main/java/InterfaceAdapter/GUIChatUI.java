package InterfaceAdapter;

import Entities.Message;
import UseCase.ChatService;
import UseCase.ChatServiceImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

public class GUIChatUI implements ChatUI {

    @FXML private TextArea chatArea;
    @FXML private TextField inputField;
    @FXML private TextField clientNameField;
    @FXML private Button themeButton;
    private boolean isDarkTheme = false;
    private ChatService chatService;
    private String clientName;

    public void initialize() {//code that needs to run/initialize after all of its properties have been set.
        inputField.setOnAction(e -> sendMessage());
        clientNameField.setOnAction(e -> sendNameUpdate(clientNameField.getText()));

        themeButton.setOnAction(e -> switchTheme());

//            clientNameField.textProperty().addListener((observable, oldValue, newValue) -> { //updates with each char due to observers.
//            sendNameUpdate(newValue);
//        });
    }

    private void sendNameUpdate(String newName) {
        if (chatService != null) {
//            System.out.println("Debug: Sending name update: " + newName); //quick ghetto debugging
            chatService.sendNameUpdate(newName);
            this.clientName = newName; // Updates client name
        }
    }
    private void sendMessage() {
        //Retrieves the text from inputField and stores it in the content variable.
        //Calls the sendMessage() method on the chatService object, passing in clientNr and content as arguments.
        //Clears the inputField for next msg.
        if (chatService == null) {
            System.out.println("Chat service is not initialized yet.");
            return;
        }
        String content = inputField.getText();
        chatService.sendMessage(content);
        inputField.clear();
    }


    public void startClient() {
        System.out.println("Attempting to start client...");
        Socket clientSocket;
        try {
//            String serverIp = System.getenv("SERVER_IP"); //hardcoded system enviromental variable.
//            String serverPort = System.getenv("SERVER_PORT");
            String serverIp = getLocalIPv4Address();  // Automatically get the local IPv4 address
            String serverPort = "8080";  // Port set to server.


            if (serverIp == null || serverPort == null) {
                System.out.println("Environment variables SERVER_IP and/or SERVER_PORT are not set.");
                return;
            }
            int port = Integer.parseInt(serverPort);
            clientSocket = new Socket(serverIp, port); // Use the server's environment variables

            chatService = new ChatServiceImpl(clientSocket);
            System.out.println("Socket created.");
            new Thread(() -> {
                while (true) { // An infinite loop that keeps running to continuously check for new messages.
                    try {
                        Message receivedMessage = chatService.receiveMessage();
                        if (receivedMessage != null) {
                            if (receivedMessage.getContent().startsWith("SET_TITLE ")) {
                                int clientNumber = Integer.parseInt(receivedMessage.getContent().substring(10));//moving the starter index to 10 to avoid SET_TITLE
                                Platform.runLater(() -> {
                                    Stage stage = (Stage) chatArea.getScene().getWindow();
                                    stage.setTitle("Client " + clientNumber);
                                });
                            } else {
                                displayMessage(receivedMessage);
                            }
                        }
                        Thread.sleep(500); // Pauses the thread for 500 milliseconds between each check of new messages again.
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void displayMessage(Message message) {
        String rawContent = message.getContent();
//        System.out.println(rawContent); //ghetto debug.
        String[] parts = rawContent.split(" ", 2); // Split the content into two parts: clientName/number and actual content(message)
        if (parts.length < 2) {
            return; // Invalid message format
        }
        String senderClientNr = parts[0];
        String actualContent = parts[1];
        Platform.runLater(() -> chatArea.appendText(senderClientNr + ": " + actualContent + "\n"));
    }

    public void switchTheme() {
        Scene scene = chatArea.getScene();
        if (isDarkTheme) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("GUIChatUI.css").toExternalForm());
            isDarkTheme = false;
        } else {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("GUIChatUIDARK.css").toExternalForm());
            isDarkTheme = true;
        }

    }
}
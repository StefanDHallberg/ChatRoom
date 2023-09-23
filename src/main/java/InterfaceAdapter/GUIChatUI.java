package InterfaceAdapter;

import Entities.Message;
import Entities.TextMessage;
import UseCase.ChatService;
import UseCase.ChatServiceImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class GUIChatUI extends Application implements ChatUI {

    @FXML private TextArea chatArea;
    @FXML private TextField inputField;
    private ChatService chatService;
    private int clientNr;

    public void setClientNr(int clientNr) {
        this.clientNr = clientNr;
    }

    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUIChatUI.fxml"));
            Parent root = loader.load();
            GUIChatUI controller = loader.getController();
            controller.setClientNr(clientNr);

            Scene scene = new Scene(root, 300, 250);
            primaryStage.setTitle("Client " + clientNr);
            primaryStage.setScene(scene);
            primaryStage.show();

            new Thread(controller::startClient).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {//code that needs to run/initialize after all of its properties have been set.
        inputField.setOnAction(e -> sendMessage());
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
        chatService.sendMessage(clientNr, content);
        inputField.clear();
    }


    public void startClient() {
        System.out.println("Attempting to start client...");
        Socket clientSocket;
        try {//hardcoded host 'n port for now.
            clientSocket = new Socket("localhost", 8080);
            chatService = new ChatServiceImpl(clientSocket, this.clientNr); //new instance of ChatServiceImpl for handling chat logic.
            System.out.println("Socket created.");

            new Thread(() -> {
                while (true) { //An infinite loop that keeps running to continuously check for new messages.
                    try {
                        Message receivedMessage = chatService.receiveMessage(); //Calls receiveMessage() method to receive a new message.
                        if (receivedMessage != null) {//Checks if the received message is not null. If it's not.
                            displayMessage(receivedMessage); //it calls the displayMessage(receivedMessage) method to display the message.
                        }
                        Thread.sleep(500);//Pauses the thread for 500 milliseconds between each check of new messages again.
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayMessage(Message message) { //displays text messages with the appropriate clientnr and content.
        //checks if message is an instance of TextMessage. if it is, execute code.
        //safely cast message to TextMessage and call methods that are defined in the TextMessage class.
        //extensibility for future proofing, different types of Message objects --ImageMessage, AudioMessage, etc.--
        //using instanceof allows code to be more flexible and easier to extend, as I can add more conditions to handle different types of messages.
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String content = textMessage.getContent();
            int senderClientNr = textMessage.getSenderClientNr();
            Platform.runLater(() -> chatArea.appendText("Client " + senderClientNr + ": " + content + "\n"));
        }
    }
}
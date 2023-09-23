package FrameworkNDrivers;

import InterfaceAdapter.GUIChatUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class StartingGUI extends Application { //Start Clients.
    private int clientCount;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {//a simple GUI for starting multiple instances of Clients.
        Button startClientButton = new Button("Start New Client");
        startClientButton.setOnAction(e -> startNewClient());

        VBox layout = new VBox(startClientButton);
        Scene scene = new Scene(layout, 200, 100);

        primaryStage.setTitle("Starting GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

private void startNewClient() {//creates a new instance of the client window with the appropriate count.
    try {
        Stage clientStage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceAdapter/GUIChatUI.fxml"));
        Parent root = loader.load();

        GUIChatUI controller = loader.getController();
        controller.setClientNr(++clientCount);

        Scene scene = new Scene(root, 300, 250);
        clientStage.setTitle("Client " + clientCount);
        clientStage.setScene(scene);
        clientStage.show();

        new Thread(controller::startClient).start();  // Starts the client logic in a new thread
    } catch (IOException e) {
        e.printStackTrace();
    }
}


}

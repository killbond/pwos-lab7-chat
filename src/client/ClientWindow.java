package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import network.TCPConnection;
import network.TCPConnectionListener;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.application.Application;

import java.io.IOException;

public class ClientWindow extends Application implements TCPConnectionListener {

    private static final int PORT = 8189;

    private static final int WIDTH = 600;

    private static final int HEIGHT = 250;

    private final TextArea log = new TextArea();

    private final TextField fieldNickname = new TextField("nickname");

    private final TextField fieldInput = new TextField("message ...");

    private TCPConnection connection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("os-lab7-chat-client");
        primaryStage.setAlwaysOnTop(true);

        log.setEditable(false);
        log.setWrapText(true);

        fieldInput.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                String msg = fieldInput.getText();
                if(msg.equals("")) return;
                fieldInput.setText(null);
                connection.sendString(fieldNickname.getText() + ": " + msg);
            }
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 25, 15, 25));

        grid.add(log, 0, 0, 2, 1);
        grid.add(new Label("Nickname:"), 0, 1);
        grid.add(fieldNickname, 1, 1);
        grid.add(new Label("Your message:"), 0, 2);
        grid.add(fieldInput, 1, 2);

        Scene scene = new Scene(grid, WIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            connection = new TCPConnection(this, "127.0.0.1", PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg) {
        log.appendText(msg + "\n");
        log.positionCaret(log.getText().length());
    }
}

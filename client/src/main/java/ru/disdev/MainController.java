package ru.disdev;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.disdev.commons.transport.Client;

import java.nio.charset.Charset;

public class MainController {

    @FXML
    private ListView<HBox> list;
    @FXML
    private TextArea input;
    @FXML
    private Button sendButton;
    private Client client;
    private Channel channel;

    public void initialize() {
        Stage stage = Main.newChildStage("Connection");
        stage.initModality(Modality.APPLICATION_MODAL);
        Connection connection = new Connection();
        TextField host = new TextField();
        TextField port = new TextField();
        host.setPromptText("Хост");
        port.setPromptText("Порт");
        host.textProperty().bindBidirectional(connection.hostProperty());
        port.textProperty().bindBidirectional(connection.portProperty());
        Button button = new Button("Подключиться");
        button.setOnAction(e -> client = new Client.ClientBuilder()
                .hostName(connection.getHost())
                .port(Integer.parseInt(connection.getPort()))
                .useLE(true)
                .stringCharset(Charset.forName("UTF-16LE"))
                .onConnection(c -> {
                    channel = c;
                    channel.write(connection.getName());
                })
                .build()
                .start());
        VBox vBox = new VBox(host, port, button);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        BorderPane pane = new BorderPane(vBox);
        pane.setPrefWidth(300);
        pane.setPadding(new Insets(10));
        stage.setScene(new Scene(pane));
        stage.setOnCloseRequest(e -> Platform.exit());
        Platform.runLater(stage::showAndWait);
    }
}

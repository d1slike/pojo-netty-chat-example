package ru.disdev;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.disdev.commons.transport.Client;
import ru.disdev.packets.Disconnection;
import ru.disdev.packets.Message;

import java.nio.charset.Charset;

public class MainController {

    private ObservableList<HBox> messages = FXCollections.observableArrayList();

    @FXML
    private ListView<HBox> list;
    @FXML
    private TextArea input;
    @FXML
    private Button sendButton;
    private Client client;
    private Channel channel;
    private Connection connection;
    private Stage connectionModal;

    public void initialize() {
        list.setItems(messages);
        showConnectionModal();
    }

    private void showConnectionModal() {
        Stage stage = Main.newChildStage("Connection");
        connectionModal = stage;
        connection = new Connection();
        sendButton.setOnAction(this::onClickSendMessage);

        TextField host = new TextField();
        TextField port = new TextField();
        TextField name = new TextField();
        host.setPromptText("Хост");
        port.setPromptText("Порт");
        name.setPromptText("Имя");
        host.textProperty().bindBidirectional(connection.hostProperty());
        port.textProperty().bindBidirectional(connection.portProperty());
        name.textProperty().bindBidirectional(connection.nameProperty());

        Button button = new Button("Подключиться");
        button.setOnAction(this::onClickConnection);

        VBox vBox = new VBox(host, port, name, button);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        BorderPane pane = new BorderPane(vBox);
        pane.setPrefWidth(300);
        pane.setPadding(new Insets(10));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(pane));
        stage.setOnCloseRequest(e -> Platform.exit());
        Platform.runLater(stage::showAndWait);
    }

    private void onClickSendMessage(ActionEvent event) {
        if (channel == null || !channel.isActive()) return;
        String text = input.getText();
        if (text == null || text.isEmpty()) return;
        Message message = new Message();
        message.setMessage(text);
        channel.writeAndFlush(message);
        input.setText("");
    }

    private void onClickConnection(ActionEvent event) {
        client = Client.builder()
                .hostName(connection.getHost())
                .port(Integer.parseInt(connection.getPort()))
                .onException((c, t) -> t.printStackTrace())
                .useLE(true)
                .stringCharset(Charset.forName("UTF-16LE"))
                .onConnection(c -> {
                    channel = c;
                    channel.writeAndFlush(connection.getName());
                    Main.getMainStage().setOnCloseRequest(e -> {
                        channel.writeAndFlush(new Disconnection());
                        client.shutdown();
                        Platform.exit();
                    });
                    Platform.runLater(connectionModal::close);
                })
                .build()
                .subscribe(Message.class, (c, m) -> {
                    String message = m.getMessage();
                    String[] split = message.split("%&%", 2);
                    if (split.length == 2) {
                        Text sender = new Text(split[0] + ":");
                        sender.setStyle("-fx-font-weight: bold");
                        Text msg = new Text(split[1]);
                        HBox box = new HBox(10, sender, msg);
                        Platform.runLater(() -> messages.add(box));
                    }
                })
                .start();
    }

}

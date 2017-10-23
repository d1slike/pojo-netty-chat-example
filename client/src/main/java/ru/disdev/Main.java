package ru.disdev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private static Stage mainStage;
    public static final String PROGRAM_NAME = "Chat";

    public static void main(String[] args) {
        launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Pane pane = FXMLLoader.load(getFXMLUrl("/main.fxml"));
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static Stage newChildStage(String name) {
        Stage childStage = new Stage();
        childStage.initOwner(mainStage);
        String title = PROGRAM_NAME;
        if (name != null) {
            title = title + " - " + name;
        }
        childStage.setTitle(title);
        return childStage;
    }

    public static URL getFXMLUrl(String fileName) {
        return Main.class.getResource(fileName);
    }
}

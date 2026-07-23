package com.example.miniproyecto4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static final String HOME_VIEW_FXML = "/com/example/miniproyecto4/Views/HomeView.fxml";
    private static final String APP_ICON = "/com/example/miniproyecto4/Iconos/logo.png";

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource(HOME_VIEW_FXML));
        Scene scene = new Scene(root);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(APP_ICON)));
        primaryStage.setTitle("Battleship - Naval Command");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}

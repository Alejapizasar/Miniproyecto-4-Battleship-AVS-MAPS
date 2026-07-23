package com.example.miniproyecto4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Entry point of the Battleship application. Initializes the JavaFX
 * runtime, loads the home view, configures the main application window,
 * and starts the user interface.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class Main extends Application
{
    private static final String HOME_VIEW_FXML = "/com/example/miniproyecto4/Views/HomeView.fxml";
    private static final String APP_ICON = "/com/example/miniproyecto4/Iconos/logo.png";

    /**
     * Initializes and displays the primary stage of the application.
     * The home view is loaded from its FXML file and configured as
     * the initial scene.
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime.
     * @throws IOException if the home view cannot be loaded.
     */
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

    /**
     * Launches the JavaFX application.
     *
     * @param args the command-line arguments passed to the application.
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
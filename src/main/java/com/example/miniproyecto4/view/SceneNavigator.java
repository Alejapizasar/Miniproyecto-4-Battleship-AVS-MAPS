package com.example.miniproyecto4.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class responsible for navigating between JavaFX scenes.
 * It loads FXML views, replaces the current scene displayed in
 * the application window, and optionally returns the controller
 * associated with the loaded view.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class SceneNavigator
{
    /**
     * Creates a new scene navigator.
     */
    public SceneNavigator()
    {
    }

    /**
     * Loads the specified FXML view and replaces the current scene
     * displayed in the application's stage.
     *
     * @param sourceNode the node that belongs to the current stage.
     * @param fxmlResourcePath the path of the FXML file to load.
     * @param windowTitle the title assigned to the application window.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public void navigateTo(Node sourceNode, String fxmlResourcePath, String windowTitle) throws IOException
    {
        this.navigateToAndGetController(sourceNode, fxmlResourcePath, windowTitle);
    }

    /**
     * Loads the specified FXML view, replaces the current scene,
     * and returns the controller associated with the loaded view.
     *
     * @param sourceNode the node that belongs to the current stage.
     * @param fxmlResourcePath the path of the FXML file to load.
     * @param windowTitle the title assigned to the application window.
     * @return the controller associated with the loaded FXML view.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public Object navigateToAndGetController(Node sourceNode, String fxmlResourcePath, String windowTitle) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(fxmlResourcePath));
        Parent root = loader.load();

        Stage stage = (Stage) sourceNode.getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.setTitle(windowTitle);
        stage.centerOnScreen();

        return loader.getController();
    }
}
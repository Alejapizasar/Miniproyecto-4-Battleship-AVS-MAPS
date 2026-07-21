package com.example.miniproyecto4.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Small collaborator responsible for switching the root of the current
 * Stage to a different FXML view. Kept out of the controllers so they
 * do not depend directly on FXMLLoader/Stage plumbing (low coupling).
 *
 * <p>This is a plain, instantiable class on purpose (each controller
 * creates its own instance) so it does not become a Singleton.</p>
 *
 */
public class SceneNavigator
{
    public SceneNavigator()
    {
    }

    /**
     * Replaces the scene of the window that owns {@code sourceNode}
     * with the view loaded from {@code fxmlResourcePath}.
     *
     * @param sourceNode      any node currently attached to the active Stage
     *                        (usually the button that triggered the navigation)
     * @param fxmlResourcePath classpath-relative path to the target FXML file
     * @param windowTitle      title to set on the Stage after switching
     * @throws IOException if the FXML file cannot be loaded
     */
    public void navigateTo(Node sourceNode, String fxmlResourcePath, String windowTitle) throws IOException
    {
        this.navigateToAndGetController(sourceNode, fxmlResourcePath, windowTitle);
    }

    /**
     * Same as {@link #navigateTo}, but also returns the controller that
     * FXMLLoader created for the destination view. Callers use this when
     * the new screen needs data from the screen that is being left (e.g.
     * PlayerController handing its placed Board over to GameController)
     * instead of reaching for a Singleton or static state.
     *
     * @param sourceNode      any node currently attached to the active Stage
     * @param fxmlResourcePath classpath-relative path to the target FXML file
     * @param windowTitle      title to set on the Stage after switching
     * @return the controller instance bound to {@code fxmlResourcePath}
     * @throws IOException if the FXML file cannot be loaded
     */
    public Object navigateToAndGetController(Node sourceNode, String fxmlResourcePath, String windowTitle)
            throws IOException
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
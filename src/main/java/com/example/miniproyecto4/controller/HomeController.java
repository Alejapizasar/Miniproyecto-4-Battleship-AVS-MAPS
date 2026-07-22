package com.example.miniproyecto4.controller;

import java.io.IOException;

import com.example.miniproyecto4.view.SceneNavigator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for HomeView.fxml. Handles navigation out of the home
 * screen: starting a new game, continuing a saved one, or quitting.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class HomeController
{
    private static final String PLAYER_VIEW_FXML = "/com/example/miniproyecto4/Views/PlayerView.fxml";

    @FXML
    private Button startBtn;

    @FXML
    private Button continueBtn;

    @FXML
    private Button quitBtn;

    private final SceneNavigator sceneNavigator;

    public HomeController()
    {
        this.sceneNavigator = new SceneNavigator();
    }

    @FXML
    private void initialize()
    {
        this.startBtn.setOnAction(event -> this.handleStartNewGame());
        this.continueBtn.setOnAction(event -> this.handleContinueGame());
        this.quitBtn.setOnAction(event -> this.handleQuit());
    }

    private void handleStartNewGame()
    {
        try
        {
            this.sceneNavigator.navigateTo(this.startBtn, PLAYER_VIEW_FXML, "Battleship - Player Setup");
        }
        catch (IOException exception)
        {
            // TODO: replace with a custom checked exception + Alert dialog
            // once the exception-handling module of the project is built.
            exception.printStackTrace();
        }
    }

    private void handleContinueGame()
    {
        // TODO: load a saved game from the serialized file (persistence module).
    }

    private void handleQuit()
    {
        Platform.exit();
    }
}

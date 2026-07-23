package com.example.miniproyecto4.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.example.miniproyecto4.exceptions.PersistenceException;
import com.example.miniproyecto4.persistence.GameStateSerializer;
import com.example.miniproyecto4.persistence.SerializableGameState;
import com.example.miniproyecto4.view.DialogHelper;
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
    private static final String GAME_VIEW_FXML = "/com/example/miniproyecto4/Views/GameView.fxml";

    // Must match the path GameController writes to in persistGameState().
    private static final Path SAVE_FILE_PATH = com.example.miniproyecto4.persistence.SaveLocation.GAME_STATE_FILE;

    @FXML
    private Button startBtn;

    @FXML
    private Button continueBtn;

    @FXML
    private Button quitBtn;

    private final SceneNavigator sceneNavigator;
    private final GameStateSerializer gameStateSerializer;

    public HomeController()
    {
        this.sceneNavigator = new SceneNavigator();
        this.gameStateSerializer = new GameStateSerializer();
    }

    @FXML
    private void initialize()
    {
        this.startBtn.setOnAction(event -> this.handleStartNewGame());
        this.continueBtn.setOnAction(event -> this.handleContinueGame());
        this.quitBtn.setOnAction(event -> this.handleQuit());

        // Nothing to resume if no save file exists yet: disable the button
        // instead of letting the player click into an error.
        this.continueBtn.setDisable(Files.notExists(SAVE_FILE_PATH));
    }

    private void handleStartNewGame()
    {
        try
        {
            this.sceneNavigator.navigateTo(this.startBtn, PLAYER_VIEW_FXML, "Battleship - Player Setup");
        }
        catch (IOException exception)
        {
            DialogHelper.showError("No se pudo iniciar",
                    "No fue posible abrir la pantalla de colocación de barcos.", exception);
        }
    }

    private void handleContinueGame()
    {
        try
        {
            SerializableGameState state = this.gameStateSerializer.load(SAVE_FILE_PATH);

            Object destinationController = this.sceneNavigator.navigateToAndGetController(
                    this.continueBtn, GAME_VIEW_FXML, "Battleship - Partida guardada");

            if (destinationController instanceof GameController gameController)
            {
                gameController.resumeGame(state);
            }
        }
        catch (PersistenceException | IOException exception)
        {
            DialogHelper.showError("No se pudo continuar la partida",
                    "El archivo de guardado (" + SAVE_FILE_PATH + ") no se pudo leer o está dañado.",
                    exception);
        }
    }

    private void handleQuit()
    {
        Platform.exit();
    }
}

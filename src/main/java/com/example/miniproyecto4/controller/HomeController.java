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
 * Controller responsible for managing the application's home screen.
 * This controller handles the navigation to a new game, resumes a
 * previously saved match, or closes the application.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class HomeController
{
    /**
     * Path to the player setup view.
     */
    private static final String PLAYER_VIEW_FXML = "/com/example/miniproyecto4/Views/PlayerView.fxml";

    /**
     * Path to the game view.
     */
    private static final String GAME_VIEW_FXML = "/com/example/miniproyecto4/Views/GameView.fxml";

    /**
     * Path to the serialized game state file.
     */
    private static final Path SAVE_FILE_PATH = com.example.miniproyecto4.persistence.SaveLocation.GAME_STATE_FILE;

    /**
     * Button used to start a new game.
     */
    @FXML
    private Button startBtn;

    /**
     * Button used to continue a previously saved game.
     */
    @FXML
    private Button continueBtn;

    /**
     * Button used to close the application.
     */
    @FXML
    private Button quitBtn;

    /**
     * Utility responsible for scene navigation.
     */
    private final SceneNavigator sceneNavigator;

    /**
     * Component responsible for loading serialized game states.
     */
    private final GameStateSerializer gameStateSerializer;

    /**
     * Creates a new home screen controller.
     */
    public HomeController()
    {
        this.sceneNavigator = new SceneNavigator();
        this.gameStateSerializer = new GameStateSerializer();
    }

    /**
     * Initializes the controller after the FXML components have been loaded.
     * Event handlers are assigned to each button and the continue button
     * is enabled only when a saved game is available.
     */
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

    /**
     * Opens the player setup screen to begin a new match.
     * Displays an error dialog if the destination view cannot be loaded.
     */
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

    /**
     * Restores a previously saved game by loading the serialized game state
     * and passing it to the game controller.
     * Displays an error dialog if the save file cannot be loaded.
     */
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

    /**
     * Closes the JavaFX application.
     */
    private void handleQuit()
    {
        Platform.exit();
    }
}
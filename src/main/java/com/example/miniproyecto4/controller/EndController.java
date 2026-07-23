package com.example.miniproyecto4.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.example.miniproyecto4.model.PlayerData;
import com.example.miniproyecto4.view.DialogHelper;
import com.example.miniproyecto4.view.SceneNavigator;

/**
 * Controller responsible for managing the end game screen.
 * This controller receives the final match result from the game controller,
 * updates the displayed outcome and player statistics, and handles the
 * available navigation options after the match has finished.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class EndController
{
    /**
     * Path to the home screen FXML file.
     */
    private static final String HOME_VIEW_FXML = "/com/example/miniproyecto4/Views/HomeView.fxml";

    /**
     * Path to the player setup screen FXML file.
     */
    private static final String PLAYER_VIEW_FXML = "/com/example/miniproyecto4/Views/PlayerView.fxml";

    /**
     * Label displaying the match result title.
     */
    @FXML
    private Label resultTitleLabel;

    /**
     * Label displaying the result message.
     */
    @FXML
    private Label resultMessageLabel;

    /**
     * Label displaying the total number of shots fired.
     */
    @FXML
    private Label shotsValueLabel;

    /**
     * Label displaying the total number of successful hits.
     */
    @FXML
    private Label hitsValueLabel;

    /**
     * Label displaying the total number of enemy ships sunk.
     */
    @FXML
    private Label sunkValueLabel;

    /**
     * Label displaying the total number of turns played.
     */
    @FXML
    private Label turnsValueLabel;

    /**
     * Button that starts a new game.
     */
    @FXML
    private Button newGameBtn;

    /**
     * Button that returns to the main menu.
     */
    @FXML
    private Button mainMenuBtn;

    /**
     * Button that closes the application.
     */
    @FXML
    private Button quitBtn;

    /**
     * Utility object responsible for scene navigation.
     */
    private final SceneNavigator sceneNavigator;

    /**
     * Creates a new end screen controller.
     */
    public EndController()
    {
        this.sceneNavigator = new SceneNavigator();
    }

    /**
     * Initializes the controller after the FXML components have been loaded.
     * Event handlers are assigned to each available button.
     */
    @FXML
    private void initialize()
    {
        this.newGameBtn.setOnAction(event -> this.handleNewGame());
        this.mainMenuBtn.setOnAction(event -> this.handleMainMenu());
        this.quitBtn.setOnAction(event -> Platform.exit());
    }

    /**
     * Updates the end game screen with the final match result and
     * the player's statistics.
     *
     * @param victory indicates whether the human player won the match.
     * @param data contains the player's final statistics.
     */
    public void setResult(boolean victory, PlayerData data)
    {
        if (victory)
        {
            this.resultTitleLabel.setText("¡VICTORIA!");
            this.resultMessageLabel.setText("¡Hundiste todos los barcos enemigos!");
        }
        else
        {
            this.resultTitleLabel.setText("DERROTA");
            this.resultMessageLabel.setText("La máquina hundió toda tu flota.");
        }

        this.shotsValueLabel.setText(String.valueOf(data.getShotsFired()));
        this.hitsValueLabel.setText(String.valueOf(data.getHits()));
        this.sunkValueLabel.setText(String.valueOf(data.getShipsSunk()));
        this.turnsValueLabel.setText(String.valueOf(data.getShotsFired()));
    }

    /**
     * Opens the player setup screen to start a new game.
     * Displays an error dialog if the requested view cannot be loaded.
     */
    private void handleNewGame()
    {
        try
        {
            this.sceneNavigator.navigateTo(this.newGameBtn, PLAYER_VIEW_FXML, "Battleship - Player Setup");
        }
        catch (IOException exception)
        {
            DialogHelper.showError("No se pudo continuar",
                    "No fue posible abrir la pantalla de colocación de barcos.", exception);
        }
    }

    /**
     * Returns the user to the main menu.
     * Displays an error dialog if the requested view cannot be loaded.
     */
    private void handleMainMenu()
    {
        try
        {
            this.sceneNavigator.navigateTo(this.mainMenuBtn, HOME_VIEW_FXML, "Battleship");
        }
        catch (IOException exception)
        {
            DialogHelper.showError("No se pudo continuar",
                    "No fue posible abrir el menú principal.", exception);
        }
    }
}
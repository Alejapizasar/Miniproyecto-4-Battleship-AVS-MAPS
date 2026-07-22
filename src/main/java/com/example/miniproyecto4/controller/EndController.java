package com.example.miniproyecto4.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.example.miniproyecto4.model.PlayerData;
import com.example.miniproyecto4.view.SceneNavigator;

/**
 * Controller for EndView.fxml. Receives the match result from
 * {@link GameController} right after the scene switch (see
 * {@link #setResult}) and fills in the victory/defeat title, message,
 * and final stats, then wires the three navigation buttons.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class EndController
{
    private static final String HOME_VIEW_FXML = "/com/example/miniproyecto4/Views/HomeView.fxml";
    private static final String PLAYER_VIEW_FXML = "/com/example/miniproyecto4/Views/PlayerView.fxml";

    @FXML
    private Label resultTitleLabel;

    @FXML
    private Label resultMessageLabel;

    @FXML
    private Label shotsValueLabel;

    @FXML
    private Label hitsValueLabel;

    @FXML
    private Label sunkValueLabel;

    @FXML
    private Label turnsValueLabel;

    @FXML
    private Button newGameBtn;

    @FXML
    private Button mainMenuBtn;

    @FXML
    private Button quitBtn;

    private final SceneNavigator sceneNavigator;

    public EndController()
    {
        this.sceneNavigator = new SceneNavigator();
    }

    @FXML
    private void initialize()
    {
        this.newGameBtn.setOnAction(event -> this.handleNewGame());
        this.mainMenuBtn.setOnAction(event -> this.handleMainMenu());
        this.quitBtn.setOnAction(event -> Platform.exit());
    }

    /**
     * Fills in this screen with the outcome of the match that just ended.
     *
     * @param victory true if the human player won, false if they lost
     * @param data    the human player's final stats for this match
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

    private void handleNewGame()
    {
        try
        {
            this.sceneNavigator.navigateTo(this.newGameBtn, PLAYER_VIEW_FXML, "Battleship - Player Setup");
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    private void handleMainMenu()
    {
        try
        {
            this.sceneNavigator.navigateTo(this.mainMenuBtn, HOME_VIEW_FXML, "Battleship");
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }
}

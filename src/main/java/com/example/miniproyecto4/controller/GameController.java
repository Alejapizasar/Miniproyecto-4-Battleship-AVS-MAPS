package com.example.miniproyecto4.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.PlayerData;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.view.BoardCellView;
import com.example.miniproyecto4.view.CellState;

/**
 * Controller for GameView.fxml. It does not build its own fleet: it
 * receives the already-placed {@link Board} from {@link PlayerController}
 * right after the scene switch (see {@link #initializePlayerData}) and
 * draws it on {@code gridUser}, so the ships show up exactly where the
 * player left them on the setup screen.
 *
 * <p>The enemy board ({@code girdIa}) is drawn blank for now; it will be
 * populated once the AI/battle module of the project is built.</p>
 *
 * @author Alejandro Valencia Sandoval
 */
public class GameController
{
    @FXML
    private Button saveGameBtn;

    @FXML
    private Button viewEnemyBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private VBox userPanel;

    @FXML
    private GridPane gridUser;

    @FXML
    private VBox iaPanel;

    @FXML
    private GridPane girdIa;

    @FXML
    private Label infoLabel;

    @FXML
    private Label countshoot;

    @FXML
    private Label impactlabel;

    @FXML
    private Label impactcount;

    @FXML
    private Label failcount;

    @FXML
    private Label shipdestroy;

    private final Map<Coordinate, BoardCellView> playerCellViews;
    private final Map<Coordinate, BoardCellView> enemyCellViews;

    private Board playerBoard;
    private List<Ship> playerFleet;
    private PlayerData playerData;

    public GameController()
    {
        this.playerCellViews = new HashMap<>();
        this.enemyCellViews = new HashMap<>();
    }

    @FXML
    private void initialize()
    {
        this.buildEmptyGrid(this.gridUser, this.playerCellViews);
        this.buildEmptyGrid(this.girdIa, this.enemyCellViews);

        this.exitBtn.setOnAction(event -> Platform.exit());
        this.pauseBtn.setOnAction(event -> this.handlePause());
        this.saveGameBtn.setOnAction(event -> this.handleSaveGame());
        this.viewEnemyBtn.setOnAction(event -> this.handleViewEnemy());
    }

    /**
     * Injected by PlayerController right after navigating here. Draws the
     * player's fleet on {@code gridUser} exactly as it was placed on the
     * setup screen, and creates the {@link PlayerData} that will track
     * this match's stats (shots, hits, misses, ships sunk, winner).
     *
     * @param placedBoard the board the player configured in PlayerController
     * @param fleet       the same fleet instance, already placed
     * @param name        the name typed by the player
     */
    public void initializePlayerData(Board placedBoard, List<Ship> fleet, String name)
    {
        this.playerBoard = placedBoard;
        this.playerFleet = fleet;
        this.playerData = new PlayerData(name);

        this.infoLabel.setText(this.playerData.getName());
        this.refreshStatsLabels();
        this.renderPlayerFleet();
    }

    public PlayerData getPlayerData()
    {
        return this.playerData;
    }

    // Builds a blank SIZE x SIZE grid of water cells, offset by the
    // header row/column already drawn as Labels in the FXML.
    private void buildEmptyGrid(GridPane grid, Map<Coordinate, BoardCellView> cellViews)
    {
        for (int row = 0; row < Board.SIZE; row++)
        {
            for (int column = 0; column < Board.SIZE; column++)
            {
                Coordinate coordinate = new Coordinate(row, column);
                BoardCellView cellView = new BoardCellView();

                cellViews.put(coordinate, cellView);
                grid.add(cellView, column + 1, row + 1);
            }
        }
    }

    // Paints every cell occupied by a placed ship as CellState.SHIP,
    // reusing the same bow/mid/stern segment info PlayerController uses.
    private void renderPlayerFleet()
    {
        if (this.playerBoard == null)
        {
            return;
        }

        for (Ship ship : this.playerBoard.getPlacedShips())
        {
            List<Coordinate> cells = ship.getCells();
            for (int i = 0; i < cells.size(); i++)
            {
                BoardCellView cellView = this.playerCellViews.get(cells.get(i));
                if (cellView != null)
                {
                    cellView.setShipSegmentInfo(ship.getSize(), i, ship.getOrientation());
                    cellView.setState(CellState.SHIP);
                }
            }
        }
    }

    // Reflects the current PlayerData counters on the HUD labels already
    // declared in the FXML. Will be called again every time a shot lands
    // once the AI/battle module drives playerData.registerShot(...).
    private void refreshStatsLabels()
    {
        if (this.playerData == null)
        {
            return;
        }

        this.countshoot.setText(String.valueOf(this.playerData.getShotsFired()));
        this.impactcount.setText(String.valueOf(this.playerData.getHits()));
        this.failcount.setText(String.valueOf(this.playerData.getMisses()));
        this.shipdestroy.setText(String.valueOf(this.playerData.getShipsSunk()));
    }

    private void handlePause()
    {
        // TODO: pause the turn timer/thread once the concurrency module is built.
    }

    private void handleSaveGame()
    {
        // TODO: serialize playerBoard/playerFleet/playerData once the persistence module is built.
    }

    private void handleViewEnemy()
    {
        // TODO: toggle girdIa visibility/reveal once the AI/battle module is built.
    }
}
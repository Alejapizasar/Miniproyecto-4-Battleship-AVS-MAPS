package com.example.miniproyecto4.controller;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import com.example.miniproyecto4.concurrency.GameTimerThread;
import com.example.miniproyecto4.concurrency.MachineTurnThread;
import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.exceptions.PersistenceException;
import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.HumanPlayer;
import com.example.miniproyecto4.model.MachinePlayer;
import com.example.miniproyecto4.model.PlayerData;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.model.ShotResult;
import com.example.miniproyecto4.model.interfaces.Opponent;
import com.example.miniproyecto4.persistence.FlatFilePlayerRepository;
import com.example.miniproyecto4.persistence.GameStateSerializer;
import com.example.miniproyecto4.persistence.SerializableGameState;
import com.example.miniproyecto4.view.BoardCellView;
import com.example.miniproyecto4.view.BoardInteractionAdapter;
import com.example.miniproyecto4.view.CellState;
import com.example.miniproyecto4.view.SceneNavigator;

/**
 * Controller for GameView.fxml. Receives the human's already-placed
 * {@link Board} from {@link PlayerController} (see
 * {@link #initializePlayerData}), builds a random {@link MachinePlayer}
 * opponent, and runs the whole shooting phase: clicks on {@code girdIa}
 * fire at the machine; every human miss hands the turn to a background
 * {@link MachineTurnThread}; a {@link GameTimerThread} keeps a running
 * match clock; "Guardar partida" persists both the flat-file record and
 * the full serialized state.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class GameController
{
    private static final Path SAVE_FILE_PATH = Path.of("battleship_save.dat");
    private static final Path PLAYERS_FILE_PATH = Path.of("players.txt");

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
    private final GameStateSerializer gameStateSerializer;
    private final SceneNavigator sceneNavigator;
    private final Object turnLock;

    private Opponent humanOpponent;
    private MachinePlayer machineOpponent;
    private PlayerData playerData;
    private GameTimerThread timerThread;

    private boolean humanTurn;
    private boolean gameOver;
    private boolean enemyFleetRevealed;

    public GameController()
    {
        this.playerCellViews = new HashMap<>();
        this.enemyCellViews = new HashMap<>();
        this.gameStateSerializer = new GameStateSerializer();
        this.sceneNavigator = new SceneNavigator();
        this.turnLock = new Object();
        this.humanTurn = true;
    }

    @FXML
    private void initialize()
    {
        this.buildGrid(this.gridUser, this.playerCellViews, false);
        this.buildGrid(this.girdIa, this.enemyCellViews, true);

        this.exitBtn.setOnAction(event -> this.handleExit());
        this.pauseBtn.setOnAction(event -> this.handlePause());
        this.saveGameBtn.setOnAction(event -> this.handleSaveGame());
        this.viewEnemyBtn.setOnAction(event -> this.handleViewEnemy());
    }

    /**
     * Injected by PlayerController right after navigating here. Draws the
     * human fleet on {@code gridUser}, spins up a random machine opponent,
     * and starts the match clock.
     *
     * @param placedBoard the board the player configured in PlayerController
     * @param fleet       the same fleet instance, already placed
     * @param name        the name typed by the player
     */
    public void initializePlayerData(Board placedBoard, List<Ship> fleet, String name)
    {
        this.humanOpponent = new HumanPlayer(placedBoard, fleet);
        this.machineOpponent = new MachinePlayer();
        this.playerData = new PlayerData(name);

        try
        {
            this.machineOpponent.placeFleetRandomly();
        }
        catch (PlacementException exception)
        {
            // TODO: show an Alert once the exception-handling module is wired
            // into this screen too; RandomPlacementStrategy retries heavily,
            // so in practice this should not happen.
            exception.printStackTrace();
        }

        this.infoLabel.setText(this.playerData.getName());
        this.refreshStatsLabels();
        this.renderFleet(placedBoard, this.playerCellViews);

        this.timerThread = new GameTimerThread(this::onTimerTick);
        this.timerThread.start();
    }

    public PlayerData getPlayerData()
    {
        return this.playerData;
    }

    // ---------- Grid setup ----------

    // Builds a blank SIZE x SIZE grid of water cells, offset by the header
    // row/column already drawn as Labels in the FXML. When interactive is
    // true, every cell is wired through a BoardInteractionAdapter inner
    // class so clicks fire a shot at the machine.
    private void buildGrid(GridPane grid, Map<Coordinate, BoardCellView> cellViews, boolean interactive)
    {
        for (int row = 0; row < Board.SIZE; row++)
        {
            for (int column = 0; column < Board.SIZE; column++)
            {
                Coordinate coordinate = new Coordinate(row, column);
                BoardCellView cellView = new BoardCellView();

                cellViews.put(coordinate, cellView);
                grid.add(cellView, column + 1, row + 1);

                if (interactive)
                {
                    EnemyCellInteractionHandler handler = new EnemyCellInteractionHandler(coordinate);
                    cellView.setOnMouseClicked(event -> handler.onCellClicked(coordinate));
                }
            }
        }
    }

    // Paints every cell occupied by a placed ship as CellState.SHIP, reusing
    // the bow/mid/stern segment info ShipShapeFactory needs.
    private void renderFleet(Board board, Map<Coordinate, BoardCellView> cellViews)
    {
        for (Ship ship : board.getPlacedShips())
        {
            List<Coordinate> cells = ship.getCells();
            for (int index = 0; index < cells.size(); index++)
            {
                BoardCellView cellView = cellViews.get(cells.get(index));
                if (cellView != null)
                {
                    cellView.setShipSegmentInfo(ship.getSize(), index, ship.getOrientation());
                    cellView.setState(CellState.SHIP);
                }
            }
        }
    }

    // ---------- Shooting / turns ----------

    // Fired by EnemyCellInteractionHandler when the human clicks a cell on
    // girdIa. Runs on the JavaFX Application Thread, guarded by the same
    // lock the background machine thread uses so a click cannot land while
    // the machine is mid-turn.
    private void handlePlayerShot(Coordinate coordinate)
    {
        synchronized (this.turnLock)
        {
            if (!this.humanTurn || this.gameOver)
            {
                return;
            }

            try
            {
                ShotResult result = this.machineOpponent.receiveShotAt(coordinate);
                this.applyShotResult(this.machineOpponent.getBoard(), this.enemyCellViews, coordinate, result);

                this.playerData.registerShot(result);
                this.refreshStatsLabels();

                if (this.machineOpponent.hasLost())
                {
                    this.handleVictory();
                }
                else if (result == ShotResult.MISS)
                {
                    this.humanTurn = false;
                    this.startMachineTurn();
                }
                // On HIT/SUNK without finishing the fleet, the human simply
                // shoots again: humanTurn stays true, nothing else to do.
            }
            catch (InvalidShotException exception)
            {
                // TODO: show an Alert - cell already shot or out of range,
                // once the exception-handling module reaches this screen.
            }
        }
    }

    private void startMachineTurn()
    {
        new MachineTurnThread(this.machineOpponent, this.humanOpponent.getBoard(),
                this.turnLock, this::onMachineShotResolved).start();
    }

    // Callback for MachineTurnThread, delivered via Platform.runLater, so
    // this always runs on the JavaFX Application Thread.
    private void onMachineShotResolved(Coordinate coordinate, ShotResult result)
    {
        this.applyShotResult(this.humanOpponent.getBoard(), this.playerCellViews, coordinate, result);

        if (this.humanOpponent.hasLost())
        {
            this.handleDefeat();
            return;
        }

        if (result == ShotResult.MISS)
        {
            synchronized (this.turnLock)
            {
                this.humanTurn = true;
            }
        }
        else
        {
            // Machine hit or sunk a ship without finishing the human fleet:
            // it keeps shooting, one new thread per shot.
            this.startMachineTurn();
        }
    }

    // Shared by both boards: paints a MISS/HIT/SUNK outcome. For SUNK, the
    // whole ship (not just the cell that finished it off) is revealed.
    private void applyShotResult(Board sourceBoard, Map<Coordinate, BoardCellView> cellViews,
                                 Coordinate coordinate, ShotResult result)
    {
        if (result == ShotResult.MISS)
        {
            BoardCellView cellView = cellViews.get(coordinate);
            if (cellView != null)
            {
                cellView.setState(CellState.MISS);
            }
            return;
        }

        Ship ship = sourceBoard.getShipAt(coordinate);
        if (ship == null)
        {
            return;
        }

        if (result == ShotResult.HIT)
        {
            BoardCellView cellView = cellViews.get(coordinate);
            if (cellView != null)
            {
                int index = ship.getCells().indexOf(coordinate);
                cellView.setShipSegmentInfo(ship.getSize(), index, ship.getOrientation());
                cellView.setState(CellState.HIT);
            }
            return;
        }

        // SUNK: reveal every segment of the ship, not just the last hit.
        List<Coordinate> cells = ship.getCells();
        for (int index = 0; index < cells.size(); index++)
        {
            BoardCellView cellView = cellViews.get(cells.get(index));
            if (cellView != null)
            {
                cellView.setShipSegmentInfo(ship.getSize(), index, ship.getOrientation());
                cellView.setState(CellState.SUNK);
            }
        }
    }

    private static final String END_VIEW_FXML = "/com/example/miniproyecto4/Views/EndView.fxml";

    private void handleVictory()
    {
        this.gameOver = true;
        this.stopTimer();
        this.infoLabel.setText(this.playerData.getName() + " - Victoria");
        this.navigateToEndScreen(true);
    }

    private void handleDefeat()
    {
        this.gameOver = true;
        this.stopTimer();
        this.infoLabel.setText(this.playerData.getName() + " - Derrota");
        this.navigateToEndScreen(false);
    }

    private void navigateToEndScreen(boolean victory)
    {
        Platform.runLater(() ->
        {
            try
            {
                Object destinationController = this.sceneNavigator.navigateToAndGetController(
                        this.exitBtn, END_VIEW_FXML, "Battleship - Fin de la partida");

                if (destinationController instanceof EndController endController)
                {
                    endController.setResult(victory, this.playerData);
                }
            }
            catch (java.io.IOException exception)
            {
                exception.printStackTrace();
            }
        });
    }

    // ---------- HUD / timer ----------

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

    // Delivered once per second by GameTimerThread via Platform.runLater.
    private void onTimerTick(long elapsedSeconds)
    {
        if (this.gameOver || this.playerData == null)
        {
            return;
        }
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        this.infoLabel.setText(String.format("%s - %02d:%02d", this.playerData.getName(), minutes, seconds));
    }

    private void stopTimer()
    {
        if (this.timerThread != null)
        {
            this.timerThread.stopTimer();
        }
    }

    // ---------- Buttons ----------

    private void handlePause()
    {
        // TODO: pause/resume the timer thread's display and block shots
        // once a dedicated "paused" overlay is designed for this screen.
    }

    private void handleSaveGame()
    {
        try
        {
            SerializableGameState state = new SerializableGameState(
                    this.humanOpponent.getBoard(), this.machineOpponent.getBoard(), this.playerData);
            this.gameStateSerializer.save(state, SAVE_FILE_PATH);

            FlatFilePlayerRepository repository = new FlatFilePlayerRepository(PLAYERS_FILE_PATH);
            repository.save(this.playerData);
        }
        catch (PersistenceException exception)
        {
            // TODO: show an Alert once the exception-handling module reaches this screen.
            exception.printStackTrace();
        }
    }

    private void handleViewEnemy()
    {
        this.enemyFleetRevealed = !this.enemyFleetRevealed;
        for (Ship ship : this.machineOpponent.getFleet())
        {
            List<Coordinate> cells = ship.getCells();
            for (int index = 0; index < cells.size(); index++)
            {
                Coordinate coordinate = cells.get(index);
                BoardCellView cellView = this.enemyCellViews.get(coordinate);
                if (cellView == null
                        || cellView.getState() == CellState.HIT
                        || cellView.getState() == CellState.SUNK)
                {
                    continue; // Already revealed by a real shot; leave it as is.
                }

                cellView.setShipSegmentInfo(ship.getSize(), index, ship.getOrientation());
                cellView.setState(this.enemyFleetRevealed ? CellState.SHIP : CellState.WATER);
            }
        }
    }

    private void handleExit()
    {
        this.stopTimer();
        Platform.exit();
    }

    // ---------- Inner adapter class ----------

    /**
     * One instance per enemy-board cell. Extends {@link BoardInteractionAdapter}
     * and overrides only {@code onCellClicked}, which is the single event
     * this screen actually cares about; hover feedback can be added later
     * by overriding the other two methods without touching this class's
     * contract. Being a (private, non-static) inner class, it can reach
     * back into {@code GameController.this} directly.
     */
    private class EnemyCellInteractionHandler extends BoardInteractionAdapter
    {
        private final Coordinate coordinate;

        private EnemyCellInteractionHandler(Coordinate coordinate)
        {
            this.coordinate = coordinate;
        }

        @Override
        public void onCellClicked(Coordinate ignoredCoordinate)
        {
            GameController.this.handlePlayerShot(this.coordinate);
        }
    }
}

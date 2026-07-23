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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

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
import com.example.miniproyecto4.view.DialogHelper;
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
    private static final Path SAVE_FILE_PATH = com.example.miniproyecto4.persistence.SaveLocation.GAME_STATE_FILE;
    private static final Path PLAYERS_FILE_PATH = com.example.miniproyecto4.persistence.SaveLocation.PLAYERS_FILE;

    @FXML
    private Button saveGameBtn;

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
    // HU-3: plain, unlimited, no-cost toggle meant purely for the professor
    // to verify the machine's fleet placement (Ctrl+Shift+V), never
    // reachable from any visible button and never for normal gameplay.
    private boolean verificationModeActive;
    private boolean paused;
    private boolean persistenceWarningShown;

    // Single source of truth for "where would ENTER fire", same role
    // cursorCoordinate plays in PlayerController for ship placement.
    // Only the enemy board (girdIa) is navigable; the player's own board
    // is read-only during this phase.
    private Coordinate enemyCursorCoordinate;

    /**
     * Creates the collaborators this controller owns for the lifetime of
     * a match: the cell-view lookup maps for both grids, the persistence
     * helpers, and the shared turn lock. The actual match data (boards,
     * opponents, player stats) is supplied afterward by either
     * initializePlayerData or resumeGame, since FXMLLoader must be able
     * to call this constructor with no arguments.
     */
    public GameController()
    {
        this.playerCellViews = new HashMap<>();
        this.enemyCellViews = new HashMap<>();
        this.gameStateSerializer = new GameStateSerializer();
        this.sceneNavigator = new SceneNavigator();
        this.turnLock = new Object();
        this.humanTurn = true;
        this.enemyCursorCoordinate = new Coordinate(0, 0);
    }

    @FXML
    private void initialize()
    {
        this.buildGrid(this.gridUser, this.playerCellViews, false);
        this.buildGrid(this.girdIa, this.enemyCellViews, true);
        this.girdIa.setFocusTraversable(true);

        this.exitBtn.setOnAction(event -> this.handleExit());
        this.pauseBtn.setOnAction(event -> this.handlePause());
        this.saveGameBtn.setOnAction(event -> this.handleSaveGame());

        // Keyboard shortcuts (arrows to move the firing cursor over
        // girdIa, ENTER to fire) plus the HU-3 professor-only shortcut:
        // attached as soon as the Scene actually exists (not yet during
        // initialize()), same pattern PlayerController uses.
        this.gridUser.sceneProperty().addListener((observable, oldScene, newScene) ->
        {
            if (newScene != null)
            {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);

                // saveGameBtn/pauseBtn/exitBtn are all focusTraversable
                // Buttons, so without an explicit request one of them
                // (not girdIa) would end up with the initial focus and
                // arrow keys/ENTER would silently do nothing. Nested
                // runLater so this wins the race against JavaFX's own
                // default-focus assignment (same fix as PlayerController).
                Platform.runLater(() -> Platform.runLater(() -> this.girdIa.requestFocus()));
                this.updateEnemyCursorHighlight(true);
            }
        });
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
            // RandomPlacementStrategy retries heavily, so this should not
            // normally happen; if it does, the machine has no valid fleet
            // and the match cannot continue, so the player needs to know.
            DialogHelper.showError("No se pudo iniciar la partida",
                    "No fue posible ubicar la flota de la máquina.", exception);
        }

        this.infoLabel.setText(this.playerData.getName());
        this.refreshStatsLabels();
        this.renderFleet(placedBoard, this.playerCellViews);

        this.timerThread = new GameTimerThread(this::onTimerTick);
        this.timerThread.start();
    }

    /**
     * @return the human player's stats for the match currently in progress
     */
    public PlayerData getPlayerData()
    {
        return this.playerData;
    }

    /**
     * Injected by {@link HomeController} instead of {@link #initializePlayerData}
     * when the player chose "Continuar" on the home screen. Rebuilds both
     * opponents from the serialized boards (fleets, hits and misses
     * included) and repaints both grids to match exactly where the match
     * was left off, instead of starting a fresh one.
     *
     * @param state the snapshot loaded by {@link com.example.miniproyecto4.persistence.GameStateSerializer}
     */
    public void resumeGame(com.example.miniproyecto4.persistence.SerializableGameState state)
    {
        Board humanBoard = state.getHumanBoard();
        Board machineBoard = state.getMachineBoard();

        this.playerData = state.getPlayerData();
        this.humanOpponent = new HumanPlayer(humanBoard, humanBoard.getPlacedShips());
        this.machineOpponent = new MachinePlayer(machineBoard, machineBoard.getPlacedShips());
        this.humanTurn = state.isHumanTurn();

        this.infoLabel.setText(this.playerData.getName());
        this.refreshStatsLabels();
        this.renderFleet(humanBoard, this.playerCellViews);
        this.restoreShotMarks(humanBoard, this.playerCellViews);
        this.restoreShotMarks(machineBoard, this.enemyCellViews);

        this.timerThread = new GameTimerThread(this::onTimerTick);
        this.timerThread.start();

        if (!this.humanTurn)
        {
            this.startMachineTurn();
        }
    }

    // Repaints every already-fired-at cell on a restored board: MISS if
    // nothing was there, HIT/SUNK depending on whether the ship that owned
    // that cell ended up fully hit. Reuses applyShotResult so the visuals
    // match exactly what a live shot would have produced.
    private void restoreShotMarks(Board board, Map<Coordinate, BoardCellView> cellViews)
    {
        for (Coordinate coordinate : board.getShotCells())
        {
            Ship ship = board.getShipAt(coordinate);
            ShotResult result;
            if (ship == null)
            {
                result = ShotResult.MISS;
            }
            else
            {
                result = ship.isSunk() ? ShotResult.SUNK : ShotResult.HIT;
            }
            this.applyShotResult(board, cellViews, coordinate, result);
        }
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
                    cellView.setOnMouseEntered(event -> this.moveEnemyCursorTo(coordinate));
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
        boolean invalidShot = false;

        synchronized (this.turnLock)
        {
            if (!this.humanTurn || this.gameOver || this.paused)
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
                    return;
                }

                if (result == ShotResult.MISS)
                {
                    this.humanTurn = false;
                }
                // On HIT/SUNK without finishing the fleet, the human simply
                // shoots again: humanTurn stays true, nothing else to do.

                // HU-5: persist after every single move, not just on manual save.
                this.persistGameState();

                if (!this.humanTurn)
                {
                    this.startMachineTurn();
                }
            }
            catch (InvalidShotException exception)
            {
                // Cell already shot at, or out of range. The Alert itself is
                // shown after leaving this synchronized block, so a modal
                // dialog never sits on top of the shared turn lock.
                invalidShot = true;
            }
        }

        if (invalidShot)
        {
            DialogHelper.showWarning("Disparo inválido",
                    "Esa celda ya fue disparada antes, o está fuera del tablero. Elige otra.");
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
            // HU-5: persist after every single move, not just on manual save.
            this.persistGameState();
        }
        else
        {
            this.persistGameState();
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
        this.recordMatchResult();
        this.deleteSavedGame();
        this.navigateToEndScreen(true);
    }

    private void handleDefeat()
    {
        this.gameOver = true;
        this.stopTimer();
        this.infoLabel.setText(this.playerData.getName() + " - Derrota");
        this.recordMatchResult();
        this.deleteSavedGame();
        this.navigateToEndScreen(false);
    }

    // Appends this match's final result (nickname + ships sunk) to the flat
    // player-history file. Called once, when the match actually ends -
    // NOT on every move, so players.txt stays one line per match.
    private void recordMatchResult()
    {
        try
        {
            com.example.miniproyecto4.persistence.SaveLocation.ensureDirectoryExists();
            FlatFilePlayerRepository repository = new FlatFilePlayerRepository(PLAYERS_FILE_PATH);
            repository.save(this.playerData);
        }
        catch (PersistenceException | java.io.IOException exception)
        {
            DialogHelper.showError("No se pudo guardar el resultado",
                    "La partida terminó correctamente, pero no se pudo registrar el resultado "
                            + "en el historial de jugadores (" + PLAYERS_FILE_PATH + ").", exception);
        }
    }

    // A finished match has nothing left to resume, so its serialized save
    // is removed; otherwise "Continuar" on the home screen would try to
    // reload a game that already ended.
    private void deleteSavedGame()
    {
        try
        {
            java.nio.file.Files.deleteIfExists(SAVE_FILE_PATH);
        }
        catch (java.io.IOException exception)
        {
            // Not fatal to this match (already over), but if the stale save
            // survives, "Continuar" on the home screen would try to resume
            // a match that already ended - worth telling the player.
            DialogHelper.showError("Aviso de guardado",
                    "No se pudo eliminar la partida guardada anterior (" + SAVE_FILE_PATH + ").", exception);
        }
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
                DialogHelper.showError("Error al mostrar el resultado",
                        "La partida terminó, pero no se pudo abrir la pantalla de resultados.", exception);
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
        if (this.gameOver)
        {
            return;
        }

        this.paused = !this.paused;

        if (this.paused)
        {
            if (this.timerThread != null)
            {
                this.timerThread.pauseTimer();
            }
            this.pauseBtn.setText("Reanudar");
            this.infoLabel.setText("PAUSADO");
        }
        else
        {
            if (this.timerThread != null)
            {
                this.timerThread.resumeTimer();
            }
            this.pauseBtn.setText("Pausa");
            this.infoLabel.setText(this.playerData.getName());
        }
    }

    // Writes the resumable, serialized snapshot (both boards + stats).
    // Called automatically after every move (HU-5); see handleSaveGame()
    // for the manual "Guardar partida" button, which reports its own
    // outcome instead of relying on this method's throttled warning.
    private void persistGameState()
    {
        try
        {
            this.writeGameState();
        }
        catch (PersistenceException exception)
        {
            // Runs after every single shot (HU-5), so a modal Alert here
            // would interrupt every move if the disk write keeps failing.
            // Warn the player once per match instead of once per shot; the
            // game itself keeps working from memory either way.
            if (!this.persistenceWarningShown)
            {
                this.persistenceWarningShown = true;
                DialogHelper.showError("Guardado automático interrumpido",
                        "No se pudo guardar el progreso de la partida en disco ("
                                + SAVE_FILE_PATH + "). El juego continuará, pero si lo cierras "
                                + "ahora podrías perder el avance no guardado.", exception);
            }
        }
    }

    private void writeGameState() throws PersistenceException
    {
        try
        {
            com.example.miniproyecto4.persistence.SaveLocation.ensureDirectoryExists();
        }
        catch (java.io.IOException exception)
        {
            throw new PersistenceException(
                    "Could not create the save folder (" + SAVE_FILE_PATH.getParent() + ")", exception);
        }
        SerializableGameState state = new SerializableGameState(
                this.humanOpponent.getBoard(), this.machineOpponent.getBoard(),
                this.playerData, this.humanTurn);
        this.gameStateSerializer.save(state, SAVE_FILE_PATH);
    }

    private void handleSaveGame()
    {
        try
        {
            this.writeGameState();
            DialogHelper.showInfo("Partida guardada", "El progreso se guardó correctamente.");
        }
        catch (PersistenceException exception)
        {
            DialogHelper.showError("No se pudo guardar",
                    "No fue posible guardar la partida en disco (" + SAVE_FILE_PATH + ").", exception);
        }
    }

    // HU-3 (verification option from the enunciado): "Esta opción debe estar
    // disponible únicamente para fines de verificación... y no durante el
    // juego normal. El tablero principal del oponente se muestra
    // correctamente con todos los barcos ubicados." Plain toggle, no cost,
    // no time limit, no penalty, and deliberately NOT reachable from any
    // visible button or menu - only VERIFY_BOARD_SHORTCUT triggers it, so it
    // stays invisible during normal play.
    private static final KeyCombination VERIFY_BOARD_SHORTCUT =
            new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);

    private void handleVerifyEnemyBoard()
    {
        this.verificationModeActive = !this.verificationModeActive;
        this.paintEnemyFleet(this.verificationModeActive ? CellState.SHIP : CellState.WATER);
    }

    // Ctrl+Shift+V is the professor-only verification toggle; arrows move
    // the firing cursor over girdIa and ENTER fires at it, same shooting
    // path a mouse click uses (handlePlayerShot), so a keyboard kill on
    // the last ship reaches handleVictory() exactly like a mouse kill
    // does. Rotation (R) does not apply here - the fleet is already
    // placed by the time this screen is shown.
    private void handleKeyPressed(KeyEvent event)
    {
        if (VERIFY_BOARD_SHORTCUT.match(event))
        {
            this.handleVerifyEnemyBoard();
            event.consume();
            return;
        }

        switch (event.getCode())
        {
            case UP:
                this.moveEnemyCursorBy(-1, 0);
                event.consume();
                break;
            case DOWN:
                this.moveEnemyCursorBy(1, 0);
                event.consume();
                break;
            case LEFT:
                this.moveEnemyCursorBy(0, -1);
                event.consume();
                break;
            case RIGHT:
                this.moveEnemyCursorBy(0, 1);
                event.consume();
                break;
            case ENTER:
                this.handlePlayerShot(this.enemyCursorCoordinate);
                event.consume();
                break;
            default:
                break;
        }
    }

    private void moveEnemyCursorBy(int rowDelta, int columnDelta)
    {
        int newRow = Math.max(0, Math.min(Board.SIZE - 1,
                this.enemyCursorCoordinate.getRow() + rowDelta));
        int newColumn = Math.max(0, Math.min(Board.SIZE - 1,
                this.enemyCursorCoordinate.getColumn() + columnDelta));
        this.moveEnemyCursorTo(new Coordinate(newRow, newColumn));
    }

    private void moveEnemyCursorTo(Coordinate coordinate)
    {
        this.updateEnemyCursorHighlight(false);
        this.enemyCursorCoordinate = coordinate;
        this.updateEnemyCursorHighlight(true);
    }

    private void updateEnemyCursorHighlight(boolean focused)
    {
        BoardCellView cellView = this.enemyCellViews.get(this.enemyCursorCoordinate);
        if (cellView != null)
        {
            cellView.setKeyboardFocused(focused);
        }
    }

    // Paints every not-yet-resolved enemy ship segment as either SHIP
    // (reveal) or WATER (hide again), leaving cells the player already
    // hit/sunk for real untouched.
    private void paintEnemyFleet(CellState stateToApply)
    {
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
                cellView.setState(stateToApply);
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
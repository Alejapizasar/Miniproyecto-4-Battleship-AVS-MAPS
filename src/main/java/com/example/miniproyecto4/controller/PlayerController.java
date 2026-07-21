package com.example.miniproyecto4.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Fleet;
import com.example.miniproyecto4.model.Orientation;
import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.strategy.ManualPlacementStrategy;
import com.example.miniproyecto4.strategy.RandomPlacementStrategy;
import com.example.miniproyecto4.model.interfaces.ShipPlacementStrategy;
import com.example.miniproyecto4.view.CellState;
import com.example.miniproyecto4.view.BoardCellView;
import com.example.miniproyecto4.view.SceneNavigator;

/**
 * Controller for PlayerView.fxml. Wires the fleet placement screen:
 * manual placement by clicking playerBoard, random placement via
 * strategy swap, board reset, and rotating the pending orientation.
 *
 * <p>Keyboard shortcuts (R = rotate, arrows = move cursor, SPACE = place,
 * ENTER = continue) are attached as an event filter on the Scene, and are
 * deliberately ignored while {@code namePlayerTextField} has focus so the
 * player can type their name without triggering game actions. ENTER while
 * typing the name simply moves focus to the board instead of typing.</p>
 *
 * @author Alejandro Valencia Sandoval
 */
public class PlayerController
{
    private static final String GAME_VIEW_FXML = "/com/example/miniproyecto4/Views/GameView.fxml";

    @FXML
    private TextField namePlayerTextField;

    @FXML
    private GridPane playerBoard;

    @FXML
    private Button rotateShipBtn;

    @FXML
    private Button clearBoardBtn;

    @FXML
    private Button randomPositionBtn;

    @FXML
    private Button playBtn;

    @FXML
    private Button quitBtn;

    private final Board board;
    private final List<Ship> fleet;
    private final Queue<Ship> pendingShips;
    private final Map<Coordinate, BoardCellView> cellViews;
    private final SceneNavigator sceneNavigator;

    private ShipPlacementStrategy manualStrategy;
    private ShipPlacementStrategy randomStrategy;
    private Orientation currentOrientation;

    // Single source of truth for "where would SPACE place the next ship".
    // Updated both by mouse hover and by arrow-key navigation, so the two
    // input methods never disagree about the current target cell.
    private Coordinate cursorCoordinate;

    public PlayerController()
    {
        this.board = new Board();
        this.fleet = Fleet.standardFleet();
        this.pendingShips = new LinkedList<>(this.fleet);
        this.cellViews = new HashMap<>();
        this.manualStrategy = new ManualPlacementStrategy();
        this.randomStrategy = new RandomPlacementStrategy();
        this.currentOrientation = Orientation.HORIZONTAL;
        this.sceneNavigator = new SceneNavigator();
        this.cursorCoordinate = new Coordinate(0, 0);
    }

    @FXML
    private void initialize()
    {
        this.buildBoardCells();
        this.playerBoard.setFocusTraversable(true);

        this.rotateShipBtn.setOnAction(event -> this.handleRotate());
        this.clearBoardBtn.setOnAction(event -> this.handleClearBoard());
        this.randomPositionBtn.setOnAction(event -> this.handleRandomPlacement());
        this.playBtn.setOnAction(event -> this.handlePlay());
        this.quitBtn.setOnAction(event -> Platform.exit());

        // The Scene does not exist yet during initialize(), so the key
        // filter is attached as soon as playerBoard is actually attached
        // to one (immediately after SceneNavigator swaps the root).
        this.playerBoard.sceneProperty().addListener((observable, oldScene, newScene) ->
        {
            if (newScene != null)
            {
                // Filter (capture phase) so this runs BEFORE the focused
                // control (e.g. the TextField) gets a chance to consume
                // the key event.
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);

                // Default focus otherwise lands on namePlayerTextField
                // (first focusable control), which is exactly why the
                // shortcuts looked "broken" before. Move it to the board.
                Platform.runLater(() -> this.playerBoard.requestFocus());
                this.updateCursorHighlight(true);
            }
        });
    }

    // Fills playerBoard with a BoardCellView per cell and wires clicks/hover.
    private void buildBoardCells()
    {
        for (int row = 0; row < Board.SIZE; row++)
        {
            for (int column = 0; column < Board.SIZE; column++)
            {
                Coordinate coordinate = new Coordinate(row, column);
                BoardCellView cellView = new BoardCellView();

                this.cellViews.put(coordinate, cellView);
                // +1 offsets the header row/column already drawn in the FXML.
                this.playerBoard.add(cellView, column + 1, row + 1);

                cellView.setOnMouseClicked(event -> this.handleCellClicked(coordinate));
                cellView.setOnMouseEntered(event -> this.moveCursorTo(coordinate));
            }
        }
    }

    private void handleCellClicked(Coordinate coordinate)
    {
        this.moveCursorTo(coordinate);
        this.placeShipAt(coordinate);
    }

    private void handleRotate()
    {
        this.currentOrientation = this.currentOrientation == Orientation.HORIZONTAL
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;
    }

    // Routes R / arrows / SPACE / ENTER to the placement actions, unless
    // the player is actively typing their name.
    private void handleKeyPressed(KeyEvent event)
    {
        if (this.namePlayerTextField.isFocused())
        {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER)
            {
                // Treat ENTER in the name field as "done typing", not as
                // "continue to the next screen".
                this.playerBoard.requestFocus();
                event.consume();
            }
            return;
        }

        switch (event.getCode())
        {
            case R:
                this.handleRotate();
                event.consume();
                break;
            case UP:
                this.moveCursorBy(-1, 0);
                event.consume();
                break;
            case DOWN:
                this.moveCursorBy(1, 0);
                event.consume();
                break;
            case LEFT:
                this.moveCursorBy(0, -1);
                event.consume();
                break;
            case RIGHT:
                this.moveCursorBy(0, 1);
                event.consume();
                break;
            case SPACE:
                this.placeShipAt(this.cursorCoordinate);
                event.consume();
                break;
            case ENTER:
                this.handlePlay();
                event.consume();
                break;
            default:
                break;
        }
    }

    private void moveCursorBy(int rowDelta, int columnDelta)
    {
        int newRow = Math.max(0, Math.min(Board.SIZE - 1, this.cursorCoordinate.getRow() + rowDelta));
        int newColumn = Math.max(0, Math.min(Board.SIZE - 1, this.cursorCoordinate.getColumn() + columnDelta));
        this.moveCursorTo(new Coordinate(newRow, newColumn));
    }

    private void moveCursorTo(Coordinate coordinate)
    {
        this.updateCursorHighlight(false);
        this.cursorCoordinate = coordinate;
        this.updateCursorHighlight(true);
    }

    private void updateCursorHighlight(boolean focused)
    {
        BoardCellView cellView = this.cellViews.get(this.cursorCoordinate);
        if (cellView != null)
        {
            cellView.setKeyboardFocused(focused);
        }
    }

    private void placeShipAt(Coordinate coordinate)
    {
        Ship nextShip = this.pendingShips.peek();
        if (nextShip == null)
        {
            return; // Whole fleet already placed.
        }

        try
        {
            this.manualStrategy.placeShip(this.board, nextShip, coordinate, this.currentOrientation);
            this.pendingShips.poll();
            this.refreshCellsFor(nextShip);
            this.updateCursorHighlight(true);
        }
        catch (PlacementException exception)
        {
            // TODO: show an Alert telling the player why it did not fit,
            // once the exception-handling module of the project is built.
        }
    }

    private void handleClearBoard()
    {
        this.board.clear();
        for (Ship ship : this.fleet)
        {
            ship.clearPlacement();
        }
        this.pendingShips.clear();
        this.pendingShips.addAll(this.fleet);
        this.refreshAllCells();
    }

    private void handleRandomPlacement()
    {
        this.handleClearBoard();
        for (Ship ship : this.fleet)
        {
            try
            {
                this.randomStrategy.placeShip(this.board, ship, null, null);
                this.pendingShips.remove(ship);
            }
            catch (PlacementException exception)
            {
                // TODO: show an Alert if the random search fails.
            }
        }
        this.refreshAllCells();
    }

    private void handlePlay()
    {
        if (!this.pendingShips.isEmpty())
        {
            // TODO: show an Alert asking the player to finish placing the fleet.
            return;
        }

        try
        {
            Object destinationController = this.sceneNavigator.navigateToAndGetController(
                    this.playBtn, GAME_VIEW_FXML, "Battleship - Game");

            if (destinationController instanceof GameController gameController)
            {
                gameController.initializePlayerData(
                        this.board, this.fleet, this.namePlayerTextField.getText());
            }
        }
        catch (IOException exception)
        {
            // TODO: replace with a custom checked exception + Alert dialog
            // once the exception-handling module of the project is built.
            exception.printStackTrace();
        }
    }

    private void refreshCellsFor(Ship ship)
    {
        List<Coordinate> cells = ship.getCells();
        for (int i = 0; i < cells.size(); i++)
        {
            BoardCellView cellView = this.cellViews.get(cells.get(i));
            cellView.setShipSegmentInfo(ship.getSize(), i, ship.getOrientation());
            cellView.setState(CellState.SHIP);
        }
    }

    private void refreshAllCells()
    {
        for (BoardCellView cellView : this.cellViews.values())
        {
            cellView.setState(CellState.WATER);
        }
        for (Ship ship : this.board.getPlacedShips())
        {
            this.refreshCellsFor(ship);
        }
        this.updateCursorHighlight(true);
    }
}
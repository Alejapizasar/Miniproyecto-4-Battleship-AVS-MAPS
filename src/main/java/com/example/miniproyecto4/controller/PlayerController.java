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

    // Cell currently under the mouse pointer, kept in sync via
    // setOnMouseEntered on every BoardCellView, so the SPACE key knows
    // where to place the next pending ship without requiring a click.
    private Coordinate hoveredCoordinate;

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
    }

    @FXML
    private void initialize()
    {
        this.buildBoardCells();

        this.rotateShipBtn.setOnAction(event -> this.handleRotate());
        this.clearBoardBtn.setOnAction(event -> this.handleClearBoard());
        this.randomPositionBtn.setOnAction(event -> this.handleRandomPlacement());
        this.playBtn.setOnAction(event -> this.handlePlay());
        this.quitBtn.setOnAction(event -> Platform.exit());

        // The Scene does not exist yet during initialize(), so the key
        // handler is attached as soon as playerBoard is actually attached
        // to one (immediately after SceneNavigator swaps the root).
        this.playerBoard.sceneProperty().addListener((observable, oldScene, newScene) ->
        {
            if (newScene != null)
            {
                newScene.setOnKeyPressed(this::handleKeyPressed);
            }
        });
    }

    // Fills playerBoard with a BoardCellView per cell and wires clicks.
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
                cellView.setOnMouseEntered(event -> this.hoveredCoordinate = coordinate);
            }
        }
    }

    private void handleCellClicked(Coordinate coordinate)
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
        }
        catch (PlacementException exception)
        {
            // TODO: show an Alert telling the player why it did not fit,
            // once the exception-handling module of the project is built.
        }
    }

    private void handleRotate()
    {
        this.currentOrientation = this.currentOrientation == Orientation.HORIZONTAL
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;
    }

    // Routes R / SPACE / ENTER to the same actions the buttons already
    // trigger, so the whole placement flow can be done without a mouse.
    private void handleKeyPressed(KeyEvent event)
    {
        switch (event.getCode())
        {
            case R:
                this.handleRotate();
                event.consume();
                break;
            case SPACE:
                this.handlePlaceAtHoveredCell();
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

    // Places the next pending ship at whatever cell the mouse is
    // currently over, reusing the exact same logic a click would run.
    private void handlePlaceAtHoveredCell()
    {
        if (this.hoveredCoordinate != null)
        {
            this.handleCellClicked(this.hoveredCoordinate);
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
            cellView.setShipSegmentInfo(ship.getSize(), i,ship.getOrientation());
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
    }
}

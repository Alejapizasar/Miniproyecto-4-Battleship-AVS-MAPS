package com.example.miniproyecto4.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

/**
 * Controller for PlayerView.fxml. Wires the fleet placement screen:
 * manual placement by clicking playerBoard, random placement via
 * strategy swap, board reset, and rotating the pending orientation.
 *
 * @author Alejandro Valencia Sandoval
 */
public class PlayerController
{
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

    private ShipPlacementStrategy manualStrategy;
    private ShipPlacementStrategy randomStrategy;
    private Orientation currentOrientation;

    public PlayerController()
    {
        this.board = new Board();
        this.fleet = Fleet.standardFleet();
        this.pendingShips = new LinkedList<>(this.fleet);
        this.cellViews = new HashMap<>();
        this.manualStrategy = new ManualPlacementStrategy();
        this.randomStrategy = new RandomPlacementStrategy();
        this.currentOrientation = Orientation.HORIZONTAL;
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
        // TODO: navigate to GameView.fxml with SceneNavigator once it is wired.
    }

    private void refreshCellsFor(Ship ship)
    {
        for (Coordinate cell : ship.getCells())
        {
            this.cellViews.get(cell).setState(CellState.SHIP);
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

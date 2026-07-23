package com.example.miniproyecto4.view;

import com.example.miniproyecto4.model.Orientation;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

/**
 * Visual representation of a single cell in the Battleship board.
 * Each cell is rendered using JavaFX shapes and updates its
 * appearance according to its current state, allowing it to
 * display water, ships, hits, misses, and sunk ships.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class BoardCellView extends StackPane
{
    /**
     * Default width and height of each board cell.
     */
    private static final double CELL_SIZE = 32.0;

    /**
     * Dark color used for the water background.
     */
    private static final Color WATER_DARK = Color.web("#0B3D5C");

    /**
     * Light color used for the water background.
     */
    private static final Color WATER_LIGHT = Color.web("#2E7DAF");

    /**
     * Color used to represent a missed shot.
     */
    private static final Color MISS_MARK = Color.web("#D9E6EF");

    /**
     * Base color used to indicate a sunk ship.
     */
    private static final Color SUNK_OVERLAY = Color.web("#1A1A1A");

    /**
     * Current visual state of the cell.
     */
    private CellState state;

    /**
     * Size of the ship occupying this cell.
     */
    private int shipSize = 1;

    /**
     * Position of this cell within the ship.
     */
    private int segmentIndex = 0;

    /**
     * Orientation of the ship occupying this cell.
     */
    private Orientation orientation = Orientation.HORIZONTAL;

    /**
     * Creates a new board cell initialized as water.
     */
    public BoardCellView()
    {
        this.state = CellState.WATER;
        this.getStyleClass().add("board-cell");
        this.setPrefSize(CELL_SIZE, CELL_SIZE);
        this.setMinSize(CELL_SIZE, CELL_SIZE);
        this.setAlignment(Pos.CENTER);
        this.render();
    }

    /**
     * Stores the information required to draw the corresponding
     * ship segment inside this cell.
     *
     * @param shipSize the total size of the ship.
     * @param segmentIndex the position of this segment within the ship.
     * @param orientation the orientation of the ship.
     */
    public void setShipSegmentInfo(int shipSize, int segmentIndex, Orientation orientation)
    {
        this.shipSize = shipSize;
        this.segmentIndex = segmentIndex;
        this.orientation = orientation;
    }

    /**
     * Updates the current state of the cell and refreshes
     * its visual representation.
     *
     * @param newState the new state assigned to the cell.
     */
    public void setState(CellState newState)
    {
        this.state = newState;
        this.render();

        boolean resolved = newState == CellState.MISS
                || newState == CellState.HIT
                || newState == CellState.SUNK;
        this.getStyleClass().remove("cell-resolved");
        if (resolved)
        {
            this.getStyleClass().add("cell-resolved");
        }
    }

    /**
     * Returns the current state of the cell.
     *
     * @return the current cell state.
     */
    public CellState getState()
    {
        return this.state;
    }

    /**
     * Enables or disables the visual highlight used to indicate
     * the current keyboard selection.
     *
     * @param focused {@code true} to highlight the cell;
     *                {@code false} to remove the highlight.
     */
    public void setKeyboardFocused(boolean focused)
    {
        if (focused)
        {
            if (!this.getStyleClass().contains("cell-cursor"))
            {
                this.getStyleClass().add("cell-cursor");
            }
        }
        else
        {
            this.getStyleClass().remove("cell-cursor");
        }
    }

    /**
     * Rebuilds the graphical content of the cell according
     * to its current state.
     */
    private void render()
    {
        this.getChildren().clear();
        this.getChildren().add(this.buildWater());
        this.getStyleClass().removeAll("cell-hit", "cell-sunk", "cell-miss");

        switch (this.state)
        {
            case SHIP:
                this.getChildren().add(this.buildShipSegment());
                break;
            case MISS:
                this.getChildren().add(this.buildMissMark());
                this.getStyleClass().add("cell-miss");
                break;
            case HIT:
                this.getChildren().add(this.buildShipSegment());
                this.getChildren().add(this.buildHitBurst());
                this.getStyleClass().add("cell-hit");
                break;
            case SUNK:
                this.getChildren().add(this.buildShipSegment());
                this.getChildren().add(this.buildSunkOverlay());
                this.getStyleClass().add("cell-sunk");
                break;
            case WATER:
            default:
                break;
        }
    }

    /**
     * Creates the water background displayed in the cell.
     *
     * @return a pane containing the water graphics.
     */
    private StackPane buildWater()
    {
        StackPane layer = new StackPane();

        Rectangle background = new Rectangle(CELL_SIZE, CELL_SIZE);
        background.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.9, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, WATER_LIGHT),
                new Stop(1, WATER_DARK)));

        Polyline waveOne = new Polyline(4, 10, 10, 14, 16, 10, 22, 14, 28, 10);
        waveOne.setStroke(Color.web("#8FD0F0", 0.35));
        waveOne.setStrokeWidth(1.2);
        waveOne.setFill(null);

        Polyline waveTwo = new Polyline(4, 20, 10, 24, 16, 20, 22, 24, 28, 20);
        waveTwo.setStroke(Color.web("#8FD0F0", 0.25));
        waveTwo.setStrokeWidth(1.2);
        waveTwo.setFill(null);

        layer.getChildren().addAll(background, waveOne, waveTwo);
        return layer;
    }

    /**
     * Creates the graphical representation of the ship segment
     * occupying this cell.
     *
     * @return the ship segment graphic.
     */
    private javafx.scene.Group buildShipSegment()
    {
        return ShipShapeFactory.buildSegment(this.shipSize, this.segmentIndex, this.orientation);
    }

    /**
     * Creates the visual marker used to represent a missed shot.
     *
     * @return the miss marker graphic.
     */
    private javafx.scene.Group buildMissMark()
    {
        Line lineA = new Line(-8, -8, 8, 8);
        Line lineB = new Line(-8, 8, 8, -8);
        for (Line line : new Line[] { lineA, lineB })
        {
            line.setStroke(MISS_MARK);
            line.setStrokeWidth(2.5);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
        }
        return new javafx.scene.Group(lineA, lineB);
    }

    /**
     * Creates the explosion effect displayed when a ship
     * segment is hit.
     *
     * @return the hit effect graphic.
     */
    private javafx.scene.Group buildHitBurst()
    {
        Circle outer = new Circle(9);
        outer.setFill(new RadialGradient(0, 0, 0.5, 0.5, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FFD65C")),
                new Stop(0.5, Color.web("#F26B2B")),
                new Stop(1, Color.web("#B3241C", 0.0))));

        Circle core = new Circle(4);
        core.setFill(Color.web("#FFF2C4"));

        return new javafx.scene.Group(outer, core);
    }

    /**
     * Creates the visual overlay displayed when a ship
     * has been completely sunk.
     *
     * @return the sunk ship overlay graphic.
     */
    private javafx.scene.Group buildSunkOverlay()
    {
        Rectangle darken = new Rectangle(CELL_SIZE, CELL_SIZE);
        darken.setFill(Color.color(SUNK_OVERLAY.getRed(), SUNK_OVERLAY.getGreen(),
                SUNK_OVERLAY.getBlue(), 0.55));

        Line lineA = new Line(-10, -10, 10, 10);
        Line lineB = new Line(-10, 10, 10, -10);
        for (Line line : new Line[] { lineA, lineB })
        {
            line.setStroke(Color.web("#C62828"));
            line.setStrokeWidth(3);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
        }

        return new javafx.scene.Group(darken, lineA, lineB);
    }
}
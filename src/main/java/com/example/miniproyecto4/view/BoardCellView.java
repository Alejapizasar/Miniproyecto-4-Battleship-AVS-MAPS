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
 * A single cell of a Battleship board, drawn entirely with JavaFX 2D shapes
 * (no images). Each instance is a small {@link StackPane} that can be added
 * directly into a {@code GridPane} column/row, and redrawn on demand by
 * calling {@link #setState(CellState)}.
 *
 * <p>When a cell holds part of a ship, the actual hull graphic is delegated
 * to {@link ShipShapeFactory} so the bow/stern/mid-section look different
 * from each other. Call {@link #setShipSegmentInfo(int, int, Orientation)}
 * once, right before switching the state to {@code SHIP}/{@code HIT}/{@code SUNK}.</p>
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class BoardCellView extends StackPane
{
    private static final double CELL_SIZE = 32.0;

    private static final Color WATER_DARK = Color.web("#0B3D5C");
    private static final Color WATER_LIGHT = Color.web("#2E7DAF");
    private static final Color MISS_MARK = Color.web("#D9E6EF");
    private static final Color SUNK_OVERLAY = Color.web("#1A1A1A");

    private CellState state;

    // Info about the ship this cell belongs to (which segment: bow, mid,
    // stern), only relevant when state is SHIP, HIT or SUNK.
    private int shipSize = 1;
    private int segmentIndex = 0;
    private Orientation orientation = Orientation.HORIZONTAL;

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
     * Registers which ship this cell belongs to, so the correct segment
     * shape (bow / body / stern) gets drawn. Call before {@link #setState}
     * when the new state is SHIP, HIT or SUNK.
     */
    public void setShipSegmentInfo(int shipSize, int segmentIndex, Orientation orientation)
    {
        this.shipSize = shipSize;
        this.segmentIndex = segmentIndex;
        this.orientation = orientation;
    }

    public void setState(CellState newState)
    {
        this.state = newState;
        this.render();
    }

    public CellState getState()
    {
        return this.state;
    }

    /**
     * Toggles the CSS class used to highlight this cell as the current
     * keyboard-navigation cursor during manual ship placement.
     *
     * @param focused true to show the highlight, false to clear it
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

    // Clears the current children and rebuilds the shapes for the current state.
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

    private javafx.scene.Group buildShipSegment()
    {
        return ShipShapeFactory.buildSegment(this.shipSize, this.segmentIndex, this.orientation);
    }

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
